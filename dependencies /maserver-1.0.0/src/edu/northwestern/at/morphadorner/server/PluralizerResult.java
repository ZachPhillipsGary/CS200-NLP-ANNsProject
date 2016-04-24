package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** Pluralizer results.
 */

@XStreamAlias("PluralizerResult")
public class PluralizerResult extends BaseResults
{
    /** Singular form. */

    public String singular;

    /** Plural form. */

    public String plural;

    /** American spelling. */

    public boolean american;

    /** Create empty singular/plural forms.
     */

    public PluralizerResult()
    {
        this.singular   = "";
        this.plural     = "";
        this.american   = false;
    }

    /** Create populated singular/plural forms.
     *
     *  @param  singular    Singular form.
     *  @param  plural      Plural form.
     *  @param  american    American spelling.
     */

    public PluralizerResult
    (
        String singular ,
        String plural ,
        boolean american
    )
    {
        this.singular   = singular;
        this.plural     = plural;
        this.american   = american;
    }

    /** Generate string representation.
     *
     *  @return     string representation.
     */

    public String toString()
    {
        return
            "Pluralizer results\n\n" +
            "Singular:\t" + singular + "\n" +
            "Plural:\t" + plural + "\n" +
            "American:\t" + american + "\n";
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



