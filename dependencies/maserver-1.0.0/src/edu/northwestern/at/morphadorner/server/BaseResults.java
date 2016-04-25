package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.util.List;

import edu.northwestern.at.utils.StringUtils;
import edu.northwestern.at.utils.html.HTMLUtils;

/** Base class for result objects.
 */

public class BaseResults
{
    /** Append string with line feed to a string buffer.
     *
     *  @param  sb  The string buffer.
     *  @param  s   The string to append.
     *
     *  @return     The updated string buffer.
     */

     public static StringBuffer appendLn( StringBuffer sb , String s )
     {
        if ( s != null )
        {
            sb.append( s );
            sb.append( "\n" );
        }

        return sb;
     }

    /** Append string to a string buffer.
     *
     *  @param  sb  The string buffer.
     *  @param  s   The string to append.
     *
     *  @return     The updated string buffer.
     */

    public static StringBuffer append( StringBuffer sb , String s )
    {
        if ( s != null )
        {
            sb.append( s );
        }

        return sb;
    }

    /** Convert tabular data to HTML table.
     *
     *  @param  tabularData                 Tabular data.
     *  @param  firstColumnContainsLabels   true if first column is labels.
     *
     *  @return     HTML formatted tabular data.
     *
     *  <p>
     *  First line of tabular data is title.  May be empty.
     *  </p
     *  <p>
     *  Second line of tabular data is column titles.  May be empty.
     *  </p>
     *  <p>
     *  Third through last lines are tab-separated table values.
     *  May not be supplied if table is empty.
     *  </p>
     */

    public static String tabularDataBaseResults
    (
        String tabularData ,
        boolean firstColumnContainsLabels
    )
    {
                                //  Return empty string if
                                //  no tabular data supplied.

        if ( ( tabularData == null ) || ( tabularData.trim().length() == 0 ) )
        {
            return "";
        }
                                //  Convert input string to
                                //  lines of data.

        String[] lines  = tabularData.split( "\n" );

                                //  Create string buffer to hold
                                //  generated HTML.

        StringBuffer sb = new StringBuffer();

                                //  Emit title if provided.

        String tableTitle   = lines[ 0 ].trim();

        if ( ( tableTitle != null ) && ( tableTitle.length() > 0 ) )
        {
            sb  = appendLn( sb , "<h3>" + tableTitle + "</h3>" );
        }
                                //  Emit tabular data if provided.

        if ( lines.length > 1 )
        {
                                //  Start table.

            sb  = appendLn( sb , "<table border=\"0\">" );

                                //  Emit column titles if any.

            String line1    = lines[ 1 ].trim();

            if ( line1.length() > 0 )
            {
                String[] columnTitles   = line1.split( "\t" );

                if ( columnTitles.length > 0 )
                {
                    sb  = appendLn( sb , "<tr>" );

                    for ( int i = 0 ; i < columnTitles.length ; i++ )
                    {
                                //  Get next column title.

                        String colTitle = columnTitles[ i ].trim();

                                //  If empty. convert to non-breaking
                                //  space for display.

                        if ( colTitle.length() == 0 )
                        {
                            colTitle    = "&nbsp;";
                        }
                                //  Emit column title.

                        sb  =
                            appendLn
                            (
                                sb ,
                                "<th align=\"left\">" +
                                colTitle + "</th>"
                            );
                    }

                    sb  = appendLn( sb , "</tr>" );
                }
            }
                                //  Emit data rows.

            for ( int i = 2 ; i < lines.length ; i++ )
            {
                                //  Get next data row.

                String[] dataRow    = lines[ i ].split( "\t" );

                                //  Convert data row to HTML format.

                sb  = appendLn( sb , "<tr>" );

                for ( int j = 0 ; j < dataRow.length ; j++ )
                {
                                //  Get next data item.

                    String dataValue    = dataRow[ j ].trim();

                                //  If empty. convert to non-breaking
                                //  space for display.

                    if ( dataValue.length() == 0 )
                    {
                        dataValue   = "&nbsp;";
                    }
                                //  If this is first column of data,
                                //  and these are labels, make the
                                //  data values strong for display.

                    else if ( ( j == 0 ) && firstColumnContainsLabels )
                    {
                        dataValue   = "<strong>" + dataValue + "</strong>";
                    }
                                //  Emit data value.
                    sb  =
                        appendLn
                        (
                            sb ,
                            "<td valign=\"top\" align=\"left\">" +
                            dataValue + "</td>"
                        );
                }

                sb  = appendLn( sb , "</tr>" );
            }
                                //  End table.

            sb  = appendLn( sb , "</table>" );
        }
                                //  Return HTML to caller.

        return sb.toString();
    }

    /** Generate string representation from HTML.
     *
     *  @return     String representation.
     */

    public String stringFromHTML()
    {
        String result   = toHTML();

        result  =
            StringUtils.replaceAll
            (
                result ,
                "</td>" ,
                "\t"
            );

        result  =
            StringUtils.replaceAll
            (
                result ,
                "</th>" ,
                "\t"
            );

        result  =
            StringUtils.replaceAll
            (
                result ,
                "\t\n" ,
                "\n"
            );

        result  = HTMLUtils.stripHTMLTags( result );

        result  =
            StringUtils.replaceAll
            (
                result ,
                "\n\n" ,
                "\n"
            );

        return result.trim();
    }

    /** Generate HTML representation of object.
     *
     *  @return     HTML representation.
     */

    public String toHTML()
    {
        return tabularDataBaseResults( toString() , true );
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



