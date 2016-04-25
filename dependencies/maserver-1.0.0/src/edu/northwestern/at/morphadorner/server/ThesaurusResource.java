package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;
import java.util.Set;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import edu.northwestern.at.utils.ListUtils;
import edu.northwestern.at.utils.SetFactory;
import edu.northwestern.at.utils.StringUtils;

/** Thesaurus resource.
 */

public class ThesaurusResource extends BaseAdornerServerResource
{
    /** Allowed word classes. */

    protected static Set<String> allowedWordClasses =
        SetFactory.createNewSet();

    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Thesaurus resource" );
        setDescription( "Finds synonyms and antonyms for a word." );
    }

    /** Handle Get request.
     *
     *  @return     Thesaurus results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Return thesaurus lookup results.

        return postResults( queryParams , thesaurusLookup( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Thesaurus results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Return thesaurus lookup results.

        return postResults( queryParams , thesaurusLookup( queryParams ) );
    }

    /** Perform thesaurus lookup.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Thesaurus lookup result.
     */

    public ThesaurusResult thesaurusLookup( Form queryParams )
    {
        String spelling     =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.SPELLING ) ).trim();

        String wordClass    =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.WORDCLASS ) ).trim();

        boolean addSynAnt   =
            getBooleanValue
            (
                StringUtils.safeString(
                    queryParams.getFirstValue(
                        QueryParams.ADDSYNANT ) ).trim() ,
                    false
            );

        if ( wordClass.length() > 0 )
        {
            if ( !allowedWordClasses.contains( wordClass ) )
            {
                wordClass   = "";
            }
        }

        List<String> synonyms   = null;
        List<String> antonyms   = null;

        if ( spelling.length() > 0 )
        {
                                //  Get synonyms.
            synonyms    =
                MorphAdornerServerData.thesaurus.getSynonyms
                (
                    spelling ,
                    wordClass
                );
                                //  Get antonyms.
            antonyms    =
                MorphAdornerServerData.thesaurus.getAntonyms
                (
                    spelling ,
                    wordClass
                );
                                //  Add synonyms of antonyms if requested.
            if ( addSynAnt )
            {
                Set<String> moreAntonyms    = SetFactory.createNewSortedSet();

                for ( int i = 0 ; i < antonyms.size() ; i++ )
                {
                    moreAntonyms.addAll
                    (
                        MorphAdornerServerData.thesaurus.getSynonyms
                        (
                            antonyms.get( i ) ,
                            wordClass
                        )
                    );
                }

                antonyms.clear();
                antonyms.addAll( moreAntonyms );
            }
        }

        return new ThesaurusResult
        (
            spelling ,
            wordClass ,
            addSynAnt ,
            synonyms ,
            antonyms
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
                QueryParams.SPELLING ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Spelling for which to find synonyms and antonyms."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.WORDCLASS ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Word class of spelling."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.ADDSYNANT ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Add synonyms for each antonym to antonyms list."
            );

        params.add( param );

        return sortParams( params );
    }

    static
    {
        allowedWordClasses.add( "noun" );
        allowedWordClasses.add( "verb" );
        allowedWordClasses.add( "adjective" );
        allowedWordClasses.add( "adverb" );
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



