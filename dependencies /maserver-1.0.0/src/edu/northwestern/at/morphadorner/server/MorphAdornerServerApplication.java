package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.restlet.Application;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.*;
import org.restlet.ext.xml.TransformRepresentation;
import org.restlet.representation.*;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.resource.Directory;

import edu.northwestern.at.morphadorner.server.logging.*;

import edu.northwestern.at.utils.FileUtils;
import edu.northwestern.at.utils.StringUtils;

/** MorphAdorner restlet-based server application. */

public class MorphAdornerServerApplication extends WadlApplication
{
    /** Create application. */

    public MorphAdornerServerApplication()
    {
        setName( "MorphAdorner Services Server Application" );
        setDescription( "MorphAdorner web services" );
        setOwner( "Northwestern University" );
        setAuthor( "Philip R. Burns" );

                                //  Allow multipart form input.

        getMetadataService().addExtension
        (
            "multipart" ,
            MediaType.MULTIPART_FORM_DATA ,
            true
        );
    }

    /** Creates a root Restlet that will receive all incoming calls.
     */

    @Override
    public synchronized Restlet createInboundRoot()
    {
                                //  Initialize server data.

        MorphAdornerServerData.initialize();

                                //  Create a router Restlet that routes
                                //  each call to a new instance of
                                //  the MorphAdorner server resources.

        Router router   = new Router( getContext() );

                                //  Static pages resource.

        final Directory staticPagesDirectory    =
            new Directory( getContext() , getRootURI( getContext() ) );

        staticPagesDirectory.setListingAllowed( true );
        staticPagesDirectory.setModifiable( false );

        router.attachDefault( staticPagesDirectory );

                                //  Corpus config resource.
        router.attach
        (
            "/corpusconfig" ,
            CorpusConfigResource.class
        );
                                //  Gap filler resource.
        router.attach
        (
            "/gapfiller" ,
            GapFillerResource.class
        );
                                //  Hyphenator resource.
        router.attach
        (
            "/hyphenator" ,
            HyphenatorResource.class
        );
                                //  Language recognizer resource.
        router.attach
        (
            "/languagerecognizer" ,
            LanguageRecognizerResource.class
        );

        router.attach
        (
            "/languagerecogniser" ,
            LanguageRecogniserResource.class
        );
                                //  Lemmatizer resource.
        router.attach
        (
            "/lemmatizer" ,
            LemmatizerResource.class
        );

        router.attach
        (
            "/lemmatiser" ,
            LemmatiserResource.class
        );
                                //  Lexicon lookup resource.
        router.attach
        (
            "/lexiconlookup" ,
            LexiconLookupResource.class
        );
                                //  Name recognizer resource.
        router.attach
        (
            "/namerecognizer" ,
            NameRecognizerResource.class
        );

        router.attach
        (
            "/namerecogniser" ,
            NameRecognizerResource.class
        );
                                //  Parser resource.
        router.attach
        (
            "/parser" ,
            ParserResource.class
        );
                                //  Part of speech tagger resource.
        router.attach
        (
            "/partofspeechtagger" ,
            PartOfSpeechTaggerResource.class
        );
                                //  Pluralizer resource.
        router.attach
        (
            "/pluralizer" ,
            PluralizerResource.class
        );

        router.attach
        (
            "/pluraliser" ,
            PluralizerResource.class
        );
                                //  Sentence splitter resource.
        router.attach
        (
            "/sentencesplitter" ,
            SentenceSplitterResource.class
        );
                                //  Spelling standardizer resource.
        router.attach
        (
            "/spellingstandardizer" ,
            SpellingStandardizerResource.class
        );

        router.attach
        (
            "/spellingstandardiser" ,
            SpellingStandardizerResource.class
        );
                                //  Summarizer resource.
        router.attach
        (
            "/summarizer" ,
            SummarizerResource.class
        );

        router.attach
        (
            "/summariser" ,
            SummarizerResource.class
        );
                                //  Syllable counter resource.
        router.attach
        (
            "/syllablecounter" ,
            SyllableCounterResource.class
        );
                                //  TEI to sentences.
        router.attach
        (
            "/teiadornedtosentences" ,
            TEIAdornedToSentencesResource.class
        );
                                //  TEI to tabular format.
        router.attach
        (
            "/teiadornedtotabularformat" ,
            TEIAdornedToTabularFileResource.class
        );
                                //  TEI apply changes to adorned/tokenized file.
        router.attach
        (
            "/teiapplychangestoadornedfile" ,
            TEIApplyChangesToAdornedFileResource.class
        );
                                //  TEI compare adorned files resource.
        router.attach
        (
            "/teicompareadornedfiles" ,
            TEICompareAdornedFilesResource.class
        );
                                //  TEI to plain text.
        router.attach
        (
            "/teitotext" ,
            TEIToTextResource.class
        );
                                //  TEI adorner resource.
        router.attach
        (
            "/teiadorner" ,
            TEIAdornerResource.class
        );
                                //  TEI notes mover.
        router.attach
        (
            "/teinotesmover" ,
            TEINotesMoverResource.class
        );
                                //  TEI tokenizer.
        router.attach
        (
            "/teitokenizer" ,
            TEITokenizerResource.class
        );

        router.attach
        (
            "/teitokeniser" ,
            TEITokenizerResource.class
        );
                                //  TEI unadorner resource.
        router.attach
        (
            "/teiunadorner" ,
            TEIUnadornerResource.class
        );
                                //  Text segmenter resource.
        router.attach
        (
            "/textsegmenter" ,
            TextSegmenterResource.class
        );
                                //  Thesaurus resource.
        router.attach
        (
            "/thesaurus" ,
            ThesaurusResource.class
        );
                                //  Verb conjugator resource.
        router.attach
        (
            "/verbconjugator" ,
            VerbConjugatorResource.class
        );
                                //  Version resource.
        router.attach
        (
            "/version" ,
            VersionResource.class
        );
                                //  Word tokenizer resource.
        router.attach
        (
            "/wordtokenizer" ,
            WordTokenizerResource.class
        );

        router.attach
        (
            "/wordtokeniser" ,
            WordTokenizerResource.class
        );

        return router;
    }

