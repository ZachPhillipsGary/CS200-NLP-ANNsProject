package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

import org.restlet.data.Disposition;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.ext.wadl.*;
import org.restlet.Message;
import org.restlet.representation.*;
import org.restlet.resource.Options;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import org.restlet.ext.xstream.XstreamRepresentation;

import com.thoughtworks.xstream.XStream;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;

import edu.northwestern.at.morphadorner.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.tokenizer.*;

/** Base server resource class providing common methods shared by
 *  MorphAdorner server resources.
 */

public class BaseAdornerServerResource extends WadlServerResource
{
    /** WADL parameter types. */

    public static String WADLSTRING     = "xsd:string";
    public static String WADLINT        = "xsd:int";
    public static String WADLBOOLEAN    = "xsd:boolean";

    /** Headers key. */

    protected static final String HEADERS_KEY = "org.restlet.http.headers";

    /** Create BaseAdornerServerResource.
     */

    public BaseAdornerServerResource()
    {
        super();
    }

    /** Retrieve headers from a message.
     *
     *  @param  message     The message.
     *
     *  @return             Series<Header> containing headers.
     */

    @SuppressWarnings( "unchecked" )
    protected Series<Header> getMessageHeaders( Message message )
    {
        ConcurrentMap<String, Object> attrs = message.getAttributes();

        Series<Header> headers  = (Series<Header>)attrs.get( HEADERS_KEY );

        if ( headers == null )
        {
            headers = new Series<Header>( Header.class );

            Series<Header> prev =
                (Series<Header>)attrs.putIfAbsent( HEADERS_KEY , headers );

            if ( prev != null )
            {
                headers = prev;
            }
        }

        return headers;
    }

    /** Handle options to implement CORS.
     *
     *  @param  entity  Input options entity.
     */

    @Options
    public void doOptions( Representation entity )
    {
                                //  Get headers.

        Series<Header> responseHeaders  = getMessageHeaders( getResponse() );

                                //  Add required headers for CORS.

        responseHeaders.add( "Access-Control-Allow-Origin" , "*" );
        responseHeaders.add( "Access-Control-Allow-Methods" ,
            "GET, POST, OPTIONS" );
        responseHeaders.add( "Access-Control-Allow-Headers" ,
            "Content-Type" );
        responseHeaders.add( "Access-Control-Allow-Credentials" ,
            "true" );
        responseHeaders.add( "Access-Control-Max-Age" , "120" );
    }

    /** Add CORS headers to response.
     */

    public void addCORSHeaders()
    {
        Series<Header>responseHeaders   = getMessageHeaders( getResponse() );

        responseHeaders.add( "Access-Control-Allow-Origin" , "*" );
    }

    /** Describe the parameters.
     *
     *  @return     Parameters as list of ParameterInfo objects.
     *
     *  <p>
     *  Adds a description only for the "media" parameter, which is
     *  common to all the resources.
     *  </p>
     */

    protected List<ParameterInfo> describeParameters()
    {
        List<ParameterInfo> params  = new ArrayList<ParameterInfo>();

        ParameterInfo param =
            new ParameterInfo
            (
                "media" ,
                false ,
                WADLSTRING ,
                ParameterStyle.QUERY ,
                "Media type of response."
            );

        params.add( param );

        return params;
    }

    /** Remove HTML/XML tags from text.
     *
     *  @param  text    The text from which to remove tags.
     *
     *  @return         The text with tags removed.
     */

    public String unTag( String text )
    {
        String result   = text.trim();

        if ( HTMLUtils.isHTMLTaggedText( result ) )
        {
            result  = HTMLUtils.stripHTMLTags( result );
        }

        result  = result.replaceAll( "\\s" , " " );

        return result;
    }

    /** Gets integer parameter value.
     *
     *  @param  requestValue    Parameter value from request.
     *  @param  defaultValue    Default parameter value if parameter null
     *                          or invalid.
     *
     *  @return                 The parameter value, or the defaultValue
     *                          if paramValue is null or invalid.
     */

    public static int getIntValue
    (
        String requestValue ,
        int defaultValue
    )
    {
        int result  = defaultValue;

        if ( requestValue != null )
        {
            try
            {
                result  = Integer.parseInt( requestValue );
            }
            catch ( NumberFormatException e )
            {
                result  = defaultValue;
            }
        }

        return result;
    }

    /** Gets boolean parameter value.
     *
     *  @param  requestValue    Parameter value from request.
     *  @param  defaultValue    Default parameter value if parameter null
     *                          or invalid.
     *
     *  @return                 The parameter value, or the defaultValue
     *                          if paramValue is null or invalid.
     */

    public static boolean getBooleanValue
    (
        String requestValue ,
        boolean defaultValue
    )
    {
        boolean result  = defaultValue;

        if ( requestValue != null )
        {
            try
            {
                result  = Boolean.parseBoolean( requestValue );
            }
            catch ( NumberFormatException e )
            {
                result  = defaultValue;
            }
        }

        return result;
    }

