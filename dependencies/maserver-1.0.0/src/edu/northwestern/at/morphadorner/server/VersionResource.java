package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import edu.northwestern.at.morphadorner.*;
import edu.northwestern.at.utils.*;

/** Server version resource.
 */

public class VersionResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Version resource" );
        setDescription( "Returns server and MorphAdorner version." );
    }

    /** Handle Get request.
     *
     *  @return     MorphAdorner server and program version.
     */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get version result.

        return postResults( queryParams , serverVersion( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form.
     *
     *  @return     MorphAdorner server and program version.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Get version result.

        return postResults( queryParams , serverVersion( queryParams ) );
    }

    /** Return server version information.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Version result.
     */

    public VersionResult serverVersion( Form queryParams )
    {
        return new VersionResult
        (
            MorphAdornerServerData.getMorphAdornerVersion() ,
            MorphAdornerServerData.getServerVersion()
        );
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



