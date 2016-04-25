package edu.northwestern.at.morphadorner.server.tests;

/*  Please see the license information at the end of this file. */

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.restlet.resource.*;

/** Common settings for tests. */

public class BaseTest
{
    /** The default base MorphAdorner server URL. */

    public static String baseServerURL  =
        "http://localhost:8080/maserver/";

    /** Set up the test fixture.
     */

    @Before
    public void setUp()
    {
                                //  Get base server URL for
                                //  MorphAdorner services.

        baseServerURL   = System.getProperty( "baseServerURL" );

                                //  Append trailing slash if missing.

        if ( !baseServerURL.endsWith( "/" ) )
        {
            baseServerURL   += "/";
        }
    }

    /** Tear down the test fixture.
     */

    @After
    public void tearDown()
    {
    }

    /** Construct service URL.
     *
     *  @param  serviceName     The service name.
     *
     *  @return                 The service URL.
     *
     *  <p>
     *  The service URL is formed by concatenating the service name
     *  to the base server URL.
     *  </p>
     */

    public String getServiceURL( String serviceName )
    {
        return baseServerURL + serviceName;
    }

    /** Create client resource.
     *
     *  @param  serviceName     The service name.
     *  @param  displayURL      True to display the service URL.
     *
     *  @return                 Client resource.
     */

    public ClientResource createClientResource
    (
        String serviceName ,
        boolean displayURL
    )
    {
        String serviceURL   = getServiceURL( serviceName );

        if ( displayURL )
        {
            System.out.println( "Service URL: " + serviceURL );
        }

        return new ClientResource( serviceURL );
    }

    /** Create client resource.
     *
     *  @param  serviceName     The service name.
     *
     *  @return                 Client resource.
     */

    public ClientResource createClientResource( String serviceName )
    {
        return createClientResource( serviceName , true );
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



