package edu.northwestern.at.morphadorner.server;

/*  Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;

import org.restlet.ext.fileupload.*;
import org.restlet.Request;

import edu.northwestern.at.morphadorner.server.logging.MorphAdornerServerLogger;
import edu.northwestern.at.utils.*;

import org.apache.log4j.*;

/** Handle file upload to MorphAdorner server.
 */

public class UploadedFileProcessor
{
    /** Holds request parameter names and values. */

    public Map<String, String> params   = MapFactory.createNewMap();

    /** Short names of uploaded files for each file parameter name. */

    public Map<String, String> fileNameMap  = MapFactory.createNewMap();

    /** Full path name of each uploaded file for each file parameter name. */

    public Map<String, String> fullFileNameMap  = MapFactory.createNewMap();

    /** Holds names of temporary files. */

    public List<String> filesToDelete   = ListFactory.createNewList();

    /** Holds names of temporary directories. */

    public List<String> dirsToDelete    = ListFactory.createNewList();

    /** true if parameters contain all the required files. */

    public boolean foundFiles   = false;

    /** Create uploaded file processor.
     *
     *  @param  request         Servlet request.
     *  @param  fileParamNames  Parameter names of the files to upload.
     *  @param  maxUploadSize   Maximum ize (in bytes) of file to upload.
     *
     *  @throws Exception       In case of error.
     */

    public UploadedFileProcessor
    (
        Request request ,
        String[] fileParamNames ,
        int maxUploadSize
    )
        throws Exception
    {
                                //  Create disk factory to process
                                //  received file.

        DiskFileItemFactory factory = new DiskFileItemFactory();

                                //  Copy input files to disk.

//      factory.setSizeThreshold( 0 );

                                //  Get the received file items.

        RestletFileUpload upload    = new RestletFileUpload( factory );

                                //  Limit input file size.

        upload.setFileSizeMax( maxUploadSize );

                                //  Parse file request.  If the input
                                //  file size is too big, this will
                                //  fail, so we need to handle the
                                //  error in the calling method.

        List<FileItem> fileItems    = upload.parseRequest( request );

                                //  Get set of file parameter names.

        Set<String> fileParamNamesSet   = SetFactory.createNewSet();
        fileParamNamesSet.addAll( Arrays.asList( fileParamNames ) );

                                //  Set true if we find all the files
                                //  in the input parameters.

        Map<String , Boolean> foundFilesMap = MapFactory.createNewMap();

        for ( String fileParamName : fileParamNamesSet )
        {
            foundFilesMap.put( fileParamName , false );
        }
                                //  Process form fields.

        for (   final Iterator<FileItem> it = fileItems.iterator();
                it.hasNext();
            )
        {
                                //  Get next form field.

            FileItem fileItem   = it.next();

            String fieldName    = fileItem.getFieldName();

                                //  If the field is a received file,
                                //  extract it to a unique temporary
                                //  directory underneath the base
                                //  temporary directory.

            if ( fileParamNamesSet.contains( fieldName ) )
            {
                                //  Get the file name.

                String fileName = fileItem.getName();

                                //  Skip if empty file name.

                if ( ( fileName == null ) || ( fileName.length() == 0 ) )
                {
                    continue;
                }
                                //  Remember we found the file.

                foundFilesMap.put( fieldName , true );

                                //  Save short file name.

                fileNameMap.put( fieldName , fileName );

                                //  Get a unique directory in the
                                //  base temporary directory to
                                //  to hold the file's data.
                                //  We place files in unique directories
                                //  because it is perfectly legal for
                                //  them to have the same name but contain
                                //  different content.

                                //  Get name for temporary directory to
                                //  hold this file.

                File fTempDir   = getTemporaryDirectory();
                String tempDir  = fTempDir.getCanonicalPath();

                                //  Add this temporary directory name to
                                //  list of directories to delete
                                //  after handling this request.

                addTempDirectory( tempDir );

                                //  Create temporary directory.

                fTempDir.mkdirs();

                                //  Create file name in temporary
                                //  directory to hold this file's date.

                File fTempName      = new File( tempDir , fileName );
                String fullFileName = fTempName.getCanonicalPath();

                                //  Save full file name.

                fullFileNameMap.put( fieldName , fullFileName );

                                //  Add temporary file to list of files
                                //  to delete after handling request.

                addTempFile( fullFileName );

                                //  Make sure the file path exists
                                //  so we can write the file data.

                FileUtils.createPathForFile( fullFileName );

                                //  Now write the file data to the
                                //  temporary file.

                File file   = new File( fullFileName );
                fileItem.write( file );
            }
            else
            {
                                //  Add other parameters and values
                                //  to the parameters map.
                params.put
                (
                    fieldName ,
                    new String( fileItem.get() , "utf-8" )
                );
            }
                                //  Set flag indicating if all expected
                                //  files were found.

            for ( String fileName : foundFilesMap.keySet() )
            {
                foundFiles  = foundFiles || foundFilesMap.get( fileName );
            }
        }
    }

