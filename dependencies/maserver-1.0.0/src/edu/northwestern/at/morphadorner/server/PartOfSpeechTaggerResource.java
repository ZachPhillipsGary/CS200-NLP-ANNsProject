package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.XStream;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.adornedword.*;

/** Part of speech tagger resource.
 */

public class PartOfSpeechTaggerResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Part of speech tagger resource" );
        setDescription( "Tags words with their parts of speech." );
    }

    /** Handle Get request.
     *
     *  @return     Part of speech tagging results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

        PartOfSpeechTaggerResult result =
            adornWithPartsOfSpeech( queryParams );

        if ( result.outputTEI )
        {
            return result.toTEI();
        }
        else if ( result.outputTCF )
        {
            try
            {
                return result.toTCF();
            }
            catch ( Exception e )
            {
                return new StringRepresentation
                (
                    "Unable to generate TCF formatted output."
                );
            }
        }
        else
        {
            return
                postResults
                (
                    queryParams ,
                    adornWithPartsOfSpeech( queryParams ) ,
                    "adornedWord" ,
                    BaseAdornedWord.class ,
                    "token" ,
                    String.class
                );
        }
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Part of speech tagging results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost
    (
        Representation representation
    )
    {
        Form queryParams = new Form( representation );

        PartOfSpeechTaggerResult result =
            adornWithPartsOfSpeech( queryParams );

        if ( result.outputTEI )
        {
            return result.toTEI();
        }
        else if ( result.outputTCF )
        {
            try
            {
                return result.toTCF();
            }
            catch ( Exception e )
            {
                return new StringRepresentation
                (
                    "Unable to generate TCF formatted output."
                );
            }
        }
        else
        {
            return
                postResults
                (
                    queryParams ,
                    adornWithPartsOfSpeech( queryParams ) ,
                    "adornedWord" ,
                    BaseAdornedWord.class ,
                    "token" ,
                    String.class
                );
        }
    }

    /** Perform part of speech tagging.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Part of speech tagger result.
     */

    public PartOfSpeechTaggerResult adornWithPartsOfSpeech
    (
        Form queryParams
    )
    {
                                //  Get text to tag.
        String text =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.TEXT ) ).trim();

        text    = unTag( text );

                                //  Include input text in result.

        boolean includeInputText    =
            queryParams.getFirstValue( QueryParams.INCLUDEINPUTTEXT ) != null;

                                //  Get corpus configuration.
        String corpusConfig     =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

        List<List<String>> sentences                = null;
        List<List<AdornedWord>> adornedSentences    = null;

                                //  Get XML output type.

        String xmlOutputType    =
            queryParams.getFirstValue( QueryParams.XMLOUTPUTTYPE );

        if ( xmlOutputType == null )
        {
            xmlOutputType   = QueryParams.OUTPUTPLAINXML;
        }
                                //  Use TEI XML output?
        boolean outputTEI   =
            xmlOutputType.equals( QueryParams.OUTPUTTEI );

                                //  Use WebLicht TCF XML output?
        boolean outputTCF   =
            xmlOutputType.equals( QueryParams.OUTPUTTCF );

                                //  Include standard spelling?
        boolean outputReg   =
            queryParams.getFirstValue( QueryParams.OUTPUTREG ) != null;

                                //  Adorn text.

        if ( text.length() > 0 )
        {
                                //  Extract sentences and words.
            sentences       =
                adornerInfo.sentenceSplitter.extractSentences( text );

                                //  Assign part of speech tags.
            adornedSentences    =
                adornerInfo.adorner.tagger.tagSentences( sentences );

                                //  Add standard spellings and lemmata.

            for ( int i = 0 ; i < adornedSentences.size() ; i++ )
            {
                List<AdornedWord> sentence  = adornedSentences.get( i );

                for ( int j = 0 ; j < sentence.size() ; j++ )
                {
                    AdornedWord wordAndTag  = sentence.get( j );

                    String standardSpelling =
                        adornerInfo.adorner.spellingStandardizer.standardizeSpelling
                        (
                            wordAndTag.getSpelling() ,
                            wordAndTag.getPartsOfSpeech()
                        );

                    wordAndTag.setStandardSpelling( standardSpelling );

                                //  Get lemma.
                                //  Try corpusConfig first.

                    String lemma    =
                        adornerInfo.adorner.wordLexicon.getLemma
                        (
                            wordAndTag.getSpelling() ,
                            wordAndTag.getPartsOfSpeech()
                        );

                                //  Lemma not found in word corpusConfig.
                                //  Use lemmatizer.

                    if  (   lemma.equals( "*" ) &&
                        (   MorphAdornerServerData.lemmatizer != null ) )
                    {
                        if ( standardSpelling.length() > 0 )
                        {
                            lemma   =
                                MorphAdornerServerData.lemmatizer.lemmatize
                                (
                                    standardSpelling ,
                                    adornerInfo.adorner.partOfSpeechTags.
                                        getLemmaWordClass
                                        (
                                            wordAndTag.getPartsOfSpeech()
                                        )
                                );
                        }
                        else
                        {
                            lemma   =
                                MorphAdornerServerData.lemmatizer.lemmatize
                                (
                                    wordAndTag.getSpelling() ,
                                    adornerInfo.adorner.partOfSpeechTags.
                                        getLemmaWordClass
                                        (
                                            wordAndTag.getPartsOfSpeech()
                                        )
                                );
                        }
                    }

                    wordAndTag.setLemmata( lemma );
                }
            }
        }
                                //  Store results.
        return
            new PartOfSpeechTaggerResult
            (
                includeInputText ? text : null ,
                corpusConfig ,
                sentences ,
                adornedSentences ,
                outputTEI ,
                outputReg ,
                outputTCF
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
                "Text to adorn."
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
/*
        param =
            new ParameterInfo
            (
                QueryParams.OUTPUTTEI ,
                ParameterStyle.QUERY ,
                "Output adornments in fragmentary TEI XML format."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.OUTPUTTCF ,
                ParameterStyle.QUERY ,
                "Output adornments in WebLicht TCF XML format."
            );

        params.add( param );
*/
        param =
            new ParameterInfo
            (
                QueryParams.XMLOUTPUTTYPE ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY ,
                "Type of XML format for output."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.OUTPUTREG ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY ,
                "Output standardized spelling in TEI XML format."
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



