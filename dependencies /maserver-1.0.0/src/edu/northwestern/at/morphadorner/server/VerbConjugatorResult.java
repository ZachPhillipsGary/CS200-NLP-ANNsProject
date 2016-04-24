package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import edu.northwestern.at.morphadorner.corpuslinguistics.inflector.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.inflector.conjugator.*;

/** Verb conjugation results.
 */

@XStreamAlias("VerbConjugatorResult")
public class VerbConjugatorResult extends BaseResults
{
    /** Infinitive. */

    public String infinitive;

    /** Verb tense. */

//  public VerbTense verbTense;
    public String verbTense;

    /** American spellings. */

    public boolean american;

    /** Conjugation. */

    public String firstPersonSingular;
    public String secondPersonSingular;
    public String thirdPersonSingular;
    public String firstPersonPlural;
    public String secondPersonPlural;
    public String thirdPersonPlural;

    /** Create empty conjugation.
     */

    public VerbConjugatorResult()
    {
        this.infinitive             = "";
//      this.verbTense              = VerbTense.PRESENT;
        this.verbTense              = "present";
        this.american               = false;
        this.firstPersonSingular    = "";
        this.secondPersonSingular   = "";
        this.thirdPersonSingular    = "";
        this.firstPersonPlural      = "";
        this.secondPersonPlural     = "";
        this.thirdPersonPlural      = "";
    }

    /** Create populated conjugation.
     *
     *  @param  infinitive              Infinitive
     *  @param  verbTense               Verb tense
     *  @param  american                American spellings.
     *  @param  firstPersonSingular     First person singular.
     *  @param  secondPersonSingular    Second person singular.
     *  @param  thirdPersonSingular     Third person singular.
     *  @param  firstPersonPlural       First person plural.
     *  @param  secondPersonPlural      Second person plural.
     *  @param  thirdPersonPlural       Third person plural.
     */

    public VerbConjugatorResult
    (
        String infinitive ,
        VerbTense verbTense ,
        boolean american ,
        String firstPersonSingular ,
        String secondPersonSingular ,
        String thirdPersonSingular ,
        String firstPersonPlural ,
        String secondPersonPlural ,
        String thirdPersonPlural
    )
    {
        this.infinitive             = infinitive;
        this.verbTense              = verbTense.toString().toLowerCase();
        this.american               = american;
        this.firstPersonSingular    = firstPersonSingular;
        this.secondPersonSingular   = secondPersonSingular;
        this.thirdPersonSingular    = thirdPersonSingular;
        this.firstPersonPlural      = firstPersonPlural;
        this.secondPersonPlural     = secondPersonPlural;
        this.thirdPersonPlural      = thirdPersonPlural;
    }

    /** Return string version of class entry values.
     *
     *  @return     String of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        String sVerbTense   = verbTense.toString();
        sVerbTense  = sVerbTense.replaceAll( "_" , " " );

        sb.append( "Conjugation of " );
        sb.append( sVerbTense );
        sb.append( " tense for infinitive " );
        sb.append( "\"to " + infinitive + "\"\n\n" );

        sb.append( "First person singular:\t" );
        sb.append( firstPersonSingular );
        sb.append( "\n" );

        sb.append( "Second person singular:\t" );
        sb.append( secondPersonSingular );
        sb.append( "\n" );

        sb.append( "Third person singular:\t" );
        sb.append( thirdPersonSingular );
        sb.append( "\n" );

        sb.append( "First person plural:\t" );
        sb.append( firstPersonPlural );
        sb.append( "\n" );

        sb.append( "Second person plural:\t" );
        sb.append( secondPersonPlural );
        sb.append( "\n" );

        sb.append( "Third person plural:\t" );
        sb.append( thirdPersonPlural );
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



