package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import edu.northwestern.at.morphadorner.server.logging.MorphAdornerServerLogger;

/** MorphAdorner Restlet-based Server */

public class MorphAdornerServer
{
    /** Default port on which to listen for incoming requests. */

    public static final int DEFAULT_SERVER_PORT = 8182;

    /** Main program.
     *
     *  @param  args    Command line arguments.
     */

    public static void main( String[] args )
    {
                                //  Pick up listening port if
                                //  specified on command line.

        int serverPort  = DEFAULT_SERVER_PORT;

        if ( args.length > 0 )
        {
            try
            {
                serverPort  = Integer.parseInt( args[ 0 ] );
            }
            catch ( Exception e )
            {
                MorphAdornerServerLogger.logger.trace( e );

                serverPort = DEFAULT_SERVER_PORT;
            }
        }
                                //  Pick up MorphAdorner Server
                                //  data directory if specified.

        if ( args.length > 1 )
        {
            try
            {
                MorphAdornerServerData.setDataDirectory( args[ 1 ] );
            }
            catch ( Exception e )
            {
                MorphAdornerServerLogger.logger.trace( e );

                MorphAdornerServerData.setDataDirectory( null );
            }
        }

        try
        {
                                //  Create a new Component.

            Component component = new Component();

                                //  Add a new HTTP server listening on
                                //  port 8182.

            Server server   = new Server( Protocol.HTTP , serverPort );

            component.getServers().add( server );

                                //  Increase thread count for server.

            server.getContext().getParameters().add( "maxThreads" , "1024" );

                                //  Add file client to allow serving
                                //  static pages.

            component.getClients().add( Protocol.FILE );

                                //  Attach the MorphAdorner server
                                //  application.

            component.getDefaultHost().attach
            (
                new MorphAdornerServerApplication()
            );
                                //  Start the server.

            component.start();
        }
        catch ( Exception e)
        {
                                //  Something is wrong.

            MorphAdornerServerLogger.logger.trace( e );
        }
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



