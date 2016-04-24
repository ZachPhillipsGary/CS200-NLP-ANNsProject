package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.representation.*;
import org.restlet.data.Form;
import org.restlet.ext.xml.DomRepresentation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.annotations.*;

import de.tuebingen.uni.sfs.dspin.tcf04.api.*;
import de.tuebingen.uni.sfs.dspin.tcf04.data.*;
import de.tuebingen.uni.sfs.dspin.tcf04.descriptor.*;

import edu.northwestern.at.morphadorner.corpuslinguistics.adornedword.*;
import edu.northwestern.at.morphadorner.server.converters.*;
import edu.northwestern.at.utils.CharUtils;
import edu.northwestern.at.utils.Formatters;
import edu.northwestern.at.utils.ListFactory;

/** Part of speech tagger result.
 */

@XStreamAlias("PartOfSpeechTaggerResult")
public class PartOfSpeechTaggerResult extends WordTokenizerResult
{
    /** Tagged sentences. */

    @XStreamConverter(AdornedSentencesConverter.class)
    public List<List<AdornedWord>> adornedSentences;

    /** True to return TEI formatted XML if XML output selected. */

    public boolean outputTEI    = false;

    /** True to add non-standard reg= attribute for standard spelling to
     *  TEI formatted XML.
     */

    public boolean outputReg    = false;

    /** True to output results in WebLicht TCF format XML. */

    public boolean outputTCF    = false;

    /** Create empty part of speech tagger results.
     */

    public PartOfSpeechTaggerResult()
    {
        this.text               = "";
        this.corpusConfig       = "";
        this.langCode           = null;
        this.sentences          = null;
        this.adornedSentences   = null;
        this.outputTEI          = false;
        this.outputReg          = false;
        this.outputTCF          = false;
    }

    /** Create populated part of speech tagger results.
     *
     *  @param  text                Text to tag.
     *  @param  corpusConfig        Corpus configuration.
     *  @param  sentences           Word and sentences.
     *  @param  adornedSentences    Adorned sentences.
     *  @param  outputTEI           Output XML as fragmentary TEI.
     *  @param  outputReg           Add standard spelling attribute to
     *                              TEI output.
     *  @param  outputTCF           Output XML in WebLicht TCF format.
     */

    public PartOfSpeechTaggerResult
    (
        String text ,
        String corpusConfig ,
        List<List<String>> sentences ,
        List<List<AdornedWord>> adornedSentences ,
        boolean outputTEI ,
        boolean outputReg ,
        boolean outputTCF
    )
    {
        this.text               = text;
        this.corpusConfig       = corpusConfig;
        this.langCode           = null;
        this.sentences          = sentences;
        this.adornedSentences   = adornedSentences;
        this.outputTEI          = outputTEI;
        this.outputReg          = outputReg;
        this.outputTCF          = outputTCF;
    }

    /** Convert results to TEI XML String.
     *
     *  @return     TEI XML formatted results as string.
     */

    public String toTEIString()
    {
        StringWriter sw = new StringWriter();

        int wordID  = 0;

        sw.write( "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\r" );

        for ( int i = 0 ; i < adornedSentences.size() ; i++ )
        {
            sw.write( "<s n=\"" + ( i + 1 ) + "\"/>" );
            sw.write( "\r\n" );

            List<AdornedWord> sentence  = adornedSentences.get( i );

            for ( int j = 0 ; j < sentence.size() ; j++ )
            {
                AdornedWord adornedWord = sentence.get( j );

                String token    = adornedWord.getToken();

                sw.write( "  <w xml:id=\"w" + ++wordID + "\"" );
                sw.write( " lemma=\"" + adornedWord.getLemmata() + "\"" );
                sw.write( " ana=\"#" + adornedWord.getPartsOfSpeech() + "\"" );

                if ( outputReg )
                {
                    sw.write( " reg=\"" + adornedWord.getStandardSpelling() + "\"" );
                }

                sw.write( ">" );
                sw.write( token );
                sw.write( "</w>\r\n" );
            }

            sw.write( "</s>\r\n" );
        }

        return sw.toString();
    }

    /** Convert results to TEI XML DOMRepresentation.
     *
     *  @return     TEI XML formatted results as DOMRepresentation.
     */

    public DomRepresentation toTEI()
    {
        DomRepresentation representation    = null;

        try
        {
            representation  = new DomRepresentation( MediaType.TEXT_XML );

            Document document   = representation.getDocument();

            Element div = document.createElement( "div" );

            document.appendChild( div );

            int wordID  = 0;

            for ( int i = 0 ; i < adornedSentences.size() ; i++ )
            {
                Element s   = document.createElement( "s" );

                String nAttributeValue  = ( i + 1 ) + "";

                s.setAttribute( "n" , nAttributeValue );

                List<AdornedWord> sentence  = adornedSentences.get( i );

                MyXMLSentenceMelder melder  =
                    new MyXMLSentenceMelder( document , s );

                int l   = sentence.size();

                for ( int j = 0 ; j < l ; j++ )
                {
                    AdornedWord adornedWord = sentence.get( j );

                    String token    = adornedWord.getToken();
                    String pos      = adornedWord.getPartsOfSpeech();
                    String reg      = adornedWord.getStandardSpelling();

                    Element w;
                    boolean isPunct = false;

                    if ( CharUtils.isPunctuation( token ) )
                    {
                        w       = document.createElement( "pc" );
                        isPunct = true;
                    }
                    else
                    {
                        w   = document.createElement( "w" );
                        pos = "#" + pos;

                        w.setAttribute( "lemma" , adornedWord.getLemmata() );
                        w.setAttribute( "ana" , pos );

                        if ( outputReg )
                        {
                            w.setAttribute( "reg" , reg );
                        }
                    }

                    w.setTextContent( token );

                    w.setAttribute( "xml:id" , "w" + ++wordID );

                    if ( melder.shouldOutputBlank( token , ( j == 0 ) ) )
                    {
                        melder.outputBlank();
                    }

                    s.appendChild( w );

                    if ( j >= ( l - 1 ) )
                    {
                        if ( !isPunct )
                        {
                            Element pc  = document.createElement( "pc" );

                            pc.setAttribute
                            (
                                "xml:id" ,
                                "w" + wordID + "-1"
                            );

                            pc.setAttribute( "unit" , "sentence" );

                            s.appendChild( pc );
                        }
                    }
                }

                div.appendChild( s );
            }
        }
        catch ( Exception e )
        {
        }

        return representation;
    }

