package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.*;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.XStream;

import org.restlet.ext.xstream.XstreamRepresentation;

import edu.northwestern.at.morphadorner.corpuslinguistics.lexicon.*;
import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.utils.Formatters;

/** Lexicon lookup result.
 */

@XStreamAlias("LexiconLookupResult")
public class LexiconLookupResult extends BaseResults
{
    /** Spelling. */

    public String spelling;

    /** Corpus configuration. */

    public String corpusConfig;

    /** Lexicon entry. */

    public LexiconEntry lexiconEntry;

    /** Related spellings with same lemma. */

    @XStreamConverter(RelatedSpellingsListConverter.class)
    public List<String> relatedSpellings;

    /** Create empty lexicon entry results.
     */

    public LexiconLookupResult()
    {
        this.spelling           = "";
        this.corpusConfig       = "";
        this.lexiconEntry       = null;
        this.relatedSpellings   = null;
    }

    /** Create populated lexicon lookup results.
     *
     *  @param  spelling            Spelling to lemmatize.
     *  @param  corpusConfig        Corpus configuration.
     *  @param  lexiconEntry        Lexicon entry.
     *  @param  relatedSpellings    Related spellings.
     */

    public LexiconLookupResult
    (
        String spelling ,
        String corpusConfig ,
        LexiconEntry lexiconEntry ,
        List<String> relatedSpellings
    )
    {
        this.spelling           = spelling;
        this.corpusConfig       = corpusConfig;
        this.lexiconEntry       = lexiconEntry;
        this.relatedSpellings   = relatedSpellings;
    }

    /** Generate first part of string representation.
     *
     *  @return     First part of string representation.
     *
     *  <p>
     *  The first part contains the parts of speech, lemmata, and counts
     *  for the given spelling in the selected corpus.
     *  </p>
     */

    public String toString1()
    {
        StringBuffer sb = new StringBuffer();

        if ( spelling.length() == 0 )
        {
            return "No spelling provided.\n";
        }
        else if ( lexiconEntry == null )
        {
            return
                spelling + " does not appear in " +
                "the " + corpusConfig +
                " corpus training data.\n";
        }
        else
        {
            sb.append
            (
                spelling +
                " appears " +
                Formatters.formatIntegerWithCommas( lexiconEntry.entryCount ) +
                " time" + ( lexiconEntry.entryCount == 1 ? "" : "s" ) +
                " in the " +
                corpusConfig + " corpus training data.\n"
            );

            sb.append( "Part of Speech\tLemma\tCount\n" );

            Iterator<String> iterator   =
                lexiconEntry.categoriesAndCounts.keySet().iterator();

            Set<String> lemmata = new TreeSet<String>();

            while ( iterator.hasNext() )
            {
                String posTag   = iterator.next();

                String lemma    = lexiconEntry.lemmata.get( posTag );

                lemmata.add( lemma );

                int count       =
                    lexiconEntry.categoriesAndCounts.get( posTag ).intValue();

                sb.append( posTag );
                sb.append( "\t" );
                sb.append( lemma );
                sb.append( "\t" );
                sb.append( Formatters.formatIntegerWithCommas( count ) );
                sb.append( "\n" );
            }
        }

        return sb.toString();
    }

    /** Generate second part of string representation.
     *
     *  @return     Second part of string representation.
     *
     *  <p>
     *  The second part contains the list of related words.
     *  </p>
     */

    public String toString2()
    {
        StringBuffer sb = new StringBuffer();

        if  (   ( relatedSpellings != null ) &&
                ( relatedSpellings.size() > 0 )
            )
        {
            sb.append
            (
                "\nRelated spellings:\n"
            );

            StringBuffer sb2    = new StringBuffer();

            sb2.append( relatedSpellings.get( 0 ) );

            for ( int i = 1 ; i < relatedSpellings.size() ; i++ )
            {
                sb2.append( ", " );
                sb2.append( relatedSpellings.get( i ) );
            }

            sb.append( sb2.toString() );
            sb.append( "\n" );
        }

        return sb.toString();
    }

    /** Generate string representation.
     *
     *  @return     String representation.
     */

    public String toString()
    {
        return toString1() + toString2();
    }

    /** Return HTML version of class entry values.
     *
     *  @return     HTML string of class entry values.
     */

    public String toHTML()
    {
        return
            tabularDataBaseResults( toString1() , false ) +
            tabularDataBaseResults( toString2() , false );
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



