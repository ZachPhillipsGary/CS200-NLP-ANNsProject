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

/** Language recognizer service tests. */

public class TestLanguageRecognizerService extends BaseTest
{
    /** Texts to recognize. */

    protected static String frenchText  =
        "Au clair de la lune " +
        "Mon ami Pierrot " +
        "Pr\u00eate-moi ta plume " +
        "Pour \u00e9crire un mot " +
        "Ma chandelle est morte " +
        "Je n'ai plus de feu " +
        "Ouvre-moi ta porte " +
        "Pour l'amour de Dieu.";

    protected static String italianText =
        "Caro mio ben, " +
        "credimi almen, " +
        "senza di te " +
        "languisce il cor. " +
        "Il tuo fedel " +
        "sospira ognor. " +
        "Cessa, crudel, " +
        "tanto rigor!";

    protected static String englishText =
        "Mary had a little lamb, " +
        "whose fleece was white as snow. " +
        "And everywhere that Mary went, " +
        "the lamb was sure to go.";

    /** Test language recognizer service. */

    @Test
    public void testLanguageRecognizedrService()
    {
                                //  Create client resource.

        ClientResource resource =
            createClientResource( "languagerecognizer" );

                                //  Add query parameters.

        resource.addQueryParameter( "text" , englishText );
        resource.addQueryParameter( "media" , "xml" );

                                //  Get result from server.
        try
        {
            LanguageRecognizerResult result =
                resource.get( LanguageRecognizerResult.class );

            ScoredLanguage language = result.languages.get( 0 );

            assertEquals( language.getLanguageCode() , "en" );

            System.out.println( result );
        }
        catch ( Exception e )
        {
            System.out.println( "Error: " + e.getMessage() );
            fail();
        }

        ClientResource resource2    =
            createClientResource( "languagerecognizer" , false );

        resource2.addQueryParameter( "text" , frenchText );
        resource2.addQueryParameter( "media" , "xml" );

                                //  Get result from server.
        try
        {
            LanguageRecognizerResult result2    =
                resource2.get( LanguageRecognizerResult.class );

            ScoredLanguage language = result2.languages.get( 0 );

            assertEquals( language.getLanguageCode() , "fr" );

            System.out.println( result2 );
        }
        catch ( Exception e )
        {
            System.out.println( "Error: " + e.getMessage() );
            fail();
        }

        ClientResource resource3    =
            createClientResource( "languagerecognizer" , false );

        resource3.addQueryParameter( "text" , italianText );
        resource3.addQueryParameter( "media" , "xml" );

                                //  Get result from server.
        try
        {
            LanguageRecognizerResult result3    =
                resource3.get( LanguageRecognizerResult.class );

            ScoredLanguage language = result3.languages.get( 0 );

            assertEquals( language.getLanguageCode() , "it" );

            System.out.println( result3 );
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


