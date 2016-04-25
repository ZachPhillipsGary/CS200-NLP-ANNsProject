package edu.northwestern.at.morphadorner.server.tests;

/*  Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.restlet.*;
import org.restlet.ext.json.*;
import org.restlet.ext.xml.*;
import org.restlet.data.*;
import org.restlet.representation.*;
import org.restlet.resource.*;
import org.restlet.service.*;

import org.restlet.ext.xstream.XstreamRepresentation;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.XStream;

import edu.northwestern.at.morphadorner.server.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** Name recognizer service tests. */

public class TestNameRecognizerService extends BaseTest
{
    /** Text to search for names. */

    protected static String sampleText  =
        "Abraham Lincoln was born in Kentucky.  " +
        "He is buried in Springfield, Illinois.";

    /** Test name recognizer service. */

    @Test
    public void testNameRecognizerService()
    {
                                //  Create client resource.

        ClientResource resource = createClientResource( "namerecognizer" );

                                //  Add query parameters.

        resource.addQueryParameter( "text" , sampleText );
        resource.addQueryParameter( "corpusConfig" , "ncf" );
        resource.addQueryParameter( "media" , "xml" );

                                //  Get result from server.

        try
        {
            NameRecognizerResult result =
                resource.get( NameRecognizerResult.class );

                                //  Display results.

            System.out.println( "Text: " + sampleText );
            System.out.println( "Person names: " + result.personNames );
            System.out.println( "Place names: " + result.placeNames );

                                //  Check results.

            assertEquals( 1 , result.personNames.size() );
            assertEquals( 3 , result.placeNames.size() );
        }
        catch ( Exception e )
        {
            System.out.println( "Error: " + e.getMessage() );
            fail();
        }
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



