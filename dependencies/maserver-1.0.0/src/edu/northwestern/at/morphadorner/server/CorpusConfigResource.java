package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.*;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.ext.xstream.XstreamRepresentation;

import edu.northwestern.at.utils.*;

/** Corpus Config resource.
 */

public class CorpusConfigResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Corpus configuration resource" );
        setDescription( "Describes a corpus configuration." );
    }

    /** Handle Get request.
     *
     *  @return     List of corpus configurations.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get corpus configuration.

        return postResults( queryParams , corpusConfig( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     List of corpus configurations.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Get corpus configuration.

        return postResults( queryParams , corpusConfig( queryParams ) );
    }

    /** Return server version information.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Version result.
     */

    public CorpusConfigResult corpusConfig( Form queryParams )
    {
        List<CorpusConfigInfo> corpusConfigInfoList =
            ListFactory.createNewList();

        for ( String configName : MorphAdornerServerData.corpusConfigs.keySet() )
        {
            MorphAdornerServerInfo serverInfo   =
                MorphAdornerServerData.corpusConfigs.get( configName );

            CorpusConfigInfo corpusConfigInfo   =
                new CorpusConfigInfo
                (
                    serverInfo.serverInfoName ,
                    serverInfo.serverInfoDescription
                );

            corpusConfigInfoList.add( corpusConfigInfo );
        }

        return new CorpusConfigResult( corpusConfigInfoList );
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



