package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;

import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import com.thoughtworks.xstream.XStream;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.lexicon.*;

/** Lexicon lookup resource.
 */

public class LexiconLookupResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Lexicon lookup resource" );
        setDescription
        (
            "Looks up information about word in corpus configuration" +
            " training  data."
        );
    }

    /** Handle Get request.
     *
     *  @return     Lexicon lookup results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

        return
            postResults
            (
                queryParams ,
                lexiconLookup( queryParams ) ,
                "MutableInteger" ,
                MutableInteger.class
            );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Pluralizer results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
        Form queryParams = new Form( representation );

        return
            postResults
            (
                queryParams ,
                lexiconLookup( queryParams ) ,
                "MutableInteger" ,
                MutableInteger.class
            );
    }

    /** Perform lexicon lookup.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Lexicon lookup result.
     */

    public LexiconLookupResult lexiconLookup( Form queryParams )
    {
        String spelling     =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.SPELLING ) ).trim();

        String corpusConfig =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

                                //  If we have a spelling, get
                                //  information about it from
                                //  selected lexicon.

        LexiconEntry lexiconEntry       = null;
        List<String> relatedSpellings   = null;

        if ( spelling.length() > 0 )
        {
            lexiconEntry    =
                adornerInfo.adorner.wordLexicon.getLexiconEntry( spelling );

            if ( lexiconEntry != null )
            {
                Iterator<String> iterator   =
                    lexiconEntry.categoriesAndCounts.keySet().iterator();

                Set<String> lemmata = new TreeSet<String>();

                while ( iterator.hasNext() )
                {
                    String posTag   = iterator.next();

                    String lemma    = lexiconEntry.lemmata.get( posTag );

                    lemmata.add( lemma );
                }

                iterator    = lemmata.iterator();

                while ( iterator.hasNext() )
                {
                    String theLemma = iterator.next();

                    Set<String> spellingsSet    =
                        adornerInfo.lemmaToSpellings.get( theLemma );

                    spellingsSet.remove( spelling );

                    if ( spellingsSet.size() > 0 )
                    {
                        if ( relatedSpellings == null )
                        {
                            relatedSpellings    = ListFactory.createNewList();
                        }

                        relatedSpellings.addAll( spellingsSet );
                    }
                }
            }
        }
                                //  Create results object.
        return
            new LexiconLookupResult
            (
                spelling ,
                corpusConfig ,
                lexiconEntry ,
                relatedSpellings
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
                "Spelling to look up in corpus lexicon."
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



