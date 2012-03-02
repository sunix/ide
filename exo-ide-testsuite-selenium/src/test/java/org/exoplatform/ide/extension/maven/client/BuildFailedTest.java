/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.maven.client;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildFailedTest.java Feb 27, 2012 5:24:41 PM azatsarynnyy $
 *
 */
public class BuildFailedTest extends BaseTest
{
   private static final String PROJECT = BuildFailedTest.class.getSimpleName();

   protected static Map<String, Link> project;

   @Before
   public void before()
   {
      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithoutPOM.zip");

         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void after()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);

         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testBuildFailed() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();

      // Open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      // Necessary because davfs working slowly and several
      // tests which run in test suite not work correctly.
      Thread.sleep(40 * 1000);

      // Building of project is started
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_PROJECT);
      IDE.BUILD.waitOpened();
      String builderMessage = IDE.BUILD.getOutputMessage();
      assertTrue(builderMessage.startsWith(Build.Messages.BUILDING_PROJECT));
      
      // Wait until building is finished.
      Thread.sleep(10000);

      // Close Build project view because Output view is not visible
      selenium().click("//div[@class='gwt-TabLayoutPanelTabs']//div[@tab-title='Build project']");

      // Get error message
      IDE.OUTPUT.waitForMessageShow(1, 15);
      String buildErrorMessage = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(buildErrorMessage.endsWith(Build.Messages.BUILD_FAILED));
   }
}
