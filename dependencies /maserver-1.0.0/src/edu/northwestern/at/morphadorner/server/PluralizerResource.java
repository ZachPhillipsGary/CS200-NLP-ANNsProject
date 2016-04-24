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

/** Noun and pronoun pluralizer resource.
 */

public class PluralizerResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Pluralizer resource" );
        setDescription( "Finds plural forms of nouns and pronouns." );
    }

    /** Handle Get request.
     *
     *  @return     Pluralizer results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get lemmatizer result.

        return postResults( queryParams , pluralize( queryParams ) );
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

        return postResults( queryParams , pluralize( queryParams ) );
    }

    /** Perform pluralization.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Pluralizer result.
     */

    public PluralizerResult pluralize( Form queryParams )
    {
        String singular     =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.SINGULAR ) ).trim();

        String sAmerican        =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.AMERICAN ) ).trim();

        boolean american        =
            sAmerican.equals( "1" ) ||
            sAmerican.equals( "true" );

        String plural       = singular;

                                //  Pluralize singular form if we have one.

        if ( singular.length() > 0 )
        {
            plural  = MorphAdornerServerData.inflector.pluralize( singular );
        }
                                //  Change to American spelling
                                //  if requested.
        if ( american )
        {
            singular    =
                MorphAdornerServerData.britishToUS.mapSpelling( singular );

            plural      =
                MorphAdornerServerData.britishToUS.mapSpelling( plural );
        }

        return new PluralizerResult( singular , plural , american );
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
                QueryParams.SINGULAR ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Singular noun or pronoun."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.AMERICAN ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY ,
                "Use American spelling."
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



