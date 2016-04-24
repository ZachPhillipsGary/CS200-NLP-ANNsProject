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

/** Summarizer service tests. */

public class TestSummarizerService extends BaseTest
{
    /** Text to summarize. */

    protected static String sampleText  =
"Four score and seven years ago our fathers brought forth on this " +
"continent a new nation, conceived in Liberty, and dedicated to " +
"the proposition that all men are created equal. " +
" " +
"Now we are engaged in a great civil war, testing whether that " +
"nation, or any nation, so conceived and so dedicated, can long " +
"endure. We are met on a great battle-field of that war. We have " +
"come to dedicate a portion of that field, as a final resting " +
"place for those who here gave their lives that that nation might " +
"live. It is altogether fitting and proper that we should do " +
"this. " +
" " +
"But, in a larger sense, we can not dedicate -- we can not " +
"consecrate -- we can not hallow -- this ground. The brave men, living " +
"and dead, who struggled here, have consecrated it, far above our " +
"poor power to add or detract. The world will little note, nor " +
"long remember what we say here, but it can never forget what " +
"they did here. It is for us the living, rather, to be dedicated " +
"here to the unfinished work which they who fought here have thus " +
"far so nobly advanced. It is rather for us to be here dedicated " +
"to the great task remaining before us -- that from these honored " +
"dead we take increased devotion to that cause for which they " +
"gave the last full measure of devotion -- that we here highly " +
"resolve that these dead shall not have died in vain -- that this " +
"nation, under God, shall have a new birth of freedom -- and that " +
"government : of the people, by the people, for the people, shall " +
"not perish from the earth.";

    /** Test summarizer service. */

    @Test
    public void testSummarizerService()
    {
                                //  Create client resource.

        ClientResource resource = createClientResource( "summarizer" );

                                //  Add query parameters.

        resource.addQueryParameter( "text" , sampleText );
        resource.addQueryParameter( "corpusConfig" , "ncf" );
        resource.addQueryParameter( "maxSumSent" , "2" );
        resource.addQueryParameter( "media" , "xml" );

                                //  Get result from server.
        try
        {
            SummarizerResult result =
                resource.get( SummarizerResult.class );

            System.out.println( "Summary: " + result.summaryText );
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



