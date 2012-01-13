/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.codeassistant.framework.server.extractors;

import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DocExtractor
{

   /**
    * Path to base folder for exctracting source archive curently it System.getProperty("java.io.tmpdir")
    */
   private static String extPath;

   /**
    * Extract GroovyDoc (JavaDoc) for getting archive and filtered by package
    * 
    * @param javaArchSrcPath
    * @param pkgPath
    * @return
    * @throws RecognitionException
    * @throws TokenStreamException
    * @throws IOException
    */
   public static Map<String, GroovyRootDoc> extract(String javaArchSrcPath, String pkgPath)
      throws RecognitionException, TokenStreamException, IOException
   {
      String fs = System.getProperty("file.separator");
      String archName = javaArchSrcPath.substring(javaArchSrcPath.lastIndexOf(fs) + 1);
      String tmp =
         System.getProperty("java.io.tmpdir").endsWith(fs) ? System.getProperty("java.io.tmpdir") : System
            .getProperty("java.io.tmpdir") + fs;
      extPath = tmp + "source" + fs + archName + fs;
      Map<String, GroovyRootDoc> docs = new HashMap<String, GroovyRootDoc>();
      GroovyDocTool plainTool; // = new GroovyDocTool(new String[]{extPath});
      if (pkgPath != null)
      {
         pkgPath = pkgPath.replace(".", fs);
      }
      unzip(javaArchSrcPath);
      Map<String, List<String>> pkgMap = srcPkgMap(new File(extPath), new PkgFileFilter(pkgPath), true);
      Set<String> pkgs = pkgMap.keySet();
      for (String pkg : pkgs)
      {
         plainTool = new GroovyDocTool(new String[]{extPath + pkg});
         plainTool.add(pkgMap.get(pkg));
         docs.put(pkg.replace(fs, "."), plainTool.getRootDoc());
      }
      return docs;
   }

   /**
    * @param javaArchSrcPath
    * @return
    * @throws RecognitionException
    * @throws TokenStreamException
    * @throws IOException
    */
   public static Map<String, GroovyRootDoc> extract(String javaArchSrcPath) throws RecognitionException,
      TokenStreamException, IOException
   {

      return extract(javaArchSrcPath, null);
   }

   /**
    * @param zipPath
    * @throws IOException
    * @throws FileNotFoundException
    */
   private static void unzip(String zipPath) throws FileNotFoundException, IOException
   {
      new File(extPath).mkdirs();
      ZipFile zipFile;

      zipFile = new ZipFile(zipPath);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements())
      {
         ZipEntry entry = (ZipEntry)entries.nextElement();
         if (entry.isDirectory())
         {
            File file = new File(extPath + entry.getName());
            file.mkdir();
            continue;
         }
         copyInputStream(zipFile.getInputStream(entry),
            new BufferedOutputStream(new FileOutputStream(extPath + entry.getName())));
      }
      zipFile.close();
   }

   /**
    * @param in
    * @param out
    * @throws IOException
    */
   private static void copyInputStream(InputStream in, OutputStream out) throws IOException
   {
      byte[] buffer = new byte[1024];
      int len;

      while ((len = in.read(buffer)) >= 0)
         out.write(buffer, 0, len);

      in.close();
      out.close();
   }

   /**
    * @param directory
    * @param filter
    * @return
    */
   private static List<String> listFiles(File directory, FilenameFilter filter)
   {
      Vector<String> files = new Vector<String>();
      File[] entries = directory.listFiles();
      for (File entry : entries)
      {
         if (filter == null || filter.accept(directory, entry.getName()))
         {
            files.add(entry.getName());
         }
      }
      return files;
   }

   /**
    * @param directory
    * @param filter
    * @param recurse
    * @return
    */
   public static Map<String, List<String>> srcPkgMap(File directory, FilenameFilter filter, boolean recurse)
   {
      File[] entries = directory.listFiles();
      Map<String, List<String>> map = new HashMap<String, List<String>>();
      for (File entry : entries)
      {
         if (entry.isFile() && (filter == null || filter.accept(directory, entry.getName())))
         {
            String p = entry.getParent();
            if (map.containsKey(p.substring(extPath.length())))
            {

            }
            map.put(p.substring(extPath.length()), listFiles(directory, filter));
         }
         // If the file is a directory and the recurse flag
         // is set, recurse into the directory
         if (recurse && entry.isDirectory())
         {
            map.putAll(srcPkgMap(entry, filter, recurse));
         }
      }
      return map;
   }

   /**
    * @author vetal
    * 
    */
   private static final class PkgFileFilter implements FilenameFilter
   {
      private final String pkg;

      public PkgFileFilter(String pkg)
      {
         this.pkg = pkg;
      }

      public boolean accept(File dir, String name)
      {
         if (pkg == null)
         {
            return name.endsWith(".java");
         }
         return (dir.getPath().startsWith(extPath + pkg) && name.endsWith(".java"));

      }
   }

}