    /** Return string version of class entry values.
     *
     *  @return     String of class entry values.
     */

    public String toString()
    {
        return stringFromHTML();
    }

    /** Return HTML version of class entry values.
     *
     *  @return     HTML string of class entry values.
     */

    public String toHTML()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "<h3>\n" );

        int nWords  = 0;

        for ( int i = 0 ; i < adornedSentences.size() ; i++ )
        {
            nWords  += adornedSentences.get( i ).size();
        }

        int nSent   = adornedSentences.size();

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

        sb.append( "</h3>\n" );

        sb.append( "<table border=\"0\">\n" );
        sb.append( "<tr>\n" );
        sb.append( "<th align=\"left\">S#</th><th align=\"left\">W#</th>" );
        sb.append( "<th align=\"left\">Spelling</th><th align=\"left\">Pos</th>" );
        sb.append( "<th align=\"left\">Standard</th><th align=\"left\">Lemma</th>" );
        sb.append( "</tr>\n" );

        for ( int i = 0 ; i < nSent ; i++ )
        {
            List<AdornedWord> sentence  = adornedSentences.get( i );

            for ( int j = 0 ; j < sentence.size() ; j++ )
            {
                AdornedWord adornedWord = sentence.get( j );

                String token    = adornedWord.getToken();
                String pos      = adornedWord.getPartsOfSpeech();
                String reg      = adornedWord.getStandardSpelling();
                String lemma    = adornedWord.getLemmata();

                sb.append( "<tr><td>" );
                sb.append( ( i + 1 ) + "" );
                sb.append( "</td><td>" );
                sb.append( ( j + 1 ) + "" );
                sb.append( "</td><td>" );
                sb.append( token );
                sb.append( "</td><td>" );
                sb.append( pos );
                sb.append( "</td><td>" );
                sb.append( reg );
                sb.append( "</td><td>" );
                sb.append( lemma );
                sb.append( "</td></tr>\n" );
            }
        }

        sb.append( "</table>\n" );

        return sb.toString();
    }

    /** Convert results to WebLicht TCF XML.
     *
     *  @return     WebLicht TCF XML formatted results.
     *
     *  @throws     TextCorpusFormatException
     *  @throws     UnsupportedEncodingException
     */

    public Representation toTCF()
        throws TextCorpusFormatException, UnsupportedEncodingException
    {
                                //  Create output stream to string.

        ByteArrayOutputStream fos   = new ByteArrayOutputStream();

                                //  Set layers to write.

        TextCorpusLayerTag[] layersToWrite  =
            new TextCorpusLayerTag[]
            {
                TextCorpusLayerTag.TEXT ,
                TextCorpusLayerTag.TOKENS ,
                TextCorpusLayerTag.LEMMAS ,
                TextCorpusLayerTag.POSTAGS ,
                TextCorpusLayerTag.SENTENCES
            };
                                //  Create empty text corpus
                                //  with layers to write.

        TextCorpusData tc   =
            new TextCorpusData( fos , layersToWrite , "en" );

                                //  Reconstitute text from
                                //  individual sentences and
                                //  pick up individual tokens as well.

        List<Token> tokens          = ListFactory.createNewList();
        List<Lemma> lemmata         = ListFactory.createNewList();
        List<Tag> posTags           = ListFactory.createNewList();
        List<Sentence> tcfSentences = ListFactory.createNewList();

        TextCorpusFactory tcf   = tc.getFactory();

        for ( int i = 0 ; i < adornedSentences.size() ; i++ )
        {
            List<AdornedWord> sentence  = adornedSentences.get( i );

            List<String> tokenRefs  = ListFactory.createNewList();

            for ( int j = 0 ; j < sentence.size() ; j++ )
            {
                AdornedWord word    = sentence.get( j );

                Token token = tcf.createToken( word.getSpelling() );

                tokens.add( token );
                tokenRefs.add( token.getID() );

                Tag tag =
                    tcf.createTag(
                        word.getPartsOfSpeech() , token.getID() );

                posTags.add( tag );

                Lemma lemma =
                    tcf.createLemma(
                        word.getLemmata() , token.getID() );

                lemmata.add( lemma );
            }

            Sentence tcfSentence    = tcf.createSentence( tokenRefs );

            tcfSentences.add( tcfSentence );
        }
                                //  Write reconstituted text.

        tc.writeTextLayer( text );

                                //  Write tokens.

        tc.writeTokensLayer( tokens );

                                //  Write postags.

        tc.writePOSTagsLayer( posTags , "NUPOS" );

                                //  Write lemmata.

        tc.writeLemmasLayer( lemmata );

                                //  Write sentences.

        tc.writeSentencesLayer( tcfSentences );

                                //  Convert TCF XML to String representation.
        return new
            StringRepresentation
            (
                new String( fos.toByteArray() , "UTF-8" ) ,
                MediaType.APPLICATION_XML
            );
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



