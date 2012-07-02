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

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.vfs.shared.Link;
import org.fest.assertions.AssertExtension;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildPerformedTest.java Feb 27, 2012 6:55:26 PM azatsarynnyy $
 *
 */
public class CheckAfterRebuildTest extends BaseTest
{
   private static final String PROJECT = CheckAfterRebuildTest.class.getSimpleName();

   protected static Map<String, Link> project;

   @Before
   public void before()
   {
      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip");
         //need for create project on  DavFs
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

         //need for delete project on  DavFs
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testBuildPerformed() throws Exception
   {
      //step one, open project and build him
      IDE.PROJECT.EXPLORER.waitOpened();
      // Open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      // Start the build of two projects at the same time
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_PROJECT);

      // Get error message
      IDE.STATUSBAR.waitDiasspearBuildStatus();

      final String outputMess =
         "Building project CheckAfterRebuildTest" + "\n" + "Finished building project CheckAfterRebuildTest." + "\n"
            + "Result: Successful" + "\n" + "You can download the build result here";

      final String outputMessAferRebuild = "You can download the build result here";

      IDE.BUILD.waitBuildResultLink();
      assertEquals(IDE.BUILD.getOutputMessage(), outputMess);
      IDE.BUILD.clickClearButton();

      //step two, rebuild project and check,   project shouldn't  build one more time
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_PROJECT);
      IDE.BUILD.waitBuildResultLink();
      assertEquals(IDE.BUILD.getOutputMessage(), outputMessAferRebuild);
   }
}
