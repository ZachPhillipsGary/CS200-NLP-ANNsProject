package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import edu.northwestern.at.morphadorner.MorphAdornerSettings;
import edu.northwestern.at.morphadorner.server.logging.MorphAdornerServerLogger;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.html.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.hyphenator.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.inflector.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.languagerecognizer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.lemmatizer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.lexicon.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.namerecognizer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.partsofspeech.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.postagger.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.postagger.guesser.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.postagger.transitionmatrix.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.postagger.trigram.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.sentencesplitter.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.spellingmapper.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.spellingstandardizer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.stemmer.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.syllablecounter.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.thesaurus.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.tokenizer.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.servlets.*;

import net.sf.jlinkgrammar.*;

import org.apache.log4j.*;

/** Base data for MorphAdorner restlet based server.
 *
 *  <p>
 *  Also stores common objects used by multiple configurations.
 *  </p>
 */

public class MorphAdornerServerData
{
    /** Server version. */

    protected static String serverVersion           = "1.0.0";

    /** Default data directory. */

    protected static String defaultDataDirectory    = "data";

    /** Data directory. */

    protected static String dataDirectory           = null;

    /** Servlet context. */

    protected static ServletContext context         = null;

    /** Maximum unadorned upload file size. 5 megabytes by default. */

    protected static int maxUnadornedUploadFileSize = 5*1024*1024;

    /** Maximum adorned upload file size. 50 megabytes by default. */

    protected static int maxAdornedUploadFileSize   = 50*1024*1024;

    /** The lemmatizer. */

    protected static Lemmatizer lemmatizer;

    /** Porter stemmer. */

    protected static Stemmer porterStemmer      = new PorterStemmer();

    /** Lancaster stemmer. */

    protected static Stemmer lancasterStemmer   = new LancasterStemmer();

    /** Names. */

    protected static Names names                = new Names();

    /** The language recognizer. */

    protected static LanguageRecognizer languageRecognizer  = null;

    /** English inflector. */

    protected static Inflector inflector        = new EnglishInflector();

    /** British to US spelling mapper. */

    protected static SpellingMapper britishToUS =
        new BritishToUSSpellingMapper();

    /** Link grammar dictionary. */

    protected static net.sf.jlinkgrammar.Dictionary dictionary ;

    /** Link grammar parser options. */

    protected static ParseOptions parseOptions ;

    /** Link grammar parser data file directory. */

    protected static String lgParserDataDirectory   = "lgparser";

    /** Wordnet data file directory. */

    protected static String wordnetDataDirectory    = "wordnet/3.0/dict";

    /** WordNet thesaurus. */

    protected static Thesaurus thesaurus;

    /** British rules hyphenator. */

    protected static Hyphenator britishHyphenator   =
        new BritishHyphenator();

    /** American rules hyphenator. */

    protected static Hyphenator americanHyphenator  =
        new AmericanHyphenator();

    /** Syllable counter. */

    protected static SyllableCounter syllableCounter    =
        new DefaultSyllableCounter();

    /** Corpus configurations. */

    protected static Map<String, MorphAdornerServerInfo> corpusConfigs  =
        MapFactory.createNewSortedMap();

    /** First corpus configuration for extraction version information. */

    protected static MorphAdornerServerInfo refAdornerInfo  = null;

    /** Initialization states. */

    protected static final int INITNOTSTARTED   = 0;
    protected static final int INITINPROGRESS   = 1;
    protected static final int INITDONE         = 2;
    protected static final int INITFAILED       = 3;

    /** Initialization complete. */

    protected static int initializationStatus   = INITNOTSTARTED;

    /** Servlet not ready message. */

    protected static final String servletNotReadyMessage    =
        "MorphAdorner server not yet ready, please try again in a minute.";

    /** Servlet not ready title. */

    protected static final String servletNotReadyTitle  =
        "MorphAdorner server not ready";

    /** Initialize common objects.
     */

