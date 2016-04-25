package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;
import java.util.Set;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.utils.Formatters;
import edu.northwestern.at.utils.ListFactory;
import edu.northwestern.at.utils.StringUtils;
import edu.northwestern.at.utils.html.HTMLUtils;

/**  Name recognizer results.
 */

@XStreamAlias("NameRecognizerResult")
public class NameRecognizerResult extends BaseResults
{
    /** Text to process. */

    public String text;

    /** Corpus configuration. */

    public String corpusConfig;

    /** Person names. */

    @XStreamConverter(PersonNamesSetConverter.class)
    public Set<String> personNames;

    /** Place names. */

    @XStreamConverter(PlaceNamesSetConverter.class)
    public Set<String> placeNames;

    /** Create empty name recognizer results.
     */

    public NameRecognizerResult()
    {
        this.text           = "";
        this.corpusConfig   = "";
        this.personNames    = null;
        this.placeNames     = null;
    }

    /** Create populated name recognizer results.
     *
     *  @param  text            Text to tag.
     *  @param  corpusConfig    Corpus configuration.
     *  @param  personNames     Person names.
     *  @param  placeNames      Place names.
     */

    public NameRecognizerResult
    (
        String text ,
        String corpusConfig ,
        Set<String> personNames ,
        Set<String> placeNames
    )
    {
        this.text           = text;
        this.corpusConfig   = corpusConfig;
        this.personNames    = personNames;
        this.placeNames     = placeNames;
    }

    /** Generate string representation.
     *
     *  @return     String representation.
     */

    public String toString()
    {
        return stringFromHTML();
    }

    /** Return HTML version of class entry values.
     *
     *  @return     HTML string of class entry values.
     */

    public String toHTML()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "<h3>\n" );

        int nPersonNames    = personNames.size();
        int nPlaceNames     = placeNames.size();

        switch ( nPersonNames )
        {
            case 0  : sb.append( "No person names found.\n" );
                      break;

            case 1  : sb.append( "1 person name found.\n" );
                      break;

            default : sb.append
                        (
                            Formatters.formatIntegerWithCommas( nPersonNames ) +
                            " person names found.\n"
                        );
        }

        sb.append( "</h3>\n" );

        sb.append( "<table border=\"0\">\n" );

        List<String> personNamesList    =
            ListFactory.createNewList( personNames );

        for ( int i = 0 ; i < personNamesList.size() ; i++ )
        {
            String personName   = personNamesList.get( i );

            sb.append( "<tr><td>" );
            sb.append( personName );
            sb.append( "</td></tr>\n" );
        }

        sb.append( "</table>\n" );

        sb.append( "<h3>\n" );

        switch ( nPlaceNames )
        {
            case 0  : sb.append( "No place names found.\n" );
                      break;

            case 1  : sb.append( "1 place name found.\n" );
                      break;

            default : sb.append
                        (
                            Formatters.formatIntegerWithCommas( nPlaceNames ) +
                            " place names found.\n"
                        );
        }

        sb.append( "</h3>\n" );

        sb.append( "<table border=\"0\">\n" );

        List<String> placeNamesList =
            ListFactory.createNewList( placeNames );

        for ( int i = 0 ; i < placeNamesList.size() ; i++ )
        {
            String placeName    = placeNamesList.get( i );

            sb.append( "<tr><td>" );
            sb.append( placeName );
            sb.append( "</td></tr>\n" );
        }

        sb.append( "</table>\n" );

        return sb.toString();
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



