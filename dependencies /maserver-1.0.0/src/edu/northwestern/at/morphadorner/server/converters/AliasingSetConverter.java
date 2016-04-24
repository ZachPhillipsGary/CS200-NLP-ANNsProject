package edu.northwestern.at.morphadorner.server.converters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This Converter allows changing the alias of an element in a set.
 * <p>
 * Usage:
 * <p><code>
 * &nbsp;&nbsp;&nbsp;xstream.registerLocalConverter( &lt;class containing set&gt;, "setOfStrings", new AliasingSetConverter( String.class, "value"));
 * </code>
 * <p>
 * NOTE: only tested with sets of Strings.
 *
 */
public class AliasingSetConverter
    implements Converter
{

    /**
     * The type of object set is expected to convert.
     */
    private Class<?> type;

    /**
     *
     */
    private String alias;

    public AliasingSetConverter( Class<?> type, String alias )
    {
        this.type = type;
        this.alias = alias;
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public boolean canConvert( Class type )
    {
        return Set.class.isAssignableFrom( type );
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    public void marshal( Object source, HierarchicalStreamWriter writer, MarshallingContext context )
    {
        Set<?> set = (Set<?>) source;
        for ( Iterator<?> iter = set.iterator(); iter.hasNext(); )
        {
            Object elem = iter.next();
            if ( !elem.getClass().isAssignableFrom( type ) )
            {
                throw new ConversionException( "Found "+elem.getClass() +", expected to find: "+ this.type +" in Set." );
            }

            ExtendedHierarchicalStreamWriterHelper.startNode(writer, alias, elem.getClass());
            context.convertAnother(elem);
            writer.endNode();
        }
    }

    /* (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @SuppressWarnings( "unchecked" )
    public Object unmarshal( HierarchicalStreamReader reader, UnmarshallingContext context )
    {
        Set set = new HashSet();
        while ( reader.hasMoreChildren() )
        {
            reader.moveDown();
            set.add( context.convertAnother( set, type ) );
            reader.moveUp();
        }
        return set;
    }
}
