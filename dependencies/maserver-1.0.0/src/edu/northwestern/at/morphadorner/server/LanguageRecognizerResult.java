package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.utils.Formatters;

/** Language recognizer results.
 */

@XStreamAlias("LanguageRecognizerResult")
public class LanguageRecognizerResult extends BaseResults
{
    /** Text for which to find candidate languages. */

    public String text;

    /** Candidate languages and scores. */

    @XStreamConverter(LanguageListConverter.class)
    public List<ScoredLanguage> languages;

    /** Create empty language recognition results.
     */

    public LanguageRecognizerResult()
    {
        this.text       = null;
        this.languages  = null;
    }

    /** Create populated language recognition results.
     *
     *  @param  text        Text for which to find candidate languages.
     *  @param  languages   Candidate languages as scored strings.
     */

    public LanguageRecognizerResult
    (
        String text ,
        List<ScoredLanguage> languages
    )
    {
        this.text       = text;
        this.languages  = languages;
    }

    /** Return string version of class entry values.
     *
     *  @return     String of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        switch ( languages.size() )
        {
            case 0  : sb.append( "No language identified.\n" );
                      break;

            case 1  : sb.append( "1 language identified.\n" );
                      break;

            default : sb.append( languages.size() +
                        " languages identified.\n" );
        }

        sb.append( "Language\tScore\n" );

        for ( int i = 0 ; i < languages.size() ; i++ )
        {
            String languageCode = languages.get( i ).getLanguageCode();
            String languageName = languages.get( i ).getLanguageName();
            String score        =
                Formatters.formatDouble( languages.get( i ).getScore() , 4 );

            sb.append( languageCode );
            sb.append( " (" );
            sb.append( languageName );
            sb.append( ")\t" );
            sb.append( score );
            sb.append( "\n" );
        }

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



