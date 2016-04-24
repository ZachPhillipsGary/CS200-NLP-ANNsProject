package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.XStream;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.textsegmenter.*;

/** Text segmenter resource.
 */

public class TextSegmenterResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Text segmenter resource" );
        setDescription
        (
            "Segments text using text tiling or C99 algorithm."
        );
    }

    /** Handle Get request.
     *
     *  @return     Segmentation results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query parameters.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Return text segmentation results.
        return
            postResults
            (
                queryParams ,
                segmentText( queryParams ) ,
                "token" ,
                String.class
            );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Segmentation results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query parameters.

        Form queryParams = new Form( representation );

                                //  Return text segmentation results.
        return
            postResults
            (
                queryParams ,
                segmentText( queryParams ) ,
                "token" ,
                String.class
            );
    }

    /** Perform text segmentation.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Text segmentation result.
     */

    public TextSegmenterResult segmentText( Form queryParams )
    {
                                //  Get text to segment.
        String text =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.TEXT ) ).trim();

        String untaggedText = unTag( text );

                                //  Include input text in result.

        boolean includeInputText    =
            queryParams.getFirstValue( QueryParams.INCLUDEINPUTTEXT ) != null;

                                //  Get corpus configuration.
        String corpusConfig =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

        String segmenterName        =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.SEGMENTERNAME ) ).trim();

        int c99MaskSize             =
            getIntValue
            (
                StringUtils.safeString(
                    queryParams.getFirstValue(
                        QueryParams.C99MASKSIZE ) ).trim() ,
                11
            );

        int c99SegmentsWanted       =
            getIntValue
            (
                StringUtils.safeString(
                    queryParams.getFirstValue(
                        QueryParams.C99SEGMENTSWANTED ) ).trim() ,
                -1
            );

        int tilerSlidingWindowSize          =
            getIntValue
            (
                StringUtils.safeString(
                    queryParams.getFirstValue(
                        QueryParams.TILERSLIDINGWINDOWSIZE ) ).trim() ,
                10
            );

        int tilerStepSize       =
            getIntValue
            (
                StringUtils.safeString(
                    queryParams.getFirstValue(
                        QueryParams.TILERSTEPSIZE ) ).trim() ,
                100
            );

        List<List<String>> sentences    = null;
        List<Integer> segments          = null;

                                //  Segment text.

        if ( untaggedText.length() > 0 )
        {
            sentences   =
                adornerInfo.sentenceSplitter.extractSentences( untaggedText );

            TextSegmenter textSegmenter;

            if ( segmenterName != null )
            {
                if ( segmenterName.equals( "C99" ) )
                {
                    textSegmenter   = new C99TextSegmenter();

                    ((C99TextSegmenter)textSegmenter).setSegmentsWanted(
                        c99SegmentsWanted );

                    ((C99TextSegmenter)textSegmenter).setMaskSize(
                        c99MaskSize );
                }
                else
                {
                    textSegmenter   = new TextTilingTextSegmenter();

                    ((TextTilingTextSegmenter)textSegmenter).setSlidingWindowSize(
                        tilerSlidingWindowSize );

                    ((TextTilingTextSegmenter)textSegmenter).setStepSize(
                        tilerStepSize );
                }

                segments    =
                    textSegmenter.getSegmentPositions( sentences );
            }
        }
                                //  Create results object.
        return
            new TextSegmenterResult
            (
                includeInputText ? text : null ,
                corpusConfig ,
                segmenterName ,
                c99MaskSize ,
                c99SegmentsWanted ,
                tilerSlidingWindowSize ,
                tilerStepSize ,
                sentences ,
                segments
            );
    }

    /** Describe the parameters.
     *
     *  @return     Parameters as list of ParameterInfo objects.
     */

    protected List<ParameterInfo> describeParameters()
    {
        List<ParameterInfo> params  = super.describeParameters();

        ParameterInfo param =
            new ParameterInfo
            (
                QueryParams.TEXT ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Text to segment."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.INCLUDEINPUTTEXT ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "Include input text in output."
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

        param =
            new ParameterInfo
            (
                QueryParams.SEGMENTERNAME ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY ,
                "Text segmenter to use."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.C99MASKSIZE ,
                true ,
                WADLINT ,
                ParameterStyle.QUERY ,
                "C99 mask size."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.C99SEGMENTSWANTED ,
                true ,
                WADLINT ,
                ParameterStyle.QUERY ,
                "Number of C99 segements wanted."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.TILERSLIDINGWINDOWSIZE ,
                true ,
                WADLINT ,
                ParameterStyle.QUERY ,
                "Text tiling sliding window size."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.TILERSTEPSIZE ,
                true ,
                WADLINT ,
                ParameterStyle.QUERY ,
                "Text tiling step size."
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



