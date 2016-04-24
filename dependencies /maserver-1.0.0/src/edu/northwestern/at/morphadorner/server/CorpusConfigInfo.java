package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** Corpus Config Info.
 *
 *  <p>
 *  A corpus config info object contains a configuration name
 *  and brief description of the configuration.
 *  </p>
 */

@XStreamAlias("CorpusConfigInfo")
public class CorpusConfigInfo
{
    /** Corpus configuration name. */

    protected String name;

    /** Corpus configuration description. */

    protected String description;

    /** Create empty corpus config info. */

    public CorpusConfigInfo()
    {
        name        = "";
        description = "";
    }

    /** Create populated corpus config info.
     *
     *  @param  name            The configuration name.
     *  @param  description     The configuration description,
     */

    public CorpusConfigInfo( String name , String description )
    {
        this.name           = name;
        this.description    = description;
    }

    /** Get name.
     *
     *  @return     Corpus config name.
     */

    public String getName()
    {
        return name;
    }

    /** Set name.
     *
     *  @param  name    Corpus config name.
     */

    public void setName( String name )
    {
        this.name   = name;
    }

    /** Get description.
     *
     *  @return     Corpus config description.
     */

    public String getDescription()
    {
        return description;
    }

    /** Set description.
     *
     *  @param  description Corpus config description.
     */

    public void setDescription( String description )
    {
        this.description    = description;
    }

    /** Return string version of corpus config info.
     *
     *  @return     String version of corpus config info.
     */

    public String toString()
    {
        return name + ": " + description;
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



