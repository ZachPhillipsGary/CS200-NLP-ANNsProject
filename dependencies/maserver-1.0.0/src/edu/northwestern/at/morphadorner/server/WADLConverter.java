package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.StringReader;
import java.io.StringWriter;

import java.net.URL;

import java.util.logging.Level;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.wadl.*;
import org.restlet.ext.xml.TransformRepresentation;
import org.restlet.representation.*;

/** Convert WADL descriptor to HTML for display.
 */

public class WADLConverter
{
    /** Templates for XSLT transformer. */

    public static Templates templates;

    /** Creates a new HTML representation for a given {@link WadlServerResource}
     *  instance describing a server resource.
     *
     *  @param  xml     The xml version of the WADL description.
     *
     *  @return         The created {@link WadlRepresentation}.
     */

    public static Representation createHtmlRepresentation( String xml )
    {
        Representation representation = null;

        try
        {
                                //  Get a clone of the compiled transformer.

            Transformer transformer = templates.newTransformer();

                                //  Set up the input.

            Source inputXML     =
                new StreamSource( new StringReader( xml ) );

                                //  Set up the output.

            StringWriter writer = new StringWriter();

            Result outputHTML   = new StreamResult( writer );

                                //  Transform WADL XML to HTML using
                                //  the compiled XSLT style sheet.

            transformer.transform( inputXML , outputHTML );

                                //  Create a StringRepresentation
                                //  to hold the HTML output.

            return new StringRepresentation
            (
                writer.toString() ,
                MediaType.TEXT_HTML
            );
        }
        catch ( Exception e )
        {
            Context.getCurrent().getLogger().log
            (
                Level.WARNING ,
                "Unable to generate the WADL HTML representation" ,
                e
            );
        }

        return representation;
    }

    /** Allow overrides but not instantiation. */

    protected WADLConverter()
    {
    }

    /** Initialize stylesheet. */

    static
    {
                                //  Get a transformer factory.

        TransformerFactory factory  = TransformerFactory.newInstance();

                                //  Get the WADL to HTML stylesheet.

        URL wadl2htmlXsltUrl    =
            Engine.getResource
            (
                "edu/northwestern/at/morphadorner/server/resources/wadl.xsl"
            );
                                //  Compile the XSLT stylesheet.
        try
        {
            Source xslSource    =
                new StreamSource( wadl2htmlXsltUrl.openStream() );

            templates   = factory.newTemplates( xslSource );
        }
        catch ( Exception e )
        {
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



