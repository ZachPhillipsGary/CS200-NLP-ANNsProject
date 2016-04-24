package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.*;
import org.restlet.representation.*;
import org.restlet.Request;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import edu.northwestern.at.morphadorner.*;
import edu.northwestern.at.morphadorner.server.logging.MorphAdornerServerLogger;
import edu.northwestern.at.morphadorner.tools.adornedtosimpleteip5.AdornedToSimpleTEIP5;

import edu.northwestern.at.utils.*;

import org.apache.log4j.*;

/** TEIAdorner resource.
 */

public class TEIAdornerResource extends BaseAdornerServerResource
{
    /** Create a TEIAdorner resource. */

    public TEIAdornerResource()
    {
                                //  Turn off content negotation.
                                //  We always return a specific type
                                //  regardless of what's requested.

        setNegotiated( false );
    }

    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "TEI adorner resource" );
        setDescription( "Add morphological adornments to a TEI XML text." );
    }

    /** Handle Post request.
     *
     *  @param      representation      Input form.
     *
     *  @return     Adorned TEI XML file as a file download.
     *
     *  @throws     Exception in case of error.
     */

    @Post("multipart:xml")
    public Representation handlePost( Representation representation )
        throws Exception
    {
                                    //  Add CORS headers.
        addCORSHeaders();
                                    //  Return adorned file or error
                                    //  message.

        return adornTEI( representation );
    }

    /** Adorn (or Readorn) TEI file.
     *
     *  @param  representation  The input form parameters.
     *
     *  @return                 The output.
     *
     *  @throws Exception   In case of error.
     */

    public Representation adornTEI( Representation representation )
        throws Exception
    {
                                //  Initialize parameters and results.

        String configuration            = "";
        Representation result           = null;
        boolean resultsAsAttachedFile   = true;
        boolean useChoiceForReg         = false;

                                //  Create file upload handler.

        UploadedFileProcessor uploader  = null;

                                //  Check that we have a multipart form
                                //  request.  Error if not.

        if  (   !MediaType.MULTIPART_FORM_DATA.equals
                (
                    representation.getMediaType() ,
                    true
                )
            )
        {
            setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

            result  =
                new StringRepresentation
                (
                    "Invalid parameter format for TEI adorner request."
                );
        }
        else
        {
                                //  Get request parameters including
                                //  uploaded file.
            try
            {
                uploader    =
                    new UploadedFileProcessor
                    (
                        getRequest() ,
                        QueryParams.TEIFILE ,
                        MorphAdornerServerData.getMaxUnadornedUploadFileSize()
                    );
            }
            catch ( Exception e )
            {
                setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                result  =
                    new StringRepresentation
                    (
                        "Error unadorning file: " +
                        e.getMessage()
                    );

                return result;
            }
                                //  Get corpus configuration selection.
            configuration   =
                uploader.params.get( QueryParams.CORPUSCONFIG );

                                //  Set default configuration if
                                //  none given.

            if ( configuration.length() == 0 )
            {
                configuration   = "ncf";
            }
                                //  Determine if we're to return results
                                //  as an attached file.

            resultsAsAttachedFile   =
                getBooleanValue
                (
                    uploader.params.get( QueryParams.RESULTSASATTACHEDFILE ) ,
                    false
                );
                                //  Determine if we're to use a choice
                                //  structure instead of a reg= attribute
                                //  for the standard spelling for each
                                //  word.

            useChoiceForReg =
                getBooleanValue
                (
                    uploader.params.get( QueryParams.USECHOICE ) ,
                    false
                );
                                //  If no file was found, report
                                //  an error.

            if ( !uploader.foundFiles )
            {
                setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                result  =
                    new StringRepresentation
                    (
                        "No file to adorn provided."
                    );
            }
                                //  Run morphadorner on the supplied
                                //  file.
            else
            {
                try
                {
                                //  Adorn the the input text.
                    result  =
                        runMorphAdorner
                        (
                            uploader ,
                            configuration ,
                            false ,
                            useChoiceForReg ,
                            resultsAsAttachedFile
                        );
                }
                catch ( Exception e )
                {
                    MorphAdornerServerLogger.logger.log
                    (
                        Level.ERROR ,
                        e
                    );

                    result  =
                        new StringRepresentation
                        (
                            "Error adorning file: " + e.getMessage()
                        );
                }
            }
        }

        return result;
    }

    /** Run MorphAdorner to adorn or readorn a TEI file.
     *
     *  @param  uploader        Uploaded file processor.
     *  @param  configuration   MorphAdorner configuration name.
     *  @param  tokenizeOnly    True to only tokenize the input file.
     *                          False to fully adorn the input file.
     *  @param  useChoiceForReg True to uise choice structure to emit
     *                          standard spelling.
     *                          False to use reg= attribute.
     *  @param  resultsAsAttachedFile   True to return results as
     *                                  attached file.
     *
     *  @return     DownloadableFileRepresentation for the adorned file.
     *
     *  @throws     IOException, IllegalStateException
     */

    public Representation runMorphAdorner
    (
        UploadedFileProcessor uploader ,
        String configuration ,
        boolean tokenizeOnly ,
        boolean useChoiceForReg ,
        boolean resultsAsAttachedFile
    )
        throws IOException, IllegalStateException
    {
                                //  Create MorphAdorner arguments.

        List<String> morphArgs  = new ArrayList<String>();

                                //  Get server data directory.

        String ddir = MorphAdornerServerData.dataDirectory;

                                //  Add configuration settings file parameter.

        morphArgs.add( "-p" );
        morphArgs.add
        (
            MorphAdornerServerData.makeFileName
            (
                ddir ,
                configuration + ".properties"
            )
        );
                                //  Add default settings file parameter.
        morphArgs.add( "-d" );
        morphArgs.add
        (
            MorphAdornerServerData.makeFileName
            (
                ddir ,
                "morphadorner.properties"
            )
        );
                                //  Add output directories parameters.
        String outputDir    =
            MorphAdornerServerData.makeFileName
            (
                uploader.getTemporaryDirectory() ,
                "adorned"
            );

        String tempOutputDir    =
            MorphAdornerServerData.makeFileName
            (
                uploader.getTemporaryDirectory() ,
                "tempAdorned"
            );

        uploader.addTempDirectory( outputDir );
        uploader.addTempDirectory( tempOutputDir );

                                //  Convert parameters list to array.

        String[] morphArgsArray =
            (String[])morphArgs.toArray( new String[ morphArgs.size() ] );

                                //  Set logging for MorphAdorner.
        String logConfig    =
            MorphAdornerServerData.makeFileName
            (
                 ddir ,
                "morphadornerlog.config"
            );
                                //  Set the log directory.
        String logDir   =
            MorphAdornerServerData.makeFileName
            (
                uploader.getTemporaryDirectory() ,
                "log"
            );

        uploader.addTempDirectory( logDir );

                                //  Invoke MorphAdorner.

        MorphAdorner.createAndRunAdorner
        (
            configuration ,
            false ,
            morphArgsArray ,
            logConfig ,
            logDir ,
            tempOutputDir ,
            new String[]{ uploader.getFullFileName( QueryParams.TEIFILE ) } ,
            tokenizeOnly
        );

        String adornedFileName  = "";

                                //  If only tokenizing, we're done.
        if ( tokenizeOnly )
        {
                                //  Get tokenized file name.

            File fTempOutputDir = new File( tempOutputDir );

            File fTokenizedFile =
                new File
                (
                    fTempOutputDir ,
                    uploader.getFileName( QueryParams.TEIFILE )
                );

            adornedFileName     = fTokenizedFile.getCanonicalPath();
        }
        else
        {
                                //  Get args for simple P5 converter.

            List<String> args   = new ArrayList<String>();

            args.add( outputDir );

            args.add
            (
                useChoiceForReg ? "usechoice" : "usereg"
            );

            args.add
            (
                MorphAdornerServerData.makeFileName
                (
                    ddir ,
                    "interpgrp.xml"
                )
            );

            args.add
            (
                MorphAdornerServerData.makeFileName
                (
                    tempOutputDir ,
                    "goodfiles.txt"
                )
            );

            args.add
            (
                MorphAdornerServerData.makeFileName
                (
                    tempOutputDir ,
                    "badfiles.txt"
                )
            );

            args.add
            (
                MorphAdornerServerData.makeFileName
                (
                    tempOutputDir ,
                    uploader.getFileName( QueryParams.TEIFILE )
                )
            );
                                //  Convert parameters list to array.

            String[] argsArray  =
                (String[])args.toArray( new String[ args.size() ] );

                                //  Convert adorned text to simple P5 format.

            AdornedToSimpleTEIP5.main( argsArray );

                                //  Get adorned file name.

            File fOutputDir     = new File( outputDir );

            File fAdornedFile   =
                new File
                (
                    fOutputDir ,
                    uploader.getFileName( QueryParams.TEIFILE )
                );

            adornedFileName     = fAdornedFile.getCanonicalPath();
        }
                                //  Create downloadable file representaion
                                //  for adorned or tokenized XML.

        return new DownloadableFileRepresentation
        (
            adornedFileName ,
            uploader.getFileName( QueryParams.TEIFILE ) ,
            MediaType.APPLICATION_XML ,
            true ,
            uploader ,
            resultsAsAttachedFile
        );
    }

    /** Describe the parameters.
     *
     *  @return     Parameters as list of ParameterInfo objects.
     */

    protected List<ParameterInfo> describeParameters()
    {
                                //  Create empty parameter list as
                                //  we ignore the media= parameter
                                //  for TEI services.  They always
                                //  return a file download.

        List<ParameterInfo> params  = new ArrayList<ParameterInfo>();

        ParameterInfo param =
            new ParameterInfo
            (
                QueryParams.TEIFILE ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "TEI XML file to adorn (upload)."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.CORPUSCONFIG ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY ,
                "Corpus configuration."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.RESULTSASATTACHEDFILE ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "Return result as an attached file."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.USECHOICE ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "Use choice structure to emit standard spelling."
            );

        params.add( param );

        return sortParams( params );
    }
}

/*
Copyright (c) 2012, 2013 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/