    /** Find language code for language of text.
     *
     *  @param  text            Text for which to determine language code.
     *  @param  defaultLangCode Default ISO language code if language cannot
     *                          be determined.
     *
     *  @return     ISO language code.
     *              Specified default if language cannot be found.
     */

    public String recognizeLanguage( String text , String defaultLangCode )
    {
        String result   = defaultLangCode;

        if ( ( text != null ) && ( text.length() > 0 ) )
        {
            ScoredString[] languages    =
                MorphAdornerServerData.languageRecognizer.recognizeLanguage(
                    text );

            if ( languages != null )
            {
                result  = languages[ 0 ].getString();
            }
        }

        return result;
    }

    /** Find language code for language of text.
     *
     *  @param  text            Text for which to determine language code.
     *
     *  @return     ISO language code.
     *              Set to English if language cannot be found.
     */

    public String recognizeLanguage( String text )
    {
        return recognizeLanguage( text , "en" );
    }

    /** Hyphenate a spelling.
     *
     *  @param  spelling        Spelling to hyphenate.
     *  @param  american        True to use American rules rather than
     *                          British.
     *
     *  @return                 Hyphenated spelling.
     */

    public String hyphenate( String spelling , boolean american )
    {
        String result   = "";

        if ( american )
        {
            result  =
                MorphAdornerServerData.americanHyphenator.hyphenate
                (
                    spelling
                );
        }
        else
        {
            result  =
                MorphAdornerServerData.britishHyphenator.hyphenate
                (
                    spelling
                );
        }

        return result;
    }

    /** Extract sentences from a text field.
     *
     *  @param  text            Text from which to extract sentences.
     *  @param  langCode        Language code for language of text.
     *  @param  corpusConfig    Corpus configuration for language.
     *
     *  @return                 List of sentences.
     */

    public List<List<String>> extractSentences
    (
        String text ,
        String langCode ,
        String corpusConfig
    )
    {
                                //  Holds extracted sentences.

        List<List<String>> sentences    = null;

                                //  Extract sentences from non-English
                                //  text.

        if ( ( langCode.length() > 0 ) && !langCode.equals( "en" ) )
        {
            WordTokenizer tokenizer =
                new ICU4JBreakIteratorWordTokenizer
                (
                    new Locale( langCode )
                );

            ((CanSplitAroundPeriods)tokenizer).setSplitAroundPeriods( false );

            SentenceSplitter splitter   =
                new ICU4JBreakIteratorSentenceSplitter
                (
                    new Locale( langCode )
                );

            sentences   = splitter.extractSentences( text , tokenizer );
        }
                                //  Extract sentences from English text.
        else
        {
            MorphAdornerServerInfo adornerInfo  =
                MorphAdornerServerData.getAdornerInfo( corpusConfig );

            sentences   =
                adornerInfo.sentenceSplitter.extractSentences( text );
        }

        return sentences;
    }

    /** Return output representation for POST operation.
     *
     *  @param  queryParams     The query parameters.
     *  @param  results         The results to be returned.
     *  @param  aliasName       Alias name.
     *  @param  aliasClass      Alias class.
     *  @param  aliasName2      Alias name.
     *  @param  aliasClass2     Alias class.
     *  @param  aliasName3      Alias name.
     *  @param  aliasClass3     Alias class.
     */

