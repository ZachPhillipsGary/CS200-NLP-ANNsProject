package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.ArrayList;
import java.util.List;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import edu.northwestern.at.utils.StringUtils;

/** Lemmatizer resource.
 */

public class LemmatizerResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Lemmatizer resource" );
        setDescription( "Finds lemma form of a word." );
    }

    /** Handle Get request.
     *
     *  @return     Lemmatization results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.

        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get lemmatizer result.

        return postResults( queryParams , lemmatize( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Lemmatization results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Get lemmatizer result.

        return postResults( queryParams , lemmatize( queryParams ) );
    }

    /** Perform lemmatization.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Lemmatizer result.
     */

    public LemmatizerResult lemmatize( Form queryParams )
    {
        String spelling     =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.SPELLING ) ).trim();

        String standardSpelling = spelling;

        String corpusConfig =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

        String wordClass    =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.WORDCLASS ) ).trim();

        String wordClass2   =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.WORDCLASS2 ) ).trim();

        boolean standardize =
            getBooleanValue
            (
                StringUtils.safeString(
                    queryParams.getFirstValue(
                        QueryParams.STANDARDIZE ) ).trim() ,
                    false
            );

        String lemma            = "";
        String lancasterStem    = "";
        String porterStem       = "";

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

        if ( spelling.length() > 0 )
        {
            if ( standardize )
            {
                standardSpelling    =
                    adornerInfo.adorner.spellingStandardizer.standardizeSpelling(
                        spelling , "" );
            }

            if ( wordClass.length() > 0 )
            {
                if ( wordClass2.length() > 0 )
                {
                    lemma   =
                        MorphAdornerServerData.lemmatizer.lemmatize
                        (
                            standardSpelling ,
                            wordClass + "," + wordClass2
                        );
                }
                else
                {
                    lemma   =
                        MorphAdornerServerData.lemmatizer.lemmatize
                        (
                            standardSpelling ,
                            wordClass
                        );
                }
            }
            else
            {
                lemma   =
                    MorphAdornerServerData.lemmatizer.lemmatize(
                        standardSpelling );
            }
                                //  Get Porter and Lancaster stems.

            porterStem      =
                MorphAdornerServerData.porterStemmer.stem
                ( standardSpelling );

            lancasterStem   =
                MorphAdornerServerData.lancasterStemmer.stem
                ( standardSpelling );
        }

        return new LemmatizerResult
        (
            spelling ,
            standardize ,
            standardSpelling ,
            corpusConfig ,
            wordClass ,
            wordClass2 ,
            lemma ,
            lancasterStem ,
            porterStem
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
                "Spelling to lemmatize."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.STANDARDIZE ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "True to standard the spelling before lemmatization."
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



