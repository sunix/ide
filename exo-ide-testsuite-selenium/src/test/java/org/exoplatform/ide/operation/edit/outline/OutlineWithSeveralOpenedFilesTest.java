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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * IDE-170:Test closing Code Outline panel.
 * 
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */

public class OutlineWithSeveralOpenedFilesTest extends BaseTest
{
   
   @AfterClass
   public static void tearDown()
   {
      deleteCookies();
      cleanRegistry();
      cleanDefaultWorkspace();
   }
   
   //Check, that Outline tab correctly works, when we
   //try to navigate on different tabs in editor
   @Test
   public void testOutlineWhenSeveralFilesOpen() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //---- 1 --------------
      //open new javascript file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //no outline panel
      assertFalse(selenium.isElementPresent("//div[@eventproxy='ideCodeHelperPanel']"));
      
      //---- 2 --------------
      //show outline
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      //check outline appeared
      //we can't use checkOutlineVisibility() method,
      //because when outline appears at first time, element div
      //doesn't have in style attribute visibility attribute
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCodeHelperPanel']"));
      
      //---- 3 --------------
      //open new html file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      //check outline present
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideCodeHelperPanel']"));
      
      //---- 4 --------------
      //Close Outline tab 
      selenium.click("scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]/tab[index=0]/icon");
      Thread.sleep(TestConstants.SLEEP);
      IDE.OUTLINE.checkOutlinePanelVisibility(false);

      //---- 5 --------------
      //go to javascript file
     IDE.EDITOR.selectTab(0);
      Thread.sleep(TestConstants.SLEEP);
      IDE.OUTLINE.checkOutlinePanelVisibility(false);

      //end
     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }
   
}