    public <T extends BaseResults> Representation postResults
    (
        Form queryParams ,
        T results ,
        String aliasName ,
        Class aliasClass ,
        String aliasName2 ,
        Class aliasClass2 ,
        String aliasName3 ,
        Class aliasClass3
    )
    {
                                //  Add CORS headers.
        addCORSHeaders();
                                //  Get output type.
        String media    =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.MEDIA ) ).trim();

                                //  If HTML output, ask object to
                                //  generate its own HTML representation.

        if ( media.equals( "html" ) )
        {
            String html = ((BaseResults)results).toHTML();

            return new StringRepresentation( html , MediaType.TEXT_HTML );
        }
                                //  If plain text output, ask object to
                                //  generate its own plain text
                                //  representation.

        if ( media.equals( "text" ) )
        {
            String text = results.toString();

            return new StringRepresentation( text , MediaType.TEXT_PLAIN );
        }
                                //  Get XStream representation holder.

        XstreamRepresentation<T> reprResult =
            new XstreamRepresentation<T>
            (
                media.equals( "json" ) ?
                    MediaType.APPLICATION_JSON :
                    MediaType.APPLICATION_XML ,
                results
            );
                                //  Add aliases if not null.

        if ( ( aliasName != null ) && ( aliasClass != null ) )
        {
            XStream xStream = null;

            try
            {
                xStream = reprResult.getXstream();
            }
            catch ( Exception e )
            {
            }
                                //  Add aliases.

            if ( xStream != null )
            {
                xStream.alias( aliasName , aliasClass );

                if ( ( aliasName2 != null ) && ( aliasClass2 != null ) )
                {
                    xStream.alias( aliasName2 , aliasClass2 );
                }

                if ( ( aliasName3 != null ) && ( aliasClass3 != null ) )
                {
                    xStream.alias( aliasName3 , aliasClass3 );
                }
            }
        }

        return reprResult;
    }

    /** Return output representation for POST operation.
     *
     *  @param  queryParams     The query parameters.
     *  @param  results         The results to be returned.
     *  @param  aliasName       Alias name.
     *  @param  aliasClass      Alias class.
     *  @param  aliasName2      Alias name.
     *  @param  aliasClass2     Alias class.
     */

    public <T extends BaseResults> Representation postResults
    (
        Form queryParams ,
        T results ,
        String aliasName ,
        Class aliasClass ,
        String aliasName2 ,
        Class aliasClass2
    )
    {
        return postResults
        (
            queryParams ,
            results ,
            aliasName ,
            aliasClass ,
            aliasName2 ,
            aliasClass2 ,
            null ,
            null
        );
    }

    /** Return output representation for POST operation.
     *
     *  @param  queryParams     The query parameters.
     *  @param  results         The results to be returned.
     *  @param  aliasName       Alias name.
     *  @param  aliasClass      Alias class.
     */

    public <T extends BaseResults> Representation postResults
    (
        Form queryParams ,
        T results ,
        String aliasName ,
        Class aliasClass
    )
    {
        return
            postResults
            (
                queryParams ,
                results ,
                aliasName ,
                aliasClass ,
                null ,
                null ,
                null ,
                null
            );
    }

    /** Return output representation for POST operation.
     *
     *  @param  queryParams     The query parameters.
     *  @param  results         The results to be returned.
     */

    public <T extends BaseResults> Representation postResults
    (
        Form queryParams ,
        T results
    )
    {
        return postResults
        (
            queryParams , results , null , null , null , null , null , null
        );
    }

    /** Creates a new HTML representation for a given {@link WadlServerResource}
     *  instance describing a server resource.
     *
     *  @param  applicationInfo     The application description.
     *
     *  @return     The created {@link WadlRepresentation}.
     *
     *  <p>
     *  The built-in restlet WADL to HTML transformation is broken,
     *  so we replace it commpletely.
     *  </p>
     */

    protected Representation createHtmlRepresentation
    (
        ApplicationInfo applicationInfo
    )
    {
        Representation result   = null;

        try
        {
            result  =
                WADLConverter.createHtmlRepresentation
                (
                    createWadlRepresentation( applicationInfo ).getText()
                );
        }
        catch ( Exception e )
        {
        }

        return result;
    }

    /** Sort parameter info list.
     *
     *  @param  params  List of ParameterInfo objects.
     *
     *  @return         params list sorted by name.
     */

    public List<ParameterInfo> sortParams( List<ParameterInfo> params )
    {
        Collections.sort( params , new ParameterInfoComparable() );

        return params;
    }

    /** Add common status codes.
     *
     *  @param  methodInfo      Method information in which to store
     *                          status code.
     */

    public void addStatusCodes( MethodInfo methodInfo )
    {
        ResponseInfo responseInfo   = new ResponseInfo();

        responseInfo.getStatuses().add
        (
            new Status
            (
//              Status.CLIENT_ERROR_BAD_REQUEST ,
                400 ,
                "Bad request." ,
                "Bad request." ,
                null
            )
        );

        methodInfo.getResponses().add( responseInfo );

        responseInfo = new ResponseInfo();

        responseInfo.getStatuses().add
        (
            new Status
            (
//              Status.SUCCESS_OK ,
                200 ,
                "OK." ,
                "OK." ,
                null
            )
        );

        methodInfo.getResponses().add( responseInfo );
    }

    /** Describe Get status codes.
     *
     *  @param  methodInfo  Get method information in which to
     *                      store status codes.
     */

    @Override
    protected void describeGet( MethodInfo methodInfo )
    {
        addStatusCodes( methodInfo );

        super.describeGet( methodInfo );
    }

    /** Describe Post status codes.
     *
     *  @param  methodInfo  Get method information in which to
     *                      store status codes.
     */

    @Override
    protected void describePost( MethodInfo methodInfo )
    {
        addStatusCodes( methodInfo );

        super.describePost( methodInfo );
    }

    /** Comparator class for ParameterInfo object. */

    public class ParameterInfoComparable
        implements Comparator<ParameterInfo>
    {
        @Override
        public int compare( ParameterInfo o1 , ParameterInfo o2 )
        {
            return o1.getName().compareTo( o2.getName() );
        }
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



