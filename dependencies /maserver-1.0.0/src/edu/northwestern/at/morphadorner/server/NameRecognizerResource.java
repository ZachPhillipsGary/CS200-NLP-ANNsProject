package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import org.restlet.ext.wadl.*;

import edu.northwestern.at.morphadorner.corpuslinguistics.sentencemelder.*;
import edu.northwestern.at.utils.*;

/** Name recognizer resource.
 */

public class NameRecognizerResource extends BaseAdornerServerResource
{
    /** Initialize resource. */

    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Name recognizer resource" );
        setDescription( "Recognizes proper names in text." );
    }

    /** Handle Get request. */

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Recognize names.

        return postResults( queryParams , recognizeNames( queryParams ) );
    }

    /** Handle Post request.
     *
     *  @param  representation  Input form contents.
     */

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Recognize names.

        return postResults( queryParams , recognizeNames( queryParams ) );
    }

    /** Perform name recognition.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Name recognition result.
     */

    public NameRecognizerResult recognizeNames( Form queryParams )
    {
                                //  Get text to scan for names.
        String text =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.TEXT ) ).trim();

        text    = unTag( text );

                                //  Include input text in result.

        boolean includeInputText    =
            queryParams.getFirstValue( QueryParams.INCLUDEINPUTTEXT ) != null;

                                //  Get corpus configuration.

        String corpusConfig =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

                                //  Holds person and place names.

        Set<String>[] namesInText   = null;

                                //  Get names.

        if ( text.length() > 0 )
        {
            namesInText = adornerInfo.nameRecognizer.findNames( text );
        }
                                //  Sort results.

        Set<String> sortedNamesSet  = SetFactory.createNewSortedSet();
        sortedNamesSet.addAll( namesInText[ 0 ] );

        Set<String> sortedPlacesSet = SetFactory.createNewSortedSet();
        sortedPlacesSet.addAll( namesInText[ 1 ] );

                                //  Return extracted names.

        return new NameRecognizerResult
        (
            includeInputText ? text : null ,
            corpusConfig ,
            sortedNamesSet ,
            sortedPlacesSet
        );
    }

    /** Describe the parameters.
     *
     *  @return     Parameters as list of ParameterInfo objects.
     */

    protected List<ParameterInfo> describeParameters()
    {
        List<ParameterInfo> params  = super.describeParameters();

        ParameterInfo param =
            new ParameterInfo
            (
                QueryParams.TEXT ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Text containing names to recognize."
            );

        params.add( param );

        param   =
            new ParameterInfo
            (
                QueryParams.INCLUDEINPUTTEXT ,
                true ,
                WADLBOOLEAN ,
                ParameterStyle.QUERY,
                "Include input text in output."
            );

        params.add( param );

        param =
            new ParameterInfo
            (
                QueryParams.CORPUSCONFIG ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY ,
                "Corpus configuration."
            );

        params.add( param );

        return sortParams( params );
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