    /** Create uploaded file processor when expecting a single file.
     *
     *  @param  request         Servlet request.
     *  @param  fileParamName   Parameter name of file upload.
     *  @param  maxUploadSize   Maximum ize (in bytes) of file to upload.
     *
     *  @throws Exception       In case of error.
     */

    public UploadedFileProcessor
    (
        Request request ,
        String fileParamName ,
        int maxUploadSize
    )
        throws Exception
    {
        this
        (
            request ,
            new String[]{ fileParamName } ,
            maxUploadSize
        );
    }

    /** Get a temporary directory.
     *
     *  @return     A temporary directory.
     */

    public File getTemporaryDirectory()
    {
        return DirUtils.createTemporaryDirectory( "zz" );
    }

    /** Get short file name for a parameter.
     *
     *  @param  fieldName   The form field name.
     *
     *  @return             The short file name corresponding to the
     *                      form field name.  May be null.
     */

    public String getFileName( String fieldName )
    {
        return fileNameMap.get( fieldName );
    }

    /** Get full file name for a parameter.
     *
     *  @param  fieldName   The form field name.
     *
     *  @return             The full file name corresponding to the
     *                      form field name.  May be null.
     */

    public String getFullFileName( String fieldName )
    {
        return fullFileNameMap.get( fieldName );
    }

    /** Add a file to the list of temporary file names.
     *
     *  @param  fileName    File name to add to list of temporary files.
     */

    public void addTempFile( String fileName )
    {
        filesToDelete.add( fileName );
    }

    /** Add a directory to the list of temporary directories.
     *
     *  @param  dirName     Directory name to add to list of temporary
     *                      directories.
     */

    public void addTempDirectory( String dirName )
    {
        dirsToDelete.add( dirName );
    }

    /** Get temporary directories to delete.
     *
     *  @return     List of temporary directories.
     */

    public List<String> getTempDirectories()
    {
        return dirsToDelete;
    }

    /** Get temporary files to delete.
     *
     *  @return     List of temporary files.
     */

    public List<String> getTempFiles()
    {
        return filesToDelete;
    }

    /** Delete the temporary files and directories. */

    public void deleteTemporaryFilesAndDirectories()
    {
        String curDir   = FileUtils.getCurrentDirectory();

        MorphAdornerServerLogger.logger.log
        (
            Level.INFO ,
            "deleteTemporaryFilesAndDirectories: current directory is " +
            curDir
        );

        for ( int i = 0 ; i < filesToDelete.size() ; i++ )
        {
            MorphAdornerServerLogger.logger.log
            (
                Level.INFO ,
                "deleteTemporaryFilesAndDirectories: deleting file " +
                filesToDelete.get( i )
            );

            FileUtils.deleteFile( filesToDelete.get( i ) );
        }

        Collections.sort( dirsToDelete );

        for ( int i = dirsToDelete.size() - 1 ; i >= 0 ; i-- )
        {
            MorphAdornerServerLogger.logger.log
            (
                Level.INFO ,
                "deleteTemporaryFilesAndDirectories: deleting directory " +
                dirsToDelete.get( i )
            );

            DirUtils.deleteDirectory( dirsToDelete.get( i ) );
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



