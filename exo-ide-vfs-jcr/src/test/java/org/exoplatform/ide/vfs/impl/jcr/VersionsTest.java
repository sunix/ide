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
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.FileImpl;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VersionsTest.java 77587 2011-12-13 10:42:02Z andrew00x $
 */
public class VersionsTest extends JcrFileSystemTest
{
   private Node versionsTestNode;
   private String fileID;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      versionsTestNode = testRoot.addNode(name, "nt:unstructured");
      versionsTestNode.addMixin("exo:privilegeable");

      Node fileNode = versionsTestNode.addNode("VersionsTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("mix:versionable");
      session.save();

      fileNode.checkin();
      fileNode.checkout();
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST__001".getBytes()));
      session.save();

      fileNode.checkin();
      fileNode.checkout();
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST__002".getBytes()));
      session.save();

      fileID = ((ExtendedNode)fileNode).getIdentifier();
   }

   @SuppressWarnings("unchecked")
   public void testGetVersions() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version-history/") //
         .append(fileID) //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      List<File> items = ((ItemList<File>)response.getEntity()).getItems();
      List<String> all = new ArrayList<String>(3);
      for (File i : items)
      {
         validateLinks(i);
         all.add(i.getVersionId());
      }
      assertEquals(3, all.size());
      assertEquals("1", all.get(0));
      assertEquals("2", all.get(1));
      assertEquals("0", all.get(2));
      //log.info(new String(writer.getBody()));
   }

   @SuppressWarnings("unchecked")
   public void testGetSinleVersions() throws Exception
   {
      Node singleVersionFileNode = versionsTestNode.addNode("testGetSinleVersions", "nt:file");
      Node contentNode = singleVersionFileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      session.save();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version-history/") //
         .append(((ExtendedNode)singleVersionFileNode).getIdentifier()) //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      List<File> items = ((ItemList<File>)response.getEntity()).getItems();
      List<String> all = new ArrayList<String>(1);
      for (File i : items)
      {
         validateLinks(i);
         all.add(i.getVersionId());
      }
      assertEquals(1, all.size());
      assertEquals("0", all.get(0));
      //log.info(new String(writer.getBody()));
   }

   public void testGetVersionById() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version/") //
         .append(fileID) //
         .append("/2") //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("__TEST__001", new String(writer.getBody()));
      //log.info(">>>>>>>> " + response.getHttpHeaders());
   }

   public void testGetVersionByPathAndID() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      Node fileNode  = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("itembypath") //
         .append(fileNode.getPath()) //
         .append("?") //
         .append("versionId=") //
         .append("2") //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      File file = (File)response.getEntity();
      assertEquals("2", file.getVersionId());
   }

   public void testGetVersionByIdInvalidVersion() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version/") //
         .append(fileID) //
         .append("/5") //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(400, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void testGetVersionsPagingSkipCount() throws Exception
   {
      // Get all versions.
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version-history/") //
         .append(fileID).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      ItemList<File> children = (ItemList<File>)response.getEntity();
      List<Object> all = new ArrayList<Object>(3);
      for (File i : children.getItems())
      {
         validateLinks(i);
         all.add(i.getVersionId());
      }

      // Skip first item in result.
      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version-history/") //
         .append(fileID) //
         .append("?") //
         .append("skipCount=") //
         .append("1") //
         .toString();
      Iterator<Object> iteratorAll = all.iterator();
      iteratorAll.next();
      iteratorAll.remove();
      checkPage(path, "GET", FileImpl.class.getMethod("getVersionId"), all);
   }

   public void testGetVersionsPagingMaxItems() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version-history/") //
         .append(fileID) //
         .toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      List<File> items = ((ItemList<File>)response.getEntity()).getItems();
      List<Object> all = new ArrayList<Object>(3);
      for (File i : items)
      {
         validateLinks(i);
         all.add(i.getVersionId());
      }
      assertEquals(3, all.size());

      all.remove(2);

      path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("version-history/") //
         .append(fileID) //
         .append("?") //
         .append("maxItems=") //
         .append("2") //
         .toString();

      checkPage(path, "GET", FileImpl.class.getMethod("getVersionId"), all);
   }
}