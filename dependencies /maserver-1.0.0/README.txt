MorphAdorner Server, or MAServer for short, is an HTTP-based server which
exposes selected MorphAdorner facilities over the World Wide Web.

File name:       maserver-1.0.0.zip
Current version: 1.0.0.
Last update:     September 25, 2013.

The MorphAdorner Server source code and support files, along with an
issue tracker, are available as a Mercurial repository on bitbucket.org at

    http://bitbucket.org/pibburns/morphadornerserver


Quick Setup
-----------

If you downloaded the MAServer release from the Mercurial repository
on bitbucket.org, please go to the section "Installing and
building MAServer."

If you downloaded the ready-to-use maserver-1.0.0.zip file,
proceed as follows.  Expand the contents of the maserver-1.0.0.zip file
into an empty directory.  Make sure you retain the existing directory
structure.

You must have the Java run-time environment installed on your machine
to run MAServer.  If you do not, go to the section "Installing and Building
MAServer" for information on where to get a copy of the Java runtime.
Once you have Java installed you can proceed with running MAServer.

To run MAServer standalone on Windows, type

    runmaserver.bat

at the command line of a console window.

On Unix-like systems, including Mac OS X, type

    chmod 755 runmaserver

in a terminal window to set the shell script to execute.
You only need to do this once.  To run the server, type

    ./runmaserver

By default MAServer listens on TCP port 8182.  You can change this default
port number in the batch file or shell script.  Both the batch file
and script request 4 gigabytes of memory to run.

MAServer requires at least 2.5 gigabytes of memory to run with 4 gigabytes
preferred.  For best results you should run MAServer on a 64-bit operating
system with a 64-bit version of the Java run-time environment installed.
Your system may require more memory than these minimums.  In particular,
Mac OS X may require at least 3.0 gigabytes of memory to run MAServer.

You can access the test web pages once MAServer finishes initialization,
which can take a couple of minutes on a slow system.  Open a web browser on
the system on which you are running MAServer and enter the URL

    http://localhost:8182/

You should see the main MAServer test page.  If you changed the default
TCP port for the server, replace 8182 in the URL with your modified
port number.


File Layout of MAServer Release
-------------------------------

File or Directory          Contents
=========================  ================================================
build.properties           Build settings you can modify.
build.xml                  Apache Ant build file used to compile
                           MAServer.
conf/                      Configuration files.
    log4j.properties       Logging properties.
    template-web.xml       Template for generating web.xml file.
    wadl.xsl               Web Applcation Descriptor Language html
                           conversion.
    web-xml.properties     Settings for generating web.xml file.
data/                      Data and settings files used by server.
ivy.xml                    Apache Ivy dependencies definitions.
ivysettings.xml            Apache Ivy settings.
lib/                       Java library files used by MAServer.
                           These are retrieved on demand during the
                           build process using Apache Ivy.
license.txt                The MAServer license.
modhist.txt                MAServer modification history.
README.txt                 Printable copy of this file in Windows text
                           format (lines terminated by Ascii cr/lf).
runmaserver                Unix shell command file to start server
                           in standalone mode.
runmaserver.bat            Windows batch file to start server in
                           standalone mode.
src/                       MAServer source code.
testdata/                  Test data files.
webpages/                  Static web pages for accessing MAServer
                           services.

Installing and Building MAServer
--------------------------------

To rebuild the MAServer code, make sure you have installed recent
working copies of Oracle's Java Development Kit and Apache Ant on your
system.  The Java development kits for Windows, Mac OS X, and Linux systems
may be obtained from

    http://www.oracle.com/technetwork/java/javase/downloads/index.html

Alternatively, OpenJDK may be obtained from

    http://openjdk.java.net/install/index.html

Apache Ant may be obtained from

    http://ant.apache.org

Move to the directory into which you unzipped the MAServer release (or
into which you cloned a local copy of the Mercurial repository for
MAServer).

Use a plain text editor to edit the "build.properties" file.
You should provide values for the following three settings.

(1) The "serverdata.dir" setting should be set to the MAServer data
directory for a remote installation of MAServer.  This can be a local
directory on your desktop if you are running MAServer under a local copy
of a servlet server.  This value is used only by the Ant "copyserverdata"
task.  If you don't intend to use that task to copy the server data,
you may leave "serverdata.dir" empty.

(2) The "localServerURL" setting should be set to the base URL of
your local MAServer installation.  The default value of

    localServerURL=http://localhost:8182/

is fine for out-of-the-box use when you run the server using
runmaserver.bat/runmaserver .

(3) The "remoteServerURL" setting should be set to the base URL of your
remote installation of MAServer, if any.  This is needed to run tests
against that server. If you only intend to run the built-in server
version of MAServer (using runmaserver.bat/runmaserver), you may leave this
setting empty.

For example, if your server name is "myremotehost.com", you would
enter something like:

    remoteServerURL=http://myremotehost.com/maserver/

If you intend to run MAServer under a local copy of a servlet server
such as Tomcat or Jetty on your own desktop, you can set the
remoteServerURL to point to your desktop.  In this case the setting will be
something like:

    remoteServerURL=http://localhost:8080/maserver/

Once you have set the above three entries in build.properties appropriately,
save the build.properties file with the updated values.

To run MAServer under a servlet server such as Tomcat, you must also modify
the settings in the conf/web-xml.properties file.  Open this file with a
plain text editor, and provide values for the following settings.

(1) The "datadirectory" settings specifies the location of the
MAServer data files -as seen by the servlet server-.  This may differ
from the value you set for "serverdata.dir" in the build.properties file.

(2) The "maxunadorneduploadfilesize" specifies the maximum file size
in bytes of an unadorned TEI XML file which the server will accept as an
upload.  The default value is "5m" or 5 megabytes.

