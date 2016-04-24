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

/** Parser service tests. */

public class TestParserService extends BaseTest
{
    /** Sentence to parse. */

    protected static String sampleText  =
        "Mary had a little lamb whose fleece was white as snow.";

    /** Expected parser results. */

    protected static String[] expectedParse =
    {
        "+------------------------------------Xp-----------------------------------+" ,
        "|            +-------Os------+                                            |" ,
        "+---Wd--+-Ss-+   +IDDC+--Dmu-+--Mr-+--Dmuw-+--Ss--+--Pa--+--MVz-+-Osc-+   |" ,
        "|       |    |   |    |      |     |       |      |      |      |     |   |" ,
        "LEFT-WALL Mary had.v a little lamb.n whose fleece.n was.v white.a as.z snow.n ." ,
        "        LEFT-WALL  Xp    <---Xp---->  Xp    ." ,
        "(m)     LEFT-WALL  Wd    <---Wd---->  Wd    Mary" ,
        "(m)     Mary       Ss    <---Ss---->  S     had.v" ,
        "(m)     had.v      O     <---Os---->  Os    lamb.n" ,
        "(m)     little     Dmu   <---Dmu--->  D*u   lamb.n" ,
        "(m)     a          IDDC  <---IDDC-->  IDDC  little" ,
        "(m)(r)  lamb.n     M     <---Mr---->  Mr    whose" ,
        "(m)(r)  whose      D**w  <---Dmuw-->  Dmu   fleece.n" ,
        "(m)(r)  fleece.n   Ss    <---Ss---->  Ss    was.v" ,
        "(m)(r)  was.v      Pa    <---Pa---->  Pa    white.a" ,
        "(m)(r)  white.a    MV    <---MVz--->  MVz   as.z" ,
        "(m)(r)  as.z       O*c   <---Osc--->  Os    snow.n" ,
        "        .          RW    <---RW---->  RW    RIGHT-WALL"
    };

    /** Test parser service. */

    @Test
    public void testParserService()
    {
                                //  Create client resource.

        ClientResource resource = createClientResource( "parser" );

                                //  Add query parameters.

        resource.addQueryParameter( "text" , sampleText );
        resource.addQueryParameter( "media" , "text" );

                                //  Get result from server.
        try
        {
            String result   = resource.get( String.class );

            System.out.println( "Sentence: " + sampleText );

            String[] lines  = result.split( "\n" );

            int k       = 0;
            boolean ok  = true;

            for ( int i = 0 ; i < lines.length ; i++ )
            {
                String line = lines[ i ].trim();

                System.out.println( line );

                if ( line.length() > 0 )
                {
                    ok  = ok && ( line.equals( expectedParse[ k ].trim() ) );

                    if ( !line.equals( expectedParse[ k ].trim() ) )
                    {
                        System.out.println
                        (
                            "Line " + i +
                            " doesn't match expected line " + k
                        );
                        System.out.println
                        (
                            "   " + i + ": " + line
                        );
                        System.out.println
                        (
                            "   " + k + ": " + expectedParse[ k ]
                        );
                    }

                    k++;
                }
            }

            assertTrue( ok );
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