    public synchronized static void initialize()
    {
                                //  If init done or in progress,
                                //  do nothing.

        if ( initializationStatus != INITNOTSTARTED ) return;

        try
        {
                                //  Remember initialization has started.

            initializationStatus    = INITINPROGRESS;

                                //  Initialize logging.

            MorphAdornerServerLogger.logger.log
            (
                Level.INFO ,
                "MorphAdorner server initialization started."
            );

            dataDirectory   = new File( dataDirectory ).getCanonicalPath();

            MorphAdornerServerLogger.logger.log
            (
                Level.INFO ,
                "MorphAdorner Server data directory is " +
                dataDirectory
            );

            MorphAdornerServerLogger.logger.log
            (
                Level.INFO ,
                "Maximum unadorned file size upload is " +
                Formatters.formatIntegerWithCommas(
                    maxUnadornedUploadFileSize )
            );

            MorphAdornerServerLogger.logger.log
            (
                Level.INFO ,
                "Maximum adorned file size upload is " +
                Formatters.formatIntegerWithCommas(
                    maxAdornedUploadFileSize )
            );

            lgParserDataDirectory =
                makeFileName( dataDirectory , lgParserDataDirectory );

            wordnetDataDirectory  =
                makeFileName( dataDirectory , wordnetDataDirectory );

                                //  Create language recognizer.

            languageRecognizer  = new DefaultLanguageRecognizer();

                                //  Get list of files in the data
                                //  directory.

            File fDataDir           = new File( dataDirectory );

            File[] filesAndDirs     = fDataDir.listFiles();

                                //  Process each file.

            for ( int i = 0 ; i < filesAndDirs.length ; i++ )
            {
                                //  Get next file.

                File file   = filesAndDirs[ i ];

                                //  If it's a directory, skip it.

                if ( !file.isFile() ) continue;

                                //  Get the file name.
                                //  If the extension is not
                                //  ".properties", skip it.

                String fileName     = file.getAbsolutePath();
                String extension    =
                    FileNameUtils.getFileExtension( fileName , false );

                if ( !extension.equalsIgnoreCase( "properties" ) ) continue;

                                //  Found a configuration file.
                                //  Load the properties it contains.

                UTF8Properties corpusProperties =
                    UTF8PropertyUtils.loadUTF8Properties( fileName );

                                //  Get corpus name and description.

                String configName   =
                    corpusProperties.getProperty(
                        "corpus.name" , "" ).trim();

                String configDescription    =
                    corpusProperties.getProperty(
                        "corpus.description" , "" ).trim();

                                //  Create adorner info entry if the
                                //  config name and description are
                                //  not empty.

                if  (   ( configName.length() > 0 ) &&
                        ( configDescription.length() > 0 )
                    )
                {
                    MorphAdornerServerInfo adornerInfo  =
                        new MorphAdornerServerInfo
                        (
                            configName ,
                            configDescription ,
                            fileName
                        );

                    corpusConfigs.put( configName , adornerInfo );

                    if ( refAdornerInfo == null )
                    {
                        refAdornerInfo  = adornerInfo;
                    }
                }
            }
                                //  Log error if not corpus configurations
                                //  were created.

            if ( corpusConfigs.size() == 0 )
            {
                MorphAdornerServerLogger.logger.log
                (
                    Level.ERROR ,
                    "No corpus configurations found."
                );

                initializationStatus    = INITFAILED;
            }
                                //  Get lemmatizer.

            if ( lemmatizer == null )
            {
                lemmatizer  = new DefaultLemmatizer();

                if ( refAdornerInfo != null )
                {
                    lemmatizer.setDictionary
                    (
                        refAdornerInfo.adorner.spellingStandardizer.getStandardSpellings()
                    );
                }
            }
                                //  Get link grammar parser.

            if ( parseOptions == null )
            {
                parseOptions = new ParseOptions() ;

                parseOptions.parse_options_set_short_length( 10 ) ;
                parseOptions.parse_options_set_max_null_count( 10 ) ;
                parseOptions.parse_options_set_linkage_limit( 100 ) ;
            }

            if ( dictionary == null )
            {
                dictionary =
                    new net.sf.jlinkgrammar.Dictionary
                    (
                        parseOptions ,
                        lgParserDataDirectory + "/4.0.dict" ,
                        "4.0.knowledge" ,
                        "4.0.constituent-knowledge" ,
                        "4.0.affix"
                    ) ;
            }
                                //  Create thesaurus.
            try
            {
                thesaurus   =
                    new WordnetThesaurus( wordnetDataDirectory );
            }
            catch ( Exception e )
            {
                MorphAdornerServerLogger.logger.log
                (
                    Level.ERROR ,
                    DebugUtils.getStackTrace( e )
                );

                initializationStatus    = INITFAILED;
            }
                                //  Initialization complete.

            if ( initializationStatus != INITFAILED )
            {
                initializationStatus    = INITDONE;

                MorphAdornerServerLogger.logger.log
                (
                    Level.INFO ,
                    "MorphAdorner server initialization finished."
                );
            }
        }
        catch ( Exception e )
        {
            MorphAdornerServerLogger.logger.log
            (
                Level.ERROR ,
                DebugUtils.getStackTrace( e )
            );

            initializationStatus    = INITFAILED;
        }

        if ( initializationStatus == INITFAILED )
        {
            MorphAdornerServerLogger.logger.log
            (
                Level.ERROR ,
                "MorphAdorner server initialization failed."
            );
        }
    }

    /** Make a file name from a parent directory and file name.
     *
     *  @param  directoryName   Parent directory name.
     *  @param  fileName        File name.
     *
     *  @return                 Combined directory and file name.
     */

    public synchronized static String makeFileName
    (
        String directoryName ,
        String fileName
    )
        throws SecurityException
    {
        return new File( directoryName , fileName ).getAbsolutePath();
    }

    /** Make a file name from a parent directory and file name.
     *
     *  @param  directory       Parent directory.
     *  @param  fileName        File name.
     *
     *  @return                 Combined directory and file name.
     */

    public synchronized static String makeFileName
    (
        File directory ,
        String fileName
    )
        throws SecurityException
    {
        return new File( directory , fileName ).getAbsolutePath();
    }

