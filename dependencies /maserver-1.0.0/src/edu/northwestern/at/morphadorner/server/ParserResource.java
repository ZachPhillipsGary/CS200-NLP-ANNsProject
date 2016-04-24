package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;
import java.util.StringTokenizer;

import org.restlet.representation.*;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Form;

import org.restlet.ext.wadl.*;
import org.restlet.ext.xstream.XstreamRepresentation;

import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.XStream;

import net.sf.jlinkgrammar.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.morphadorner.server.MorphAdornerServerData;

/** Parser resource.
 */

public class ParserResource extends BaseAdornerServerResource
{
    @Override
    protected void doInit()
        throws ResourceException
    {
        setName( "Parser resource" );
        setDescription( "Parses English text using link grammer parser." );
    }

    @Get("txt|html|xml|json")
    public Representation handleGet()
    {
                                //  Get query parameters.

        Form queryParams    =
            new Form( getRequest().getOriginalRef().getQuery() );

                                //  Return parser results.

        return postResults( queryParams , parseText( queryParams ) );
    }

    @Post("form:txt|html|xml|json")
    public Representation handlePost( Representation representation )
    {
                                //  Get query parameters.

        Form queryParams = new Form( representation );

                                //  Return parser results.

        return postResults( queryParams , parseText( queryParams ) );
    }

    /** Parse sentence text.
     *
     *  @param  queryParams     Query parameters.
     *
     *  @return                 Parser result.
     */

    public ParserResult parseText( Form queryParams )
    {
                                //  Get text to parse.
        String text =
            StringUtils.safeString(
                queryParams.getFirstValue( QueryParams.TEXT ) ).trim();

        text    = unTag( text );

                                //  Include input text in result.

        boolean includeInputText    =
            queryParams.getFirstValue( QueryParams.INCLUDEINPUTTEXT ) != null;

                                //  Parse sentence text.

        String parsedText   = "No text to parse.";

        if ( text.length() > 0 )
        {
            Sentence sentence = parse( text ) ;

            if ( sentence.sentence_num_linkages_found() < 1 )
            {
                parsedText  = "No linkage was found." ;
            }
            else
            {
                Linkage link = getLinkage( sentence , 0 ) ;

                parsedText  = link.linkage_print_diagram() + "\n";

                parsedText  =
                    parsedText +
                        fixOutput
                        (
                            link.linkage_print_links_and_domains()
                        );
            }
        }
                                //  Create results object.

        return
            new ParserResult
            (
                includeInputText ? text : null ,
                parsedText
            );
    }

    public Linkage getLinkage( Sentence sentence , int index )
    {
        return new Linkage(
            index , sentence , MorphAdornerServerData.parseOptions ) ;
    }

    public Sentence parse( String s )
    {
        Sentence sentence =
            new Sentence(
                s ,
                MorphAdornerServerData.dictionary ,
                MorphAdornerServerData.parseOptions ) ;

        sentence.sentence_parse( MorphAdornerServerData.parseOptions ) ;

        return sentence ;
    }

    protected static String fixOutput( String s )
    {
        int[] colWidths = new int[ 6 ];

        for ( int i =0 ; i < colWidths.length ; i++ )
        {
            colWidths[ i ]  = 0;
        }

        String[] lines  = s.split( "\n" );

        StringBuffer sb = new StringBuffer();

        for ( int i = 0 ; i < lines.length ; i++ )
        {
            String line = lines[ i ];

            if ( !line.equals( "\n" ) )
            {
                StringTokenizer tokenizer   =
                    new StringTokenizer( line );

                int j   = 0;

                if ( line.charAt( 0 ) == ' ' )
                {
                    j++;
                }

                while ( tokenizer.hasMoreTokens() )
                {
                    String token    = tokenizer.nextToken().trim();

                    colWidths[ j ]  =
                        Math.max( colWidths[ j ] , token.length() );

                    j++;
                }
            }
        }

        for ( int i = 0 ; i < colWidths.length ; i++ )
        {
            colWidths[ i ] += 2;
        }

        for ( int i = 0 ; i < lines.length ; i++ )
        {
            String line = lines[ i ];

            if ( !line.equals( "\n" ) )
            {
                StringTokenizer tokenizer   =
                    new StringTokenizer( line );

                int j   = 0;

                if ( line.charAt( 0 ) == ' ' )
                {
                    sb.append( StringUtils.dupl( " " ,  colWidths[ j++ ] ) );
                }

                while ( tokenizer.hasMoreTokens() )
                {
                    String token    = tokenizer.nextToken();

                    sb.append( StringUtils.rpad( token , colWidths[ j++ ] ) );
                }

                sb.append( "\n" );
            }
        }

        return sb.toString();
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
                "Text to parse."
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



