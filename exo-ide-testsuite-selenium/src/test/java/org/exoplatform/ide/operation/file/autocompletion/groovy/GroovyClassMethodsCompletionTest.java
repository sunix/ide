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
package org.exoplatform.ide.operation.file.autocompletion.groovy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 8, 2010 2:36:49 PM evgen $
 *
 */
public class GroovyClassMethodsCompletionTest extends BaseTest
{

   @Test
   public void testGroovyClassMethodCompletion() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      for (int i = 0; i < 9; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      typeTextIntoEditor(0, "Collections.");

      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_SPACE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("//table[@class='exo-autocomplete-panel']"));

      selenium.focus("//input[@class='exo-autocomplete-edit']");

      selenium.typeKeys("//input[@class='exo-autocomplete-edit']", "so");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertTrue(selenium.isElementPresent("//div[text()='sort(List):void']"));
      assertTrue(selenium.isElementPresent("//div[text()='sort(List, Comparator):void']"));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertFalse(selenium.isElementPresent("//table[@class='exo-autocomplete-panel']"));
      assertTrue(getTextFromCodeEditor(0).contains("Collections.sort(List, Comparator)"));
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

}