    /** Find the root URI for serving static content.
     *
     *  @param      context     The Restlet context.
     *
     *  @return     The root URI.
     *
     *  <p>
     *  Works either within a servlet container or in the standalone
     *  Restlet container.
     *  </p>
     */

    protected String getRootURI( Context context )
    {
                                //  Get servlet context if it exists.

        ServletContext servletContext   =
            (ServletContext)getContext().getAttributes().
                get( "org.restlet.ext.servlet.ServletContext" );

        String rootURI  = "";

        if ( servletContext == null )
        {
                                //  If the servlet context path is null,
                                //  assume we're running in
                                //  the standalone Restlet server.
                                //  Use the "webpages" subdirectory
                                //  as the root for the static content.
            rootURI =
                new File
                (
                    FileUtils.getCurrentDirectory() ,
                    "webpages"
                ).getAbsolutePath();

            rootURI = "file:///" + rootURI + File.separator;
        }
        else
        {
                                //  Servlet context exists.
                                //  Use it to get real path of
                                //  current directory and create
                                //  root URI for static web pages.

            String path = servletContext.getRealPath( "." );
            int last    = path.lastIndexOf( "." );

            rootURI =
                "file:///" +
                path.substring( 0 , last ).replace( '\\' , '/' );
        }

        return rootURI;
    }

    /** Creates a new HTML representation for a given {@link WadlServerResource}
     *  instance describing a server resource.
     *
     *  @param  applicationInfo     The application description.
     *
     *  @return     The created {@link WadlRepresentation}.
     *
     *  <p>
     *  The built-in restlet WADL to HTML transformation is broken,
     *  so we replace it commpletely.
     *  </p>
     */

    protected Representation createHtmlRepresentation
    (
        ApplicationInfo applicationInfo
    )
    {
        Representation result   = null;

        try
        {
            result  =
                WADLConverter.createHtmlRepresentation
                (
                    createWadlRepresentation( applicationInfo ).getText()
                );
        }
        catch ( Exception e )
        {
        }

        return result;
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



