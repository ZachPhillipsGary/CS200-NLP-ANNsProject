package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import com.thoughtworks.xstream.annotations.*;

/** Hyphenator result.
 */

@XStreamAlias("HyphenatorResult")
public class HyphenatorResult extends BaseResults
{
    /** Spelling. */

    public String spelling;

    /** Use American hyphenation rules rather than British. */

    public boolean american;

    /** Hyphenated spelling. */

    public String hyphenatedSpelling;

    /** Create empty hyphenator results.
     */

    public HyphenatorResult()
    {
        this.spelling           = "";
        this.american           = false;
        this.hyphenatedSpelling = "";
    }

    /** Create populated hyphenator results.
     *
     *  @param  spelling            Spelling.
     *  @param  american            True to use American hyphenation rules.
     *  @param  hyphenatedSpelling  Hyphenated spelling.
     */

    public HyphenatorResult
    (
        String spelling ,
        boolean american ,
        String hyphenatedSpelling
    )
    {
        this.spelling           = spelling;
        this.american           = american;
        this.hyphenatedSpelling = hyphenatedSpelling;
    }

    /** Return string version of class entry values.
     *
     *  @return     String of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "Hyphenation Results\n\n" );

        sb.append( "Spelling:\t" );
        sb.append( spelling );
        sb.append( "\n" );

        sb.append( "Rules:\t" );

        if ( american )
        {
            sb.append( "American" );
        }
        else
        {
            sb.append( "British" );
        }

        sb.append( "\n" );

        sb.append( "Hyphenated spelling:\t" );
        sb.append( hyphenatedSpelling );
        sb.append( "\n" );

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



