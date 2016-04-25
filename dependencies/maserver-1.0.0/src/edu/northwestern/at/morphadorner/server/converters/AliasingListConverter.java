package edu.northwestern.at.morphadorner.server.converters;

/*  Please see the license information at the end of this file. */

import java.util.*;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

/** Change the alias of an element in a list.
 *
 *  <p>
 *  Minor modification of the sample code posted by Joerg Schaible in a
 *  September 19, 2008 message on the java.xstream.user message board.
 *  </p>
 */

public class AliasingListConverter implements Converter
{
    /** Typeof list to convert. */

    protected Class<?> type;

    /** Alias for list entries. */

    protected String alias;

    /** Create converter.
     *
     *  @param  type    Type of list to convert.
     *  @param  alias   Alias for list entries.
     */

    public AliasingListConverter( Class<?> type , String alias )
    {
        this.type   = type;
        this.alias  = alias;
    }

    /** Check that this converter can actually convert the specified list.
     *
     *  @param  type    Type of list to convert.
     */

    @SuppressWarnings( "unchecked" )
    public boolean canConvert( Class type )
    {
        return List.class.isAssignableFrom( type );
    }

    /** Marshall the list to a writer.
     *
     *  @param  source  The list to convert.
     *  @param  writer  Writer to which to marshall converted list.
     *  @param  context Marshalling context.
     */

    public void marshal
    (
        Object source ,
        HierarchicalStreamWriter writer ,
        MarshallingContext context
    )
    {
        List<?> list    = (List<?>)source;

        for ( Iterator<?> iter  = list.iterator() ; iter.hasNext(); )
        {
            Object elem = iter.next();

            if ( !elem.getClass().isAssignableFrom( type ) )
            {
                throw new ConversionException
                (
                    "Found " + elem.getClass() + ", expected to find: " +
                    this.type + " in List."
                );
            }

            ExtendedHierarchicalStreamWriterHelper.startNode
            (
                writer ,
                alias ,
                elem.getClass()
            );

            context.convertAnother( elem );

            writer.endNode();
        }
    }

    /** Unmarshall the list from a reader.
     *
     *  @param  reader      The reader from which to unmarshall the list.
     *  @param  context     The unmarshalling context.
     */

    @SuppressWarnings( "unchecked" )
    public Object unmarshal
    (
        HierarchicalStreamReader reader ,
        UnmarshallingContext context
    )
    {
        List list = new ArrayList();

        while ( reader.hasMoreChildren() )
        {
            reader.moveDown();
            list.add( context.convertAnother( list, type ) );
            reader.moveUp();
        }

        return list;
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



