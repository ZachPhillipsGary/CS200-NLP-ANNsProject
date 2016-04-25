package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

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

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.inflector.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.inflector.conjugator.*;

/** Verb conjugator resource.
 */

public class VerbConjugatorResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Verb conjugator resource" );
        setDescription( "Conjugates a verb." );
    }

    /** Handle Get request.
     *
     *  @return     Conjugation results.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get conjugation.

        return postResults( queryParams , conjugate( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     Conjugation results.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Get conjugation.

        return postResults( queryParams , conjugate( queryParams ) );
    }

    /** Perform verb conjugation.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Conjugator result.
     */

    public VerbConjugatorResult conjugate( Form queryParams )
    {
                                //  Get infinitive form to conjugate.

        String infinitive       =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.INFINITIVE ) ).trim();

        infinitive  = infinitive.toLowerCase();

                                //  Strip leading "to " if given.

        if ( infinitive.startsWith( "to " ) )
        {
            infinitive  = infinitive.substring( 2 ).trim();
        }
                                //  Get American spelling flag.

        String sAmerican        =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.AMERICAN ) ).trim();

        boolean american        =
            sAmerican.equals( "1" ) ||
            sAmerican.equals( "true" );

                                //  Get verb tense for which to derive
                                //  conjugation.
        String sVerbTense   =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.VERBTENSE ) ).trim();

        VerbTense verbTense = VerbTense.PRESENT;

        if ( sVerbTense.equals( "present" ) )
        {
            verbTense   = VerbTense.PRESENT;
        }
        else if ( sVerbTense.equals( "presentParticiple" ) )
        {
            verbTense   = VerbTense.PRESENT_PARTICIPLE;
        }
        else if ( sVerbTense.equals( "past" ) )
        {
            verbTense   = VerbTense.PAST;
        }
        else if ( sVerbTense.equals( "pastParticiple" ) )
        {
            verbTense   = VerbTense.PAST_PARTICIPLE;
        }
                                //  Conjugate verb if we have an infinitive.

        String firstPersonSingular  = "";
        String secondPersonSingular = "";
        String thirdPersonSingular  = "";
        String firstPersonPlural    = "";
        String secondPersonPlural   = "";
        String thirdPersonPlural    = "";

        if ( infinitive.length() == 0 )
        {
        }
        else
        {
            firstPersonSingular =
                MorphAdornerServerData.inflector.conjugate
                (
                    infinitive ,
                    verbTense ,
                    Person.FIRST_PERSON_SINGULAR
                );

            secondPersonSingular    =
                MorphAdornerServerData.inflector.conjugate
                (
                    infinitive ,
                    verbTense ,
                    Person.SECOND_PERSON_SINGULAR
                );

            thirdPersonSingular =
                MorphAdornerServerData.inflector.conjugate
                (
                    infinitive ,
                    verbTense ,
                    Person.THIRD_PERSON_SINGULAR
                );

            firstPersonPlural   =
                MorphAdornerServerData.inflector.conjugate
                (
                    infinitive ,
                    verbTense ,
                    Person.FIRST_PERSON_PLURAL
                );

            secondPersonPlural  =
                MorphAdornerServerData.inflector.conjugate
                (
                    infinitive ,
                    verbTense ,
                    Person.SECOND_PERSON_PLURAL
                );

            thirdPersonPlural   =
                MorphAdornerServerData.inflector.conjugate
                (
                    infinitive ,
                    verbTense ,
                    Person.THIRD_PERSON_PLURAL
                );

            if ( american )
            {
                firstPersonSingular =
                    MorphAdornerServerData.britishToUS.mapSpelling(
                        firstPersonSingular );

                secondPersonSingular    =
                    MorphAdornerServerData.britishToUS.mapSpelling(
                        secondPersonSingular );

                thirdPersonSingular =
                    MorphAdornerServerData.britishToUS.mapSpelling(
                        thirdPersonSingular );

                firstPersonPlural   =
                    MorphAdornerServerData.britishToUS.mapSpelling(
                        firstPersonPlural );

                secondPersonPlural  =
                    MorphAdornerServerData.britishToUS.mapSpelling(
                        secondPersonPlural );

                thirdPersonPlural   =
                    MorphAdornerServerData.britishToUS.mapSpelling(
                        thirdPersonPlural );
            }
        }

        return
            new VerbConjugatorResult
            (
                infinitive ,
                verbTense ,
                american ,
                firstPersonSingular ,
                secondPersonSingular ,
                thirdPersonSingular ,
                firstPersonPlural ,
                secondPersonPlural ,
                thirdPersonPlural
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
                QueryParams.INFINITIVE ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Infinitive of verb to conjugate."
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

        param =
            new ParameterInfo
            (
                QueryParams.VERBTENSE ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY ,
                "Verb tense for which to derive conjugation."
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