    /** Select adorner to use.
     *
     *  @param  adornerName Adorner name.
     *
     *  @return             AdornerInfo for specified adorner.
     */

    public static MorphAdornerServerInfo getAdornerInfo( String adornerName )
    {
        return corpusConfigs.get( adornerName );
    }

    /** Check if servlet ready for use.
     *
     *  @return     true if servlet ready for use.
     */

    public static boolean isReady()
    {
        return ( initializationStatus == INITDONE );
    }

    /** Gets integer parameter value.
     *
     *  @param  requestValue    Parameter value from request.
     *  @param  defaultValue    Default parameter value if parameter null
     *                          or invalid.
     *
     *  @return                 The parameter value, or the defaultValue
     *                          if paramValue is null or invalid.
     */

    public static int getIntValue
    (
        String requestValue ,
        int defaultValue
    )
    {
        int result  = defaultValue;

        if ( requestValue != null )
        {
            try
            {
                result  = Integer.parseInt( requestValue );
            }
            catch ( NumberFormatException e )
            {
                result  = defaultValue;
            }
        }

        return result;
    }

    /** Get MorphAdorner program version.
     *
     *  @return     The MorphAdorner program version.
     */

    public static String getMorphAdornerVersion()
    {
        String result   = "Not available";

        if  (   ( refAdornerInfo != null ) &&
                ( refAdornerInfo.adorner != null )
            )
        {
            result  =
                refAdornerInfo.adorner.morphAdornerSettings.
                    getMorphAdornerVersion();
        }

        return result;
    }

    /** Get server version.
     *
     *  @return     The server version.
     */

    public static String getServerVersion()
    {
        return serverVersion;
    }

    /** Get the data directory.
     *
     *  @return     The data directory.
     */

    public static String getDataDirectory()
    {
        return dataDirectory;
    }

    /** Set the data directory.
     *
     *  @param  dataDir     The data directory.
     */

    public static void setDataDirectory( String dataDir )
    {
        dataDirectory   = dataDir;
    }

    /** Convert string file size to integer value.
     *
     *  @param  sFileSize   String file size.  May have "k", "m", "g"
     *                      following integer value to specify
     *                      kilobytes, megabytes, or gigabytes,
     *                      respectively.
     *
     *  @return             Integer file size.  -1 if file size cannot
     *                      be found because the input value was bad.
     */

    public static int convertStringFileSize( String sFileSize )
    {
        int fileSize    = -1;

        if ( sFileSize != null )
        {
            int multiplier  = 1;

            String fileSizeString   = sFileSize.toLowerCase();

            if ( fileSizeString.endsWith( "g" ) )
            {
                multiplier  = 1024*1024*1024;
                fileSizeString  =
                    fileSizeString.substring(
                        0 , fileSizeString.length() - 1 );
            }
            else if ( fileSizeString.endsWith( "m" ) )
            {
                multiplier  = 1024*1024;
                fileSizeString  =
                    fileSizeString.substring(
                        0 , fileSizeString.length() - 1 );
            }
            else if ( fileSizeString.endsWith( "k" ) )
            {
                multiplier  = 1024;
                fileSizeString  =
                    fileSizeString.substring(
                        0 , fileSizeString.length() - 1 );
            }
                                //  Convert given file size to
                                //  number of bytes.  If this fails,
                                //  leave the maximum upload file size
                                //  set at the default value.
            try
            {
                fileSize    =
                    Integer.parseInt( fileSizeString ) * multiplier;
            }
            catch ( Exception e )
            {
                fileSize    = -1;
            }
        }

        return fileSize;
    }

    /** Set the maximum unadorned upload file size.
     *
     *  @param  maxFileSize     Maximum unadorned upload file size.
     */

    public static void setMaxUnadornedUploadFileSize( String maxFileSize )
    {
        int fileSize    = convertStringFileSize( maxFileSize );

        if ( fileSize >= 0 )
        {
            maxUnadornedUploadFileSize  = fileSize;
        }
    }

    /** Get the maximum unadorned upload file size.
     *
     *  @return     The maximum unadorned upload file size.
     */

    public static int getMaxUnadornedUploadFileSize()
    {
        return maxUnadornedUploadFileSize;
    }

    /** Set the maximum adorned upload file size.
     *
     *  @param  maxFileSize     Maximum adorned upload file size.
     */

    public static void setMaxAdornedUploadFileSize( String maxFileSize )
    {
        int fileSize    = convertStringFileSize( maxFileSize );

        if ( fileSize >= 0 )
        {
            maxAdornedUploadFileSize    = fileSize;
        }
    }

    /** Get the maximum adorned upload file size.
     *
     *  @return     The maximum adorned upload file size.
     */

    public static int getMaxAdornedUploadFileSize()
    {
        return maxAdornedUploadFileSize;
    }

    /** Get the context.
     *
     *  @return     The context.
     */

    public static ServletContext getContext()
    {
        return context;
    }

    /** Set the context.
     *
     *  @param  theContext      The context.
     */

    public static void setContext( ServletContext theContext)
    {
        context = theContext;
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



