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

import edu.northwestern.at.utils.CharUtils;
import edu.northwestern.at.utils.ListUtils;
import edu.northwestern.at.utils.StringUtils;

/** Hyphenator resource.
 */

public class HyphenatorResource extends BaseAdornerServerResource
{
    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Hyphenator resource" );
        setDescription( "Hyphenates words." );
    }

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get corpus configuration.

        return postResults( queryParams , hyphenate( queryParams ) );
    }

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Get hyphenation result.

        return postResults( queryParams , hyphenate( queryParams ) );
    }

    /** Perform hyphenation.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Hyphenator lookup result.
     */

    public HyphenatorResult hyphenate( Form queryParams )
    {
        String spelling     =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.SPELLING ) ).trim();

        String sAmerican        =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.AMERICAN ) ).trim();

        boolean american        =
            sAmerican.equals( "1" ) ||
            sAmerican.equals( "true" );

        String hyphenatedSpelling   = "";

        if ( spelling.length() > 0 )
        {
            hyphenatedSpelling  =
                hyphenate( spelling , american );

            hyphenatedSpelling  =
                StringUtils.replaceAll
                (
                    hyphenatedSpelling ,
                    "\u00ad" ,
                    "-"
                );
        }

        return new HyphenatorResult
        (
            spelling ,
            american ,
            hyphenatedSpelling
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
                "Spelling to hyphenate."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.AMERICAN ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Use American hyphenation rules instead of British."
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



