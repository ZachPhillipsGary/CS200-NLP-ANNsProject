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
import edu.northwestern.at.morphadorner.tools.compareadornedfiles.*;

import org.apache.log4j.*;

/** TEIApplyChangesToAdornedFile resource: Apply changes specified in
 *  a change log to an adorned file.
 */

public class TEIApplyChangesToAdornedFileResource
    extends BaseAdornerServerResource
{
    /** Create a TEIApplyChangesToAdornedFileResource resource. */

    public TEIApplyChangesToAdornedFileResource()
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
        setName( "TEI apply changes to an adorned file resource" );
        setDescription
        (
            "Applies changes specified in a change log file to a " +
            "tokenized or adorned TEI XML file."
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

    @Post("multipart:xml")
    public Representation handlePost( Representation representation )
        throws Exception
    {
                                    //  Add CORS headers.
        addCORSHeaders();
                                    //  Return file with changes applied
                                    //  or error message.

        return applyChanges( representation );
    }

    /** Apply changes to an adorned file.
     *
     *  @param  representation  The input form parameters.
     *
     *  @return                 The output.
     *
     *  @throws Exception       In case of error.
     */

    public Representation applyChanges
    (
        Representation representation
    )
        throws Exception
    {
                                //  Initialize parameters and results.

        boolean resultsAsAttachedFile   = true;
        boolean revertChanges           = false;
        UploadedFileProcessor uploader  = null;

        Representation result   = null;

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
                    "Invalid parameter format for apply changes request."
                );
        }
        else
        {
                                //  Get request parameters including
                                //  uploaded files.
            try
            {
                uploader    =
                    new UploadedFileProcessor
                    (
                        getRequest() ,
                        new String[]
                        {
                            QueryParams.ORIGADORNEDFILE ,
                            QueryParams.CHANGELOGFILE
                        } ,
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
                                //  Determine if we're to revert the
                                //  specified changes rather than apply
                                //  them.

            revertChanges   =
                uploader.params.get( QueryParams.REVERTCHANGES ) != null;

                                //  If no file was found, report
                                //  an error.

            if ( !uploader.foundFiles )
            {
                setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                result  =
                    new StringRepresentation
                    (
                        "Files needed to apply changes not provided."
                    );
            }
                                //  Run changer with the supplied files.
            else
            {
                try
                {
                                //  Apply the changes in the specified
                                //  change log XML files to the provided
                                //  TEI XML file.
                    result  =
                        runChanger
                        (
                            uploader ,
                            resultsAsAttachedFile ,
                            revertChanges
                        );
                }
                catch ( Exception e )
                {
                    setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                    result  =
                        new StringRepresentation
                        (
                            "Error applying changes."
                        );
                }
            }
        }
                                //  Return updated TEI XML file.
        return result;
    }

    /** Apply changes from a change log file to an adorned file.
     *
     *  @param  uploader                Uploaded file processor.
     *  @param  resultsAsAttachedFile   Send results as attached file.
     *  @param  revertChanges           Revert specified changes.
     *
     *  @return             Representation for downloading updated XML.
     *
     *  @throws             Exception in case of error.
     */

    public Representation runChanger
    (
        UploadedFileProcessor uploader ,
        boolean resultsAsAttachedFile ,
        boolean revertChanges
    )
        throws Exception
    {
                                //  Add output directory parameter.
        String outputDir    =
            MorphAdornerServerData.makeFileName
            (
                uploader.getTemporaryDirectory() ,
                "updatedadornedfiledir"
            );

        uploader.addTempDirectory( outputDir );

                                //  Get updated adorned file name.

        File fUpdatedAdornedFileName    =
            new File
            (
                outputDir ,
                uploader.getFileName( QueryParams.ORIGADORNEDFILE )
            );

        String updatedAdornedFileName   =
            fUpdatedAdornedFileName.getCanonicalPath();

        uploader.addTempFile( updatedAdornedFileName );

                                //  Make sure output directory exists.

        new File( outputDir ).mkdirs();

                                //  Invoke adorned file updater.
        new UpdateAdornedFile
        (
            revertChanges ,
            uploader.getFullFileName( QueryParams.ORIGADORNEDFILE ) ,
            uploader.getFullFileName( QueryParams.CHANGELOGFILE ) ,
            updatedAdornedFileName ,
            new PrintStream( new NullOutputStream() )
        );
                                //  Create downloadable file representaion
                                //  for updated adorned file.

        return new DownloadableFileRepresentation
        (
            updatedAdornedFileName ,
            uploader.getFileName( QueryParams.ORIGADORNEDFILE ) ,
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
                QueryParams.ORIGADORNEDFILE ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Original adorned TEI XML file (upload)."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.CHANGELOGFILE ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "XML change log file (upload)."
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
                QueryParams.REVERTCHANGES ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "Revert changes."
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



