package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;
import java.util.Locale;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.ext.wadl.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.spellingstandardizer.*;

/** Language recognizer resource.
 */

public class LanguageRecognizerResource extends BaseAdornerServerResource
{
    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Language recognizer resource" );
        setDescription( "Recognizes language of text." );
    }

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get language recognition results.

        return postResults( queryParams , recognizeLanguage( queryParams ) );
    }

    @Post("form:txt|html|xml|json")
    public Representation handlePost
    (
        Representation representation
    )
    {
        Form queryParams = new Form( representation );

        return postResults( queryParams , recognizeLanguage( queryParams ) );
    }

    /** Perform language recognition.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Language recognizer result.
     */

    public LanguageRecognizerResult recognizeLanguage( Form queryParams )
    {
                                //  Get text to recognize.

        String text     =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.TEXT ) ).trim();

                                //  Note if we're to include the input
                                //  text in the output.

        boolean includeInputText    =
            queryParams.getFirstValue( QueryParams.INCLUDEINPUTTEXT ) != null;

                                //  Remove XML/HTML tags from the input
                                //  text.

        String untaggedText = unTag( text );

        List<ScoredLanguage> languageList   = null;

                                //  If the input text isn't empty ...

        if ( untaggedText.length() > 0 )
        {
                                //  Detect the language of the
                                //  input text.

            ScoredString[] languages    =
                MorphAdornerServerData.languageRecognizer.recognizeLanguage(
                    untaggedText );

                                //  if no language detected, set
                                //  the result language name to empty
                                //  string and probability to zero.

            if ( languages == null )
            {
                ScoredString unknown    = new ScoredString( "" , 0.0D );

                languages   =
                    new ScoredString[]{ unknown };
            }
                                //  Add all the possible
                                //  languages to the result list.

            languageList    = ListFactory.createNewList();

            for ( int i = 0 ; i < languages.length ; i++ )
            {
                String langCode = languages[ i ].getString();

                Locale locale   = new Locale( langCode );

                String langName = locale.getDisplayLanguage();

                if  (   langCode.equals( "zh-cn" ) ||
                        langCode.equals( "zh-tw" )
                    )
                {
                    langCode    = "zh";
                    langName    = "Chinese";
                }

                languageList.add
                (
                    new ScoredLanguage
                    (
                        langCode ,
                        langName ,
                        languages[ i ].getScore()
                    )
                );
            }
        }

        return new LanguageRecognizerResult
        (
            includeInputText ? text : null ,
            languageList
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
                "Text for which to recognize the language."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.INCLUDEINPUTTEXT ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY ,
                "Include input text in response."
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



