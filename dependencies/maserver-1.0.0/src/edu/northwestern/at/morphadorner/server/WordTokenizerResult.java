package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.*;

import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.tokenizer.TokenizerUtils;
import edu.northwestern.at.utils.Formatters;

/** Word tokenizer result.
 */

@XStreamAlias("WordTokenizerResult")
public class WordTokenizerResult extends BaseResults
{
    /** Text to tokenize. */

    public String text;

    /** Language code. */

    public String langCode;

    /** Corpus configuration. */

    public String corpusConfig;

    /** Sentences. */

    @XStreamConverter(ListOfListOfTokensConverter.class)
    public List<List<String>> sentences;

    /** Create empty word tokenizer results.
     */

    public WordTokenizerResult()
    {
        this.text               = "";
        this.langCode           = "";
        this.corpusConfig       = "";
        this.sentences          = null;
    }

    /** Create populated word tokenizer results.
     *
     *  @param  text                Text to tokenizer.
     *  @param  langCode            Language.
     *  @param  corpusConfig        Corpus configuration.
     *  @param  sentences           Word and sentences.
     */

    public WordTokenizerResult
    (
        String text ,
        String langCode ,
        String corpusConfig ,
        List<List<String>> sentences
    )
    {
        this.text           = text;
        this.langCode       = langCode;
        this.corpusConfig   = corpusConfig;
        this.sentences      = sentences;
    }

    /** Return string version of class entry values.
     *
     *  @return     String of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        int nWords  = 0;

        for ( int i = 0 ; i < sentences.size() ; i++ )
        {
            nWords  += sentences.get( i ).size();
        }

        int nSent   = sentences.size();

        switch ( nSent )
        {
            case 0  :   sb.append( "No words found.\n" );
                        break;

            case 1  :   if ( nWords == 1 )
                        {
                            sb.append( "1 word in 1 sentence.\n" );
                        }
                        else
                        {
                            sb.append
                            (
                                Formatters.formatIntegerWithCommas( nWords ) +
                                " words in 1 sentence.\n"
                            );
                        }
                        break;

            default :   sb.append
                        (
                            Formatters.formatIntegerWithCommas( nWords ) +
                            " words in " +
                            Formatters.formatIntegerWithCommas( nSent ) +
                            " sentences found.\n"
                        );
        }

        sb.append( "S#\tW#\tToken\tType\n" );

        for ( int i = 0 ; i < nSent ; i++ )
        {
            List<String> sentence   = sentences.get( i );

            for ( int j = 0 ; j < sentence.size() ; j++ )
            {
                sb.append( ( i + 1 ) + "\t" );
                sb.append( ( j + 1 ) + "\t" );
                sb.append( sentence.get( j ) );
                sb.append( "\t" );
                sb.append(
                    TokenizerUtils.getTokenType( sentence.get( j ) )
                );
                sb.append( "\n" );
            }
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



