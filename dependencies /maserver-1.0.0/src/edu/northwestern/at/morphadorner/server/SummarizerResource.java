package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.ext.wadl.*;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.XStream;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.sentencemelder.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.textsummarizer.*;

/** Summarizer resource.
 */

public class SummarizerResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Summarizer resource" );
        setDescription( "Finds short summary for a text." );
    }

    /** Handle Get request.
     *
     *  @return     Summarizer results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Return summarization results.

        return postResults( queryParams , summarize( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Summarizer results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Return summarization results.

        return postResults( queryParams , summarize( queryParams ) );
    }

    /** Perform text summarization.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Summarization result.
     */

    public SummarizerResult summarize( Form queryParams )
    {
                                //  Get text to summarize.
        String text =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.TEXT ) ).trim();

        text    = unTag( text );

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

                                //  Get maximum number of sentences
                                //  in summary.

        String sMaxSumSent  =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.MAXSUMSENT ) ).trim();

        int maxSumSent  = 0;

        try
        {
            maxSumSent  = Integer.parseInt( sMaxSumSent );
        }
        catch ( Exception e )
        {
        }
                                //  Hold summary text.

        StringBuffer summaryText    = null;

                                //  Get sentences and words.

        if ( text.length() > 0 )
        {
            List<List<String>> sentences    =
                adornerInfo.sentenceSplitter.extractSentences( text );

                                //  Get summary.

            int nSentences  = maxSumSent;

            if ( nSentences < 2 )
            {
                nSentences  = Math.min( sentences.size() , 2 );
            }

            TextSummarizer summarizer   = new DefaultTextSummarizer();

            List<Integer> summarySentences  =
                summarizer.summarize( sentences , nSentences );

                                //  Get melded summary sentences.

            SentenceMelder melder   = new SentenceMelder();
            summaryText             = new StringBuffer();

            for ( int i = 0 ; i < summarySentences.size() ; i++ )
            {
                List<String> sentence   =
                    sentences.get( summarySentences.get( i ) );

                String sentenceText =
                    melder.reconstituteSentence( sentence );

                summaryText.append( sentenceText );
                summaryText.append( " " );

                melder.reset();
            }
        }
                                //  Create results object.
        return
            new SummarizerResult
            (
                includeInputText ? text : null ,
                corpusConfig ,
                maxSumSent ,
                summaryText.toString()
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
                "Text to summarize."
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
                "maxSumSent" ,
                true ,
                WADLINT ,
                ParameterStyle.QUERY ,
                "Maximum number of summary sentences."
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



