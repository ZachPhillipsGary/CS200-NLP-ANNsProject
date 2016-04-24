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
import edu.northwestern.at.morphadorner.tools.*;
import edu.northwestern.at.morphadorner.tools.xmltotab.*;

import edu.northwestern.at.utils.*;

/** TEIAdornedToSentences resource: Extract sentence from an adorned TEI file.
 */

public class TEIAdornedToTabularFileResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "TEI adorned to tabular file resource" );
        setDescription
        (
            "Convert TEI adorned XML to tabular file format."
        );
    }

    /** Handle Post request.
     *
     *  @param  representation      Input form.
     *
     *  @return     Unadorned TEI XML file as a file download.
     *
     *  @throws     Exception in case of error.
     */

    @Post("multipart:txt")
    public Representation handlePost( Representation representation )
        throws Exception
    {
        return adornedTEIToTabularFormat( representation );
    }

    /** Convert adorned TEI XML file to tabular format.
     *
     *  @param  representation  The input form parameters.
     *
     *  @return                 The output.
     *
     *  @throws Exception   In case of error.
     */

    public Representation adornedTEIToTabularFormat
    (
        Representation representation
    )
        throws Exception
    {
                                //  Initialize parameters and results.

        String adorned      = "";
        String unadorned    = "";

        UploadedFileProcessor uploader  = null;
        boolean resultsAsAttachedFile   = true;

        Representation result           = null;

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
                    "Invalid parameter format for tabular conversion request."
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
                        "No file provided to convert to tabular format."
                    );
            }
                                //  Run tabular conversion on supplied file.
            else
            {
                try
                {
                                //  Convert adorned TEI XML to tabular
                                //  format.

                    result  =
                        teiToTabularFile( uploader , resultsAsAttachedFile );
                }
                catch ( Exception e )
                {
                    setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                    result  =
                        new StringRepresentation
                        (
                            "Error converting TEI XML to tabular format."
                        );
                }
            }
        }
                                //  Return extracted sentences.
        return result;
    }

    /** Run tabular conversion.
     *
     *  @param  uploader                Uploaded file processor.
     *  @param  resultsAsAttachedFile   True to return results as an
     *                                  attached file.
     *
     *  @return             Representation for downloading tabular file.
     *
     *  @throws             IOException, IllegalStateException, SAXException
     */

    public Representation teiToTabularFile
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
                "tabular"
            );

        uploader.addTempDirectory( outputDir );

                                //  Get tabular file name.

        String shortTabularFileName =
            FileNameUtils.changeFileExtension
            (
                uploader.getFileName( QueryParams.TEIFILE ) ,
                ".tab"
            );

        File fTabularFile       =
            new File( outputDir ,  shortTabularFileName );

        String tabularFileName  = fTabularFile.getCanonicalPath();

        uploader.addTempFile( tabularFileName );

                                //  Make sure output directory exists.

        new File( outputDir ).mkdirs();

                                //  Invoke tabular file converter.
        XMLToTab.main
        (
            new String[]
            {
                uploader.getFullFileName( QueryParams.TEIFILE ) ,
                tabularFileName
            }
        );
                                //  Create downloadable file representaion
                                //  for tabular file.

        return new DownloadableFileRepresentation
        (
            tabularFileName ,
            shortTabularFileName ,
            MediaType.TEXT_PLAIN ,
//          MediaType.TEXT_TSV ,
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
                "TEI XML file to convert to tabular format (upload)."
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



