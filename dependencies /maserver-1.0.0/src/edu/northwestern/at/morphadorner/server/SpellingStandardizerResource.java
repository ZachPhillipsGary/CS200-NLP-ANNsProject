package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import org.restlet.ext.wadl.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.spellingstandardizer.*;

/** Spelling standardizer resource.
 */

public class SpellingStandardizerResource extends BaseAdornerServerResource
{
    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Spelling standardizer resource" );
        setDescription( "Finds standard spelling for a variant spelling." );
    }

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Return spelling standardization results.

        return postResults
        (
            queryParams ,
            standardizeSpelling( queryParams )
        );
    }

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Return spelling standardization results.
        return postResults
        (
            queryParams ,
            standardizeSpelling( queryParams )
        );
    }

    /** Perform spelling standardization.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Spelling standardizer result.
     */

    public SpellingStandardizerResult standardizeSpelling( Form queryParams )
    {
        String spelling     =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.SPELLING ) ).trim();

        String corpusConfig =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

        String wordClass    =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.WORDCLASS ) ).trim();

        String wordClass2   =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.WORDCLASS2 ) ).trim();

        String sExtendedSearch  =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.EXTENDEDSEARCH ) ).trim();

        boolean extendedSearch  =
            sExtendedSearch.equals( "true" ) ||
            sExtendedSearch.equals( "1" );

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

                                //  If we have a spelling, get
                                //  list of suggested spellings.

        String suggestion   = spelling;
        SortedArrayList<ScoredString> suggestions   = null;

        if ( spelling.length() > 0 )
        {
            if ( extendedSearch )
            {
System.out.println( "standardizer: spelling=" + spelling );
System.out.println( "standardizer: decrutified spelling=" +
    EnglishDecruftifier.decruftify( spelling ) );

                suggestions =
                    new SortedArrayList<ScoredString>
                    (
                        adornerInfo.standardizer.
                            getScoredSuggestedSpellings
                            (
                                EnglishDecruftifier.decruftify
                                (
                                    spelling
                                )
                            )
                    );

                suggestion  =
                    suggestions.get(
                        suggestions.size() - 1 ).getString();

                if ( suggestion.equals( "?" ) )
                {
                    suggestion  =
                        adornerInfo.adorner.spellingStandardizer.
                            standardizeSpelling( spelling , "" );
                }
            }
            else
            {
                suggestions = null;

                suggestion  =
                    adornerInfo.simpleStandardizer.standardizeSpelling
                    (
                        spelling ,
                        ""
                    );
            }
        }

        return new SpellingStandardizerResult(
            spelling, suggestion, corpusConfig, wordClass, wordClass2 );
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
                QueryParams.SPELLING ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Spelling to standardize."
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
                QueryParams.WORDCLASS ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Primary word class of spelling."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.WORDCLASS2 ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Secondary word class of spelling."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                "extendedSearch" ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "Perform extended search for standard spelling."
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



