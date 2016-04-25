package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.Serializable;
import java.util.*;
import edu.northwestern.at.utils.*;

/** Associates a language name with a score.
 */

public class ScoredLanguage implements Comparable, Serializable
{
    /** The 2 or 3 letter language code. */

    protected String languageCode;

    /** The displayable language name. */

    protected String languageName;

    /** The string score. */

    protected double score;

    /** Create scored language.
     */

    public ScoredLanguage()
    {
        this.languageCode   = "";
        this.languageName   = "";
        this.score          = 0.0D;
    }

    /** Create scored language.
     *
     *  @param  languageName    Language name.
     *  @param  score           Score.
     */

    public ScoredLanguage
    (
        String languageCode ,
        String languageName ,
        double score
    )
    {
        this.languageCode   = languageCode;
        this.languageName   = languageName;
        this.score          = score;
    }

    /** Get language code.
     *
     *  @return     The language code.
     */

    public String getLanguageCode()
    {
        return languageCode;
    }

    /** Set language code.
     *
     *  @param  languageCode    The language code.
     */

    public void setLanguageCode( String languageCode )
    {
        this.languageCode   = languageCode;
    }

    /** Get language name.
     *
     *  @return     The language name.
     */

    public String getLanguageName()
    {
        return languageName;
    }

    /** Set language name.
     *
     *  @param  languageName    The language name.
     */

    public void setLanguageName( String languageName )
    {
        this.languageName   = languageName;
    }

    /** Get score.
     *
     *  @return     The score.
     */

    public double getScore()
    {
        return score;
    }

    /** Set score.
     *
     *  @param  score   The score.
     */

    public void setScore( double score )
    {
        this.score  = score;
    }

    /** Generate displayable string.
     *
     *  @return     Code, name, and score.
     */

    public String toString()
    {
        return languageCode + " (" + languageName + ") " + score;
    }

    /** Check if another object is equal to this one.
     *
     *  @param  other   Other object to test for equality.
     *
     *  @return         true if other object is equal to this one.
     */

    public boolean equals( Object other )
    {
        boolean result  = false;

        if ( other instanceof ScoredLanguage )
        {
            ScoredLanguage otherScoredLanguage  = (ScoredLanguage)other;

            result  =
                ( languageCode.equals(
                    otherScoredLanguage.getLanguageCode() ) ) &&
                ( languageName.equals(
                    otherScoredLanguage.getLanguageName() ) ) &&
                ( score == otherScoredLanguage.getScore() );
        }

        return result;
    }

    /** Compare this scored language with another.
     *
     *  @param  other   The other scored language.
     *
     *  @return         < 0 if this scored language is less than the other,
     *                  = 0 if the two scored languages are equal,
     *                  > 0 if this scored language is greater than the other.
     */

    public int compareTo( Object other )
    {
        int result  = 0;

        if ( ( other == null ) ||
            !( other instanceof ScoredLanguage ) )
        {
            result  = Integer.MIN_VALUE;
        }
        else
        {
            ScoredLanguage otherScoredLanguage  = (ScoredLanguage)other;

            result  =
                Compare.compare( score , otherScoredLanguage.getScore() );

            if ( result == 0 )
            {
                result  =
                    Compare.compare
                    (
                        languageCode ,
                        otherScoredLanguage.getLanguageCode()
                    );
            }

            if ( result == 0 )
            {
                result  =
                    -Compare.compare
                    (
                        languageName ,
                        otherScoredLanguage.getLanguageName()
                    );
            }
        }

        return result;
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




