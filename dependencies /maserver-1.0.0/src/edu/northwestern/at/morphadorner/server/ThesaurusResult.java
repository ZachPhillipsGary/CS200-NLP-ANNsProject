package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import com.thoughtworks.xstream.annotations.*;

import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.utils.Formatters;

/** Thesaurus result.
 */

@XStreamAlias("ThesaurusResult")
public class ThesaurusResult extends BaseResults
{
    /** Spelling. */

    public String spelling;

    /** Word class. */

    public String wordClass;

    /** Add synonyms of antonyms to antonyms list. */

    public boolean addSynAnt;

    /** Synonyms. */

    @XStreamConverter(SynonymsListConverter.class)
    public List<String> synonyms;

    /** Antonyms. */

    @XStreamConverter(AntonymsListConverter.class)
    public List<String> antonyms;

    /** Create empty thesaurus results.
     */

    public ThesaurusResult()
    {
        this.spelling   = "";
        this.wordClass  = "";
        this.addSynAnt  = false;
        this.synonyms   = null;
        this.antonyms   = null;
    }

    /** Create populated thesaurus results.
     *
     *  @param  spelling    Spelling.
     *  @param  wordClass   Word class.
     *  @param  addSynAnt   Add synonyms of antonyms.
     *  @param  synonyms    Synonyms.
     *  @param  antonyms    Antonyms.
     */

    public ThesaurusResult
    (
        String spelling ,
        String wordClass ,
        boolean addSynAnt ,
        List<String> synonyms ,
        List<String> antonyms
    )
    {
        this.spelling   = spelling;
        this.wordClass  = wordClass;
        this.addSynAnt  = addSynAnt;
        this.synonyms   = synonyms;
        this.antonyms   = antonyms;
    }

    /** Return results as a string.
     *
     *  @return     Results as a string.
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

        int nSynonyms   = synonyms.size();
        int nAntonyms   = antonyms.size();

        String asWordClass  = "";

        if ( wordClass.length() > 0 )
        {
            asWordClass = " as " + wordClass;
        }

        switch ( nSynonyms )
        {
            case 0  : sb.append( "No synonyms found for " + spelling +
                                    asWordClass + ".\n" );
                      break;

            case 1  : sb.append( "1 synonym found for " + spelling +
                                    asWordClass + ".\n" );
                      break;

            default : sb.append
                        (
                            Formatters.formatIntegerWithCommas( nSynonyms ) +
                            " synonyms found for " + spelling +
                            asWordClass + ".\n"
                        );
        }

        sb.append( "</h3>\n" );

        sb.append( "<table border=\"0\">\n" );

        for ( int i = 0 ; i < synonyms.size() ; i++ )
        {
            String synonym  = synonyms.get( i );

            sb.append( "<tr><td>" );
            sb.append( synonym );
            sb.append( "</td></tr>\n" );
        }

        sb.append( "</table>\n" );

        sb.append( "<h3>\n" );

        switch ( nAntonyms )
        {
            case 0  : sb.append( "No antonyms found for " + spelling +
                                    asWordClass + ".\n" );
                      break;

            case 1  : sb.append( "1 antonym found for " + spelling +
                                    asWordClass + ".\n" );
                      break;

            default : sb.append
                        (
                            Formatters.formatIntegerWithCommas( nAntonyms ) +
                            " antonyms found for " + spelling +
                            asWordClass + ".\n"
                        );
        }

        sb.append( "</h3>\n" );

        sb.append( "<table border=\"0\">\n" );

        for ( int i = 0 ; i < antonyms.size() ; i++ )
        {
            String antonym  = antonyms.get( i );

            sb.append( "<tr><td>" );
            sb.append( antonym );
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



