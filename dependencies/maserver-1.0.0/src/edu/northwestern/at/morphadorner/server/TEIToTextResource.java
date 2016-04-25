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

import edu.northwestern.at.utils.*;

import edu.northwestern.at.morphadorner.*;
import edu.northwestern.at.morphadorner.tools.applyxslt.ApplyXSLT;

import org.apache.log4j.*;

/** TEIToText resource.
 */

public class TEIToTextResource extends BaseAdornerServerResource
{
    /** Create a TEIToText resource. */

    public TEIToTextResource()
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
        setName( "TEI to text resource" );
        setDescription( "Extracts text from a TEI XML file." );
    }

    /** Handle Post request.
     *
     *  @param  representation      Input form.
     *
     *  @return     Text of TEI XML file as a file download.
     *
     *  @throws     Exception in case of error.
     */

    @Post("multipart:txt")
    public Representation handlePost( Representation representation )
        throws Exception
    {
        return extractTextFromTEI( representation );
    }

    /** Extract text from TEI.
     *
     *  @param  representation  The input form parameters.
     *
     *  @return                 The output.
     *
     *  @throws Exception   In case of error.
     */

    public Representation extractTextFromTEI( Representation representation )
        throws Exception
    {
                                //  Initialize parameters and results.

        String inputXML     = "";
        String outputText   = "";

        boolean resultsAsAttachedFile   = true;
        Representation result           = null;
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
                    "Invalid parameter format for unadorn request."
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
                        MorphAdornerServerData.getMaxAdornedUploadFileSize()
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
                                //  Determine if we're to return results
                                //  as an attached file.

            resultsAsAttachedFile   =
                getBooleanValue
                (
                    uploader.params.get( QueryParams.RESULTSASATTACHEDFILE ) ,
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
                        "No file from which to extract text provided."
                    );
            }
                                //  Run text extractor on the supplied file.
            else
            {
                try
                {
                                //  Extract the text by running an XSLT
                                //  script on the input file.

                    result  =
                        runTextExtractor( uploader , resultsAsAttachedFile );
                }
                catch ( Exception e )
                {
                    setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                    result  =
                        new StringRepresentation
                        (
                            "Unable to extract text."
                        );
                }
            }
        }
                                //  Return extracted text.

        return result;
    }

    /** Run Text Extractor.
     *
     *  @param  uploader                Uploaded file processor.
     *  @param  resultsAsAttachedFile   Send results as attached file.
     *
     *  @return             Extracted text.
     *
     *  @throws             IOException, IllegalStateException
     */

    public Representation runTextExtractor
    (
        UploadedFileProcessor uploader ,
        boolean resultsAsAttachedFile
    )
        throws IOException, IllegalStateException
    {
                                //  Add output directory parameter.
        String outputDir    =
            MorphAdornerServerData.makeFileName
            (
                uploader.getTemporaryDirectory() ,
                "teitext"
            );

        uploader.addTempDirectory( outputDir );

                                //  Make sure output directory exists.

        new File( outputDir ).mkdirs();

                                //  Get server data directory.

        String ddir = MorphAdornerServerData.dataDirectory;

                                //  Get XSLT script to move notes.

        String teiToTextXSLT    =
            MorphAdornerServerData.makeFileName
            (
                ddir ,
                "tei2text.xsl"
            );
                                //  Invoke XSLT processor.
        ApplyXSLT.main
        (
            new String[]
            {
                outputDir ,
                teiToTextXSLT ,
                uploader.getFullFileName( QueryParams.TEIFILE )
            }
        );
                                //  Get output text file name.

        File fOutputFile    =
            new File
            (
                outputDir ,
                uploader.getFileName( QueryParams.TEIFILE )
            );

        String outputFileName   = fOutputFile.getCanonicalPath();

        uploader.addTempFile( outputFileName );

                                //  Create result representation.

        String contentFileName  =
            FileNameUtils.changeFileExtension( outputFileName , ".txt" );

        contentFileName =
            FileNameUtils.stripPathName( contentFileName );

        Representation result   =
            new DownloadableFileRepresentation
            (
                outputFileName ,
                contentFileName ,
                MediaType.TEXT_PLAIN ,
                true ,
                uploader ,
                resultsAsAttachedFile
            );
                                //  Return output text.
        return result;
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
                "TEI XML file from which to extract text (upload)."
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



