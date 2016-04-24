package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import com.thoughtworks.xstream.annotations.*;

import edu.northwestern.at.morphadorner.corpuslinguistics.sentencemelder.*;
import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.utils.ListFactory;

/** Text segmenter result.
 */

@XStreamAlias("TextSegmenterResult")
public class TextSegmenterResult extends BaseResults
{
    /** Text to tokenize. */

    public String text;

    /** Corpus configuration. */

    public String corpusConfig;

    /** C99 mask size. */

    public int c99MaskSize;

    /** C99 segments wanted. */

    public int c99SegmentsWanted;

    /** Text Tiler sliding window size. */

    public int tilerSlidingWindowSize;

    /** Text Tiler step size. */

    public int tilerStepSize;

    /** Sentences. */

    @XStreamConverter(ListOfListOfTokensConverter.class)
    public List<List<String>> sentences;

    /** Text segment positions. */

    public List<Integer> segments;

    /** Segmenter name. */

    public String segmenterName;

    @XStreamConverter(SegmentTextsListConverter.class)
    public List<String> segmentTexts;

    /** Create text segment results.
     */

    public TextSegmenterResult()
    {
        this.text                   = "";
        this.corpusConfig           = "";
        this.segmenterName          = "";
        this.c99MaskSize            = 11;
        this.c99SegmentsWanted      = -1;
        this.tilerSlidingWindowSize = 10;
        this.tilerStepSize          = 100;
        this.sentences              = null;
        this.segments               = null;
        this.segmentTexts           = null;
    }

    /** Create populated text segment results.
     *
     *  @param  text                    Text to segment.
     *  @param  corpusConfig            Corpus configuration.
     *  @param  segmenterName           Segmenter name.
     *  @param  c99MaskSize             C99 mask size.
     *  @param  c99SegmentsWanted       C99 number of segments wanted.
     *  @param  tilerSlidingWindowSize  Text tiling sliding window size.
     *  @param  tilerStepSize           Text tiling step size.
     *  @param  sentences               Word and sentences.
     *  @param  segments                Segment positions.
     */

    public TextSegmenterResult
    (
        String text ,
        String corpusConfig ,
        String segmenterName ,
        int c99MaskSize ,
        int c99SegmentsWanted ,
        int tilerSlidingWindowSize ,
        int tilerStepSize ,
        List<List<String>> sentences ,
        List<Integer> segments
    )
    {
        this.text                   = text;
        this.corpusConfig           = corpusConfig;
        this.segmenterName          = segmenterName;
        this.c99MaskSize            = c99MaskSize;
        this.c99SegmentsWanted      = c99SegmentsWanted;
        this.tilerSlidingWindowSize = tilerSlidingWindowSize;
        this.tilerStepSize          = tilerStepSize;
        this.sentences              = sentences;
        this.segments               = segments;

        makeSegmentTexts();
    }

    /** Make segment texts from sentences and segment offsets.
     *
     *  <p>
     *  On output, the segmentTexts field is set to the
     *  melded sentence text for each segment, in order.
     *  </p>
     */

    protected void makeSegmentTexts()
    {
        SentenceMelder melder   = new SentenceMelder();

        this.segmentTexts       = ListFactory.createNewList();

        int firstSentence   = 0;
        int lastSentence    = 0;

        for ( int i = 0 ; i < segments.size() ; i++ )
        {
            StringBuffer sb = new StringBuffer();

            firstSentence   = segments.get( i );

            if ( i < ( segments.size() - 1 ) )
            {
                lastSentence    = segments.get( i + 1 );
            }
            else
            {
                lastSentence    = sentences.size();
            }

            for (   int j = firstSentence ;
                    j < lastSentence ;
                    j++ )
            {
                List<String> sentence   = sentences.get( j );

                String sentenceText =
                    melder.reconstituteSentence( sentence );

                sb.append( sentenceText );
                sb.append( "  " );
            }

            segmentTexts.add( sb.toString() );
        }
    }

    /** Return string version of class entry values.
     *
     *  @return     String version of class entry values.
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        switch ( segments.size() )
        {
            case 0:
                sb.append( "No segments found.\n" );
                break;

            case 1:
                sb.append( "1 segment found using " + segmenterName +
                    ".\n" );
                break;

            default:
                sb.append
                (
                    segments.size() +
                    " segments found using " + segmenterName + ".\n" );
                break;
        }

        if ( segments.size() == 0 )
        {
            return sb.toString();
        }

        sb.append( "Segment\tText\n" );

        SentenceMelder melder   = new SentenceMelder();

        int firstSentence   = 0;
        int lastSentence    = 0;

        for ( int i = 0 ; i < segments.size() ; i++ )
        {
            firstSentence   = segments.get( i );

            if ( i < ( segments.size() - 1 ) )
            {
                lastSentence    = segments.get( i + 1 );
            }
            else
            {
                lastSentence    = sentences.size();
            }

            sb.append( ( i + 1 ) + "\t" );

            for (   int j = firstSentence ;
                    j < lastSentence ;
                    j++ )
            {
                List<String> sentence   = sentences.get( j );

                String sentenceText =
                    melder.reconstituteSentence( sentence );

                sb.append( sentenceText );
                sb.append( "  " );
            }

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



