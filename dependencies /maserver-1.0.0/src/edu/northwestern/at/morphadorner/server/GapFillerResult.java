package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import edu.northwestern.at.morphadorner.server.converters.*;

/** Gap filler result.
 */

@XStreamAlias("GapFillerResult")
public class GapFillerResult extends BaseResults
{
    /** Spelling. */

    public String spelling;

    /** Corpus configuration. */

    public String corpusConfig;

    /** Suggestions for gap-filled spellings. */

    @XStreamConverter(SuggestionsListConverter.class)
    public List<String> suggestions;

    /** Create empty gap filler results.
     */

    public GapFillerResult()
    {
        this.spelling       = "";
        this.corpusConfig   = "";
        this.suggestions    = null;
    }

    /** Create populated gap filler results.
     *
     *  @param  spelling        Spelling with gaps to fill.
     *  @param  corpusConfig    Corpus configuration.
     *  @param  suggestions     Suggestions for gap filled spellings.
     */

    public GapFillerResult
    (
        String spelling ,
        String corpusConfig ,
        List<String> suggestions
    )
    {
        this.spelling       = spelling;
        this.corpusConfig   = corpusConfig;
        this.suggestions    = suggestions;
    }

    /** Return string version of class entry values.
     *
     *  @return     String of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        switch ( suggestions.size() )
        {
            case 0  : sb.append( "No suggestions found.\n" );
                      break;

            case 1  : sb.append( "1 suggestion found.\n" );
                      break;

            default : sb.append( suggestions.size() +
                        " suggestions found.\n" );
        }

        sb.append( "\n" );

        for ( int i = 0 ; i < suggestions.size() ; i++ )
        {
            sb.append( suggestions.get( i ) );
            sb.append( "\n" );
        }

        return sb.toString();
    }

    /** Return HTML version of class entry values.
     *
     *  @return     HTML string of class entry values.
     */

    public String toHTML()
    {
        return tabularDataBaseResults( toString() , false );
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



