package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

/** Spelling standardizer result.
 */

@XStreamAlias("SpellingStandardizerResult")
public class SpellingStandardizerResult extends BaseResults
{
    /** Spelling. */

    public String spelling;

    /** Standard spelling. */

    public String standardSpelling;

    /** Corpus configuration. */

    public String corpusConfig;

    /** Word class. */

    public String wordClass;

    /** Secondary word class. */

    public String wordClass2;

    /** Create empty spelling standardizer results.
     */

    public SpellingStandardizerResult()
    {
        this.spelling           = "";
        this.standardSpelling   = "";
        this.corpusConfig       = "";
        this.wordClass          = "";
        this.wordClass2         = "";
    }

    /** Create populated spelling standardizer results.
     *
     *  @param  spelling            Spelling to lemmatize.
     *  @param  standardSpelling    Standard spelling.
     *  @param  corpusConfig        Corpus configuration.
     *  @param  wordClass           Lemmatization word class.
     *  @param  wordClass2          Secondary word class.
     */

    public SpellingStandardizerResult
    (
        String spelling ,
        String standardSpelling ,
        String corpusConfig ,
        String wordClass ,
        String wordClass2
    )
    {
        this.spelling           = spelling;
        this.standardSpelling   = standardSpelling;
        this.corpusConfig       = corpusConfig;
        this.wordClass          = wordClass;
        this.wordClass2         = wordClass2;
    }

    /** Return displayable string of class entry values.
     *
     *  @return     Displayable string of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "Spelling Standardizer Results\n\n" );
/*
        sb.append( "Spelling:\t" );
        sb.append( spelling );
        sb.append( "\n" );
*/
        sb.append( "Standard spelling:\t" );
        sb.append( standardSpelling );
        sb.append( "\n" );
/*
        sb.append( "Corpus configuration:\t" );
        sb.append( corpusConfig );
        sb.append( "\n" );

        sb.append( "Primary word class:\t" );
        sb.append( wordClass );
        sb.append( "\n" );

        sb.append( "Secondary word class:\t" );
        sb.append( wordClass2 );
        sb.append( "\n" );
*/
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