(3)  The "maxadorneduploadfilesize" specifies the maximum file size
in bytes of an adorned TEI XML file which the server will accept
as an upload.  The default value is "50m" or 50 megabytes.

The larger the file size limits provided, the more memory the server
requires to process the files.

Save the conf/web-xml.proerties file with the updated values.

After you have set the values in the build.properties and
conf/web-xml.properties files appropriately, open a console or terminal
window, move to the base directory of the MAServer release, and type:

    ant

to build MAServer.  If the build completes successfully, the maserver.jar
and maserver.war files will be placed in the "dist" subdirectory.

You must use a Java compiler which is compatible with Java 1.6 or higher.
MAserver has been successfully compiled and executed under Windows and
Linux using the standard Oracle JDK 1.6 and 1.7 releases; under Linux
using a recent release of OpenJDK 7; and under Mac OS X using a recent
version of the standard MAC OS X Java compiler and run-time.  Other Java
compilers and run-times may not work.

Type

    ant javadoc

to generate the javadoc (internal documentation) into subdirectory
"javadoc".

Type

    ant clean

to remove the effects of compilation.  This does not remove the
downloaded files in the lib subdirectory.  To remove those as well,
type

    ant cleanlib

Once in a while, if you are having trouble compiling, you may need
to clean your Ivy cache to make sure you have the correct library
files.  Type

    ant cleancache

to clean the Ivy cache.


Running MAServer In A Servlet Server
------------------------------------

To deploy MAServer in a servlet server such as Tomcat you need
to do four things:

(1)  Copy the data directory to a location of your choosing.

Copy the entire data/ directory along with its subdirectories to
a directory somewhere on your system.  By default this directory
is defined as /project/maserverdata .  You should change this setting
in the conf/web-xml.properties file by setting the value of the
"datadirectory" property to the correct directory name on your server.

If the remote server data directory is mounted so that you can access it
locally, you can type

    ant copyserverdata

to copy the data files to the remote directory you specified as the
value of the "serverdata.dir" setting in the build.properties file.

The data directory you select, and all its subdirectories,
must be readable by your chosen servlet server.  The servlet server
must also have permission to change to that directory while running.

(2)  Rebuild the maserver.war file.

Rebuild the maserver.war file by typing

    ant war

in a console/terminal window.  The updated maserver.war file is written
to dist/maserver.war .

(3)  Install the rebuilt maserver.war file into your servlet server.

Different servlet servers have various methods for doing this.
Consult the documentation for your particular servlet server for details.

For example, in Tomcat, you can copy the maserver.war file to the
Tomcat "webapps" subdirectory.  Make sure you have configured Tomcat
to deploy WAR files automatically by setting the "autoDeploy" option to
"true" in the Host container element.  See

    http://tomcat.apache.org/tomcat-7.0-doc/config/host.html

for details.

MAServer has been tested to work under both Tomcat (v7) and Jetty (v8).

(4)  Restart your servlet server.

Some servlet servers can "hot install" new web applications presented
as a war file, so you may not have to restart your server.  It's usually
a good idea to restart the server anyway.  You must restart the server
if you stopped the server before installing the MAServer war file.

After you restart your servlet server, MAServer should become available
within a couple of minutes under the application name "maserver".
Open a web browser on the system on which you are running the server
and navigate to the web page URL

    http://servername:8080/maserver/

Replace "servername" with the name of the system on which you installed
MAServer, and replace "8080" with the TCP port number for accessing your
servlet server.  You should see the main MAServer services web page
once MAServer initialization completes.


Testing
-------

The MAServer release contains a small set of tests which may be used
to test the server's operation.  These are not intended to be
comprehensive.

To run the tests, make sure you've provided values for the "remoteServerURL"
and/or "localServerURL" settings in build.properties, as described above.

The run the tests against a local MAServer instance, start that instance,
then open a console/terminal window and type:

    ant runlocaltests

To run the tests against a remote MAServer instamce, make sure the
remote instance is running, and type:

    ant runremotetests

Examine the output for error messages.  Usually either all of the tests
will run successfully, or all of them will fail (usually because
the MAServer instance isn't started or is blocked by a firewall).


License
-------

MAServer is licensed under the same NCSA style open source license
as MorphAdorner.  See the license.txt file for details of this license.


Documentation
-------------

The main MorphAdorner web site at

    http://morphadorner.northwestern.edu/

offers online documentation for both the MorphAdorner client and server
as well as printable documentation in Adobe PDF format.

You may also access the WADL (web application description language)
definitions for all the services using a web browser.  Start the local
version of the MAServer server using the runmaserver.bat (Windows)
or runmaserver script (Unix and Mac OS X).  Then open the following site
in your web browser:

    http://localhost:8182/?method=options

The WADL for an individual service can be retrieved using

    http://localhost:8182/maserver/servicename?method=options

and replacing "servicename" with the name of the MAServer service for
which you want the documentation.  For example, the WADL for the
lemmatizer service can be retrieved with:

    http://localhost:8182/lemmatizer?method=options

If your system provides the curl utility, you can retrieve the XML
formatted WADL descriptions for all services using curl in a
console/terminal window as follows:

    curl http://localhost:8182/?method=options

You can retrieve the WADL XML for a particular service --
say the lemmatizer service -- as follows:

    curl http://localhost:8182/lemmatizer?method=options

You can also retrieve the WADL descriptions from a remotely installed
MAServer installation by replacing "localhost:8182" with the server name
and server port of the remote server.  Examples:

    http://myremotehost.com/maserver/?method=options
    http://myremotehost.com/maserver/lemmatizer/?method=options

Replace "myremotehost.com" with the name (and optionally the port number)
of your remote MAServer instance.

