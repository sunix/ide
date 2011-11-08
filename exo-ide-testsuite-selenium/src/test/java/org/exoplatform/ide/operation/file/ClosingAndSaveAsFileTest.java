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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class ClosingAndSaveAsFileTest extends BaseTest
{

   private static String PROJECT = ClosingAndSaveAsFileTest.class.getSimpleName();

   private static final String FILE = "testfile";

   private static final String FILE2 = "new XML file.xml";

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT + "/");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //http://jira.exoplatform.com/browse/IDE-412
   @Test
   public void testClosingAndSaveAsFile() throws Exception
   {
      IDE.PROJECT_EXPLORER.waitOpened();
      IDE.CREATE_PROJECT.createProject(PROJECT);
      IDE.PROJECT_EXPLORER.waitForItem("/" + PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(2);

      IDE.EDITOR.clickCloseEditorButton(1);
      assertTrue(IDE.ASK_FOR_VALUE_DIALOG.isOpened());

      IDE.ASK_FOR_VALUE_DIALOG.clickCancelButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitForAskDialogNotPresent();

      IDE.EDITOR.selectTab(1);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS);
      IDE.ASK_FOR_VALUE_DIALOG.waitForPresent();
      IDE.ASK_FOR_VALUE_DIALOG.setValue(FILE);
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitForAskDialogNotPresent();

      IDE.WORKSPACE.waitForItem("/" + PROJECT + "/" + FILE);

      assertTrue(IDE.EDITOR.isTabPresentInEditorTabset(1));
      assertEquals(FILE, IDE.EDITOR.getTabTitle(1));

      IDE.EDITOR.closeFile(1);
   }

   @Test
   public void testSaveAsFileAfterTryingToCloseNewFile() throws Exception
   {
      selenium.refresh();
      IDE.PROJECT_EXPLORER.waitOpened();
      IDE.CREATE_PROJECT.createProject(PROJECT);

      IDE.PROJECT_EXPLORER.waitForItem(PROJECT);

      IDE.WORKSPACE.selectItem(PROJECT);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);

      IDE.EDITOR.clickCloseEditorButton(1);
      IDE.ASK_FOR_VALUE_DIALOG.waitForPresent();

      IDE.ASK_FOR_VALUE_DIALOG.closeDialog();
      IDE.ASK_FOR_VALUE_DIALOG.waitForAskDialogNotPresent();

      IDE.EDITOR.saveAndCloseFile(1, FILE2);

      IDE.WORKSPACE.waitForItem(PROJECT + "/" + FILE2);
      Assert.assertFalse(IDE.EDITOR.isTabPresentInEditorTabset(FILE2));
   }

}
