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

import org.xml.sax.SAXException;

import edu.northwestern.at.morphadorner.*;
import edu.northwestern.at.morphadorner.tools.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.sentencemelder.*;

import edu.northwestern.at.utils.*;

/** TEIAdornedToSentences resource: Extract sentence from an adorned TEI file.
 */

public class TEIAdornedToSentencesResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "TEI adorned to sentences resource" );
        setDescription
        (
            "Extract sentences from an adorned TEI XML file."
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
        return extractSentencesFromAdornedTEI( representation );
    }

    /** Extract sentences from an adorned TEI XML file.
     *
     *  @param  representation  The input form parameters.
     *
     *  @return                 The output.
     *
     *  @throws Exception   In case of error.
     */

    public Representation extractSentencesFromAdornedTEI
    (
        Representation representation
    )
        throws Exception
    {
                                //  Initialize parameters and results.

        UploadedFileProcessor uploader  = null;
        boolean resultsAsAttachedFile   = true;
        Representation result           = null;
        boolean mainTextOnly            = false;

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
                    "Invalid parameter format for sentence extraction request."
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
                                //  Determine if we're only returning
                                //  sentences in main text.

            mainTextOnly    =
                getBooleanValue
                (
                    uploader.params.get( QueryParams.MAINTEXTONLY ) ,
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
                        "No file from which to extract sentences provided."
                    );
            }
                                //  Run sentence extracted on the supplied file.
            else
            {
                try
                {
                                //  Extract sentences.

                    result  =
                        extractSentencesFromAdornedTEI
                        (
                            uploader ,
                            resultsAsAttachedFile ,
                            mainTextOnly
                        );
                }
                catch ( Exception e )
                {
                    setStatus( Status.CLIENT_ERROR_BAD_REQUEST );

                    result  =
                        new StringRepresentation
                        (
                            "Error extracting sentences."
                        );
                }
            }
        }
                                //  Return extracted sentences.
        return result;
    }

    /** Run sentence extrator.
     *
     *  @param  uploader                Uploaded file processor.
     *  @param  resultsAsAttachedFile   Send results as attached file.
     *  @param  mainTextOnly            Only extract sentences from
     *                                  main text.
     *
     *  @return             Representation for downloading sentences.
     *
     *  @throws             IOException, IllegalStateException, SAXException
     *
     *  <p>
     *  A sentence is considered to be in main text if all the words in
     *  in the sentence are in main text.
     *  </p>
     */

    public Representation extractSentencesFromAdornedTEI
    (
        UploadedFileProcessor uploader ,
        boolean resultsAsAttachedFile ,
        boolean mainTextOnly
    )
        throws IOException, IllegalStateException, SAXException
    {
                                //  Add output directory parameter.
        String outputDir    =
            MorphAdornerServerData.makeFileName
            (
                uploader.getTemporaryDirectory() ,
                "unadorned"
            );

        uploader.addTempDirectory( outputDir );

                                //  Make sure output directory exists.

        new File( outputDir ).mkdirs();

                                //  Load TEI XML file from which to
                                //  extract sentences.

        AdornedXMLReader xmlReader  =
            new AdornedXMLReader
            (
                uploader.getFullFileName( QueryParams.TEIFILE )
            );

                                //  Extract sentences.

        List<List<ExtendedAdornedWord>> sentences   =
            xmlReader.getSentences();

                                //  Get a sentence melder.

        SentenceMelder melder   = new SentenceMelder();

                                //  Get extracted sentences file name.

        String shortSentencesFileName   =
            FileNameUtils.changeFileExtension
            (
                uploader.getFileName( QueryParams.TEIFILE ) ,
                ".txt"
            );

        File fSentencesFile     =
            new File( outputDir ,  shortSentencesFileName );

        String sentencesFileName    = fSentencesFile.getCanonicalPath();

        uploader.addTempFile( sentencesFileName );

                                //  Open extracted sentences file.

        PrintStream printStream =
            new PrintStream( fSentencesFile , "utf-8" );

                                //  Reconstitute sentences to text format.

        boolean ok  = false;

        for ( int i = 0 ; i < sentences.size() ; i++ )
        {
            List<ExtendedAdornedWord> sentence  = sentences.get( i );

            ok  = true;

            if ( mainTextOnly )
            {
                sentence    = ejectParatext( sentence );

                ok  = ( sentence.size() > 0 );
            }

            if ( ok )
            {
                printStream.println
                (
                    melder.reconstituteSentence( sentence )
                );
            }
        }

        printStream.close();
                                //  Create downloadable file representaion
                                //  for extracted sentences.

        return new DownloadableFileRepresentation
        (
            sentencesFileName ,
            shortSentencesFileName ,
            MediaType.TEXT_PLAIN ,
            true ,
            uploader ,
            resultsAsAttachedFile
        );
    }

    /** Eject paratext from a sentence.
     *
     *  @param  sentence    The sentence.
     *
     *  @return             Sentence with paratext words removed.
     */

    public List<ExtendedAdornedWord> ejectParatext
    (
        List<ExtendedAdornedWord> sentence
    )
    {
        List<ExtendedAdornedWord> result    = ListFactory.createNewList();

        for ( ExtendedAdornedWord adornedWord : sentence )
        {
            if  (   adornedWord.getMainSide() ==
                    ExtendedAdornedWord.MainSide.MAIN
                )
            {
                result.add( adornedWord );
            }
        }

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
                "TEI XML file from which to extract sentences (upload)."
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



