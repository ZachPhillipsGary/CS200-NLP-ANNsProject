package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import org.restlet.data.Status;
import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;
import org.restlet.data.MediaType;

import org.restlet.ext.wadl.*;

import org.restlet.ext.xstream.XstreamRepresentation;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.corpuslinguistics.spellingstandardizer.*;

/** Gap filler resource.
 */

public class GapFillerResource extends BaseAdornerServerResource
{
    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Gap filler resource" );
        setDescription( "Fills gap characters." );
    }

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query params.
        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Get corpus configuration.

        return postResults( queryParams , fillGaps( queryParams ) );
    }

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query params.

        Form queryParams = new Form( representation );

                                //  Return gap filler result.

        return postResults( queryParams , fillGaps( queryParams ) );
    }

    /** Perform gap fill.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Gap filler result.
     */

    public GapFillerResult fillGaps( Form queryParams )
    {
        String spelling     =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.SPELLING ) ).trim();
/*
        spelling    =
            spelling.replaceAll
            (
                "&#9679;" ,
                CharUtils.CHAR_GAP_MARKER_STRING
            );
*/
        spelling    =
            StringUtils.replaceAll
            (
                spelling ,
                "?" ,
                CharUtils.CHAR_GAP_MARKER_STRING
            );

        String corpusConfig =
            StringUtils.safeString(
                queryParams.getFirstValue(
                    QueryParams.CORPUSCONFIG ) ).trim();

                                //  Get which adorner to use.

        MorphAdornerServerInfo adornerInfo  =
            MorphAdornerServerData.getAdornerInfo( corpusConfig );

                                //  Get list of possible gap-filled
                                //  spellings.

        List<String> filteredSuggestions    =
            ListFactory.createNewList();

        if  (   ( spelling.length() > 0 ) &&
                CharUtils.hasGapCharacter( spelling )
            )
        {
            List<String> suggestions    =
                adornerInfo.gapFiller.getMatchingWords( spelling );

            for ( String suggestion : suggestions )
            {
                if ( !CharUtils.hasGapCharacter( suggestion ) )
                {
                    filteredSuggestions.add( suggestion );
                }
            }
        }
        else
        {
            filteredSuggestions.add( spelling );
        }

        return new GapFillerResult(
            spelling, corpusConfig, filteredSuggestions );
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
                QueryParams.SPELLING ,
                true ,
                WADLSTRING ,
                ParameterStyle.QUERY,
                "Spelling containing gaps to fill."
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



