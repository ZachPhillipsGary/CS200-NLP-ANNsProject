package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import edu.northwestern.at.morphadorner.*;

import edu.northwestern.at.morphadorner.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.namestandardizer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.spellingstandardizer.*;

import edu.northwestern.at.morphadorner.MorphAdornerSettings;
import edu.northwestern.at.morphadorner.server.logging.MorphAdornerServerLogger;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;

import org.apache.log4j.*;

/** Adorner information for MorphAdorner restlet-based server.
 */

public class MorphAdornerServerInfo
{
    /** The server info name. */

    public String serverInfoName;

    /** The server info description. */

    public String serverInfoDescription;

    /** The adorner. */

    public MorphAdorner adorner;

    /** Gap filler. */

    public GapFiller gapFiller;

    /** The name recognizer. */

    public NameRecognizer nameRecognizer;

    /** The sentence splitter. */

    public SentenceSplitter sentenceSplitter;

    /** Simple spelling standardizer. */

    public ExtendedSimpleSpellingStandardizer simpleStandardizer;

    /** Extended search spelling standardizer. */

    public ExtendedSearchSpellingStandardizer standardizer;

    /** Name standardizer. */

    public NameStandardizer nameStandardizer;

    /** Maps lemma to list of spellings in lexicon. */

    public KeyedSets<String, String> lemmaToSpellings;

    /** Create adorner info object.
     *
     *  @param  serverInfoName              Name for this config.
     *  @param  serverInfoDescription       Description for this config.
     *  @param  configFileName              Configuration file name.
     */

    public MorphAdornerServerInfo
    (
        String serverInfoName ,
        String serverInfoDescription ,
        String configFileName
    )
        throws Exception
    {
                                //  Log creation of this configuration.

        MorphAdornerServerLogger.logger.log
        (
            Level.INFO ,
            "Creating MorphAdorner configuration " + serverInfoName +
            " from " + configFileName
        );
                                //  Save server info name.

        this.serverInfoName = serverInfoName;

                                //  Save server info description.

        this.serverInfoDescription  = serverInfoDescription;

                                //  Create MorphAdorner arguments.

        List<String> morphArgs  = new ArrayList<String>();

                                //  Get server data directory.

        String ddir = MorphAdornerServerData.dataDirectory;

                                //  Add configuration settings file parameter.

        morphArgs.add( "-p" );
        morphArgs.add( configFileName );

                                //  Add default settings file parameter.
        morphArgs.add( "-d" );
        morphArgs.add
        (
            MorphAdornerServerData.makeFileName
            (
                ddir ,
                "morphadorner.properties"
            )
        );
                                //  Convert parameters list to array.

        String[] morphArgsArray =
            (String[])morphArgs.toArray( new String[ morphArgs.size() ] );

                                //  Get current directory.

        String currentDirectory = FileUtils.getCurrentDirectory();

                                //  Change to data directory.

        FileUtils.chdir( ddir );

                                //  Create the adorner.
        this.adorner    =
            MorphAdorner.createAdorner
            (
                serverInfoName ,
                false ,
                morphArgsArray ,
                null ,
                null
            );
                                //  Change back to original directory.

        FileUtils.chdir( currentDirectory );

                                //  Map lemmata to spellings in
                                //  word lexicon.

        String[] spellings  = adorner.wordLexicon.getEntries();

        lemmaToSpellings    = new KeyedSets<String, String>();

        for ( int i = 0 ; i < spellings.length ; i++ )
        {
            String spelling     = spellings[ i ];
            String[] lemmata    =
                adorner.wordLexicon.getLemmata( spelling );

            for ( int j = 0 ; j < lemmata.length ; j++ )
            {
                lemmaToSpellings.add( lemmata[ j ] , spelling );
            }
        }
                                //  Create gap filler.
        gapFiller   =
            new GapFiller
            (
                adorner.spellingStandardizer.getMappedSpellings()
            );
                                //  Add standard spellings.
        gapFiller.addWords
        (
            adorner.spellingStandardizer.getStandardSpellings()
        );
                                //  Add words in lexicon.

        gapFiller.addWords( adorner.wordLexicon.getEntries() );

                                //  Create name recognizer.

        nameRecognizer  = new DefaultNameRecognizer();

        nameRecognizer.setPartOfSpeechTagger( adorner.tagger );

                                //  Create sentence splitter.

        sentenceSplitter    = new DefaultSentenceSplitter();

                                //  Set guesser into sentence splitter.

        sentenceSplitter.setPartOfSpeechGuesser
        (
            adorner.partOfSpeechGuesser
        );

        sentenceSplitter.setAbbreviations( adorner.abbreviations );

                                //  Get extended search standardizer.

        standardizer    = new ExtendedSearchSpellingStandardizer();

                                //  Load standard spellings.

        standardizer.setStandardSpellings
        (
            adorner.spellingStandardizer.getStandardSpellings()
        );
                                //  Add name lists to standard spellings.

        standardizer.addStandardSpellings(
            MorphAdornerServerData.names.getFirstNames() );

        standardizer.addStandardSpellings(
            MorphAdornerServerData.names.getSurnames() );

        standardizer.addStandardSpellings(
            MorphAdornerServerData.names.getPlaceNames().keySet() );

                                //  Load alternate/standard spelling pairs.

        standardizer.setMappedSpellings
        (
            adorner.spellingStandardizer.getMappedSpellings()
        );
                                //  Create dictionaries for standardizer.

        standardizer.createDictionaries();

                                //  Get simple spelling standardizer.

        simpleStandardizer  = new ExtendedSimpleSpellingStandardizer();

                                //  Set pairs list into simple
                                //  standardizer as well.

        simpleStandardizer.setMappedSpellings(
            adorner.spellingStandardizer.getMappedSpellings() );

        simpleStandardizer.setStandardSpellings(
            adorner.spellingStandardizer.getStandardSpellings() );

                                //  Set gap filler into spelling
                                //  standardizers.

        standardizer.setGapFiller( gapFiller );

        simpleStandardizer.setGapFiller( gapFiller);

                                //  Create name standardizer.

        nameStandardizer    = new DefaultNameStandardizer();

        nameStandardizer.loadNamesFromLexicon( adorner.wordLexicon );

        nameStandardizer.loadNames(
            MorphAdornerServerData.names.getFirstNames() );

        nameStandardizer.loadNames(
            MorphAdornerServerData.names.getSurnames() );

        nameStandardizer.loadNames(
            MorphAdornerServerData.names.getPlaceNames().keySet() );
    }

    /** Return description of this configuration.
     *
     *  @return     Description of this configuration.
     */

    public String toString()
    {
        return serverInfoDescription;
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



