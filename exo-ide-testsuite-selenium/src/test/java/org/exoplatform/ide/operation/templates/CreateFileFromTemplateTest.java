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
package org.exoplatform.ide.operation.templates;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Response;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Create file from template.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class CreateFileFromTemplateTest extends BaseTest
{
   private final static String PROJECT = CreateFileFromTemplateTest.class.getSimpleName();

   private static final String GROOVY_REST_SERVICE = "Groovy REST Service";

   private static final String EMPTY_XML = "Empty XML";

   private static final String EMPTY_HTML = "Empty HTML";

   private static final String EMPTY_TEXT = "Empty TEXT";

   private static final String OPENSOCIAL_GADGET = "OpenSocial Gadget";

   private static final String GROOVY_FILE_NAME = "Test Groovy File.groovy";

   private static final String XML_FILE_NAME = "Test Xml File.xml";

   private static final String HTML_FILE_NAME = "Test Html File.html";

   private static final String TEXT_FILE_NAME = "Test Text File.txt";

   private static final String OPENSOCIAL_GADGET_FILE_NAME = "Test Gadget File.xml";

   @BeforeClass
   public static void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testCreateFileFromTemplate() throws Exception
   {
      // -------- 1 ----------
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      Assert.assertTrue(IDE.TOOLBAR.isButtonFromNewPopupMenuEnabled(MenuCommands.New.FILE_FROM_TEMPLATE));

      // -------- 2 ----------
      testTemplate(GROOVY_REST_SERVICE, GROOVY_FILE_NAME);

      // -------- 3 ----------
      // Repeat step 2 with items "Empty XML", "Empty HTML", "Empty TEXT", "OpenSocial Gadget" item of left panel.
      testTemplate(EMPTY_XML, XML_FILE_NAME);
      testTemplate(EMPTY_HTML, HTML_FILE_NAME);
      testTemplate(OPENSOCIAL_GADGET, OPENSOCIAL_GADGET_FILE_NAME);
      testTemplate(EMPTY_TEXT, TEXT_FILE_NAME);
   }

   /**
    * IDE-573: If create new file from template and on opened files exist file with name "Untitled file.html" content of open file
    * replaced on template content
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFileFromTemplateWithDuplicatedName() throws Exception
   {
      selenium.refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      /*
       * 1. Open two html files. They will have names: Untitled file.html, Untitled file 1.html
       */
      Assert.assertTrue(IDE.TOOLBAR.isButtonFromNewPopupMenuEnabled(MenuCommands.New.FILE_FROM_TEMPLATE));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.html");
      assertEquals("Untitled file.html *", IDE.EDITOR.getTabTitle(1));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file 1.html");
      assertEquals("Untitled file 1.html *", IDE.EDITOR.getTabTitle(2));

      /*
       * 2. Open "Create file from template" form.
       */
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();
      assertTrue(IDE.TEMPLATES.isOpened());

      /*
       * 3. Select "Empty HTML" template and click "Create" button
       */
      IDE.TEMPLATES.selectTemplate(EMPTY_HTML);
      IDE.TEMPLATES.clickCreateButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.waitTabPresent(3);

      /*
       * Check, new file opened with name "Untitled file 2.html"
       */
      assertEquals("Untitled file 2.html *", IDE.EDITOR.getTabTitle(3));

      /*
       * 4. Go to file in second tab "Untitled file 1.html" and save file
       */
      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.saveAs(2, "Untitled file 1.html");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/Untitled file 1.html");

      /*
       * Close saved file
       */
      IDE.EDITOR.closeFile(2);
   }

   // //TODO fix problem in issue GWTX-100
   // //@Test
   // public void testEnablingDisablingElements() throws Exception
   // {
   // refresh();
   // IDE.WORKSPACE.selectRootItem();
   // IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
   // IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/");
   // IDE.WORKSPACE.selectItem(WS_URL + FOLDER + "/");
   //
   // //---- 1 ----------
   // //call create file from template form
   // IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
   // IDE.TEMPLATES.waitForFileFromTemplateForm();
   // IDE.TEMPLATES.checkCreateFileFromTemplateWindow();
   //
   // //---- 2 ----------
   // //select template in list
   // IDE.TEMPLATES.selectFileTemplate(EMPTY_HTML);
   //
   // IDE.TEMPLATES.checkInputFieldState(true);
   // IDE.TEMPLATES.checkButtonState(Templates.CREATE_BUTTON_ID, true);
   // IDE.TEMPLATES.checkButtonState(Templates.DELETE_BUTTON_ID, false);
   //
   //
   // String text = selenium().getValue(Templates.FILE_NAME_INPUT_LOCATOR);
   // assertEquals("Untitled file.html", text);
   //
   // //---- 3 ----------
   // //deselect template
   // selenium().controlKeyDown();
   // IDE.TEMPLATES.selectFileTemplate(EMPTY_HTML);
   // selenium().controlKeyUp();
   // Thread.sleep(TestConstants.REDRAW_PERIOD);
   //
   // IDE.TEMPLATES.checkInputFieldState(false);
   // IDE.TEMPLATES.checkButtonState(Templates.CREATE_BUTTON_ID, false);
   // IDE.TEMPLATES.checkButtonState(Templates.DELETE_BUTTON_ID, false);
   //
   // //---- 4 ----------
   // //select several templates
   // IDE.TEMPLATES.selectFileTemplate(EMPTY_HTML);
   // selenium().controlKeyDown();
   // IDE.TEMPLATES.selectFileTemplate(EMPTY_TEXT);
   // selenium().controlKeyUp();
   // Thread.sleep(TestConstants.REDRAW_PERIOD*20);
   //
   // IDE.TEMPLATES.checkInputFieldState(false);
   // IDE.TEMPLATES.checkButtonState(Templates.CREATE_BUTTON_ID, false);
   // IDE.TEMPLATES.checkButtonState(Templates.DELETE_BUTTON_ID, false);
   //
   // //---- 5 ----------
   // //select one template
   // IDE.TEMPLATES.selectFileTemplate(EMPTY_XML);
   // //remove text from name field
   // IDE.TEMPLATES.typeNameToInputField("");
   //
   // IDE.TEMPLATES.checkButtonState(Templates.CREATE_BUTTON_ID, false);
   //
   // //---- 6 ----------
   // //type some text to name field
   // IDE.TEMPLATES.typeNameToInputField("a");
   // IDE.TEMPLATES.checkButtonState(Templates.CREATE_BUTTON_ID, true);
   //
   // IDE.TEMPLATES.clickCancelButton();
   // }

   private void testTemplate(String templateName, String fileName) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();
      assertTrue(IDE.TEMPLATES.isOpened());
      // -------3-------
      // Select "Groovy REST Service" item in the "Create file" window,
      // change "File Name" field text on "Test Groovy File.groovy" name, click on "Create" button.
      IDE.TEMPLATES.selectTemplate(templateName);
      IDE.TEMPLATES.setFileName(fileName);
      IDE.TEMPLATES.clickCreateButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.waitTabPresent(1);
      // new file with appropriate titles and highlighting should be opened in the Content Panel
      assertEquals(fileName + " *", IDE.EDITOR.getTabTitle(1));
      // --------4------------
      // Click on "File->Save File As" top menu command and save file "Test Groovy File.groovy".
      IDE.EDITOR.saveAs(1, fileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + fileName);
      // new file with appropriate name should be appeared in the root folder of
      // "Workspace" panel in the "Gadget " window and in the root folder of "Server" window.
      IDE.EDITOR.closeFile(fileName);

      // check file created on server
      final String fileUrl = WS_URL + PROJECT + "/" + URLEncoder.encode(fileName, "UTF-8");
      Response response = VirtualFileSystemUtils.get(fileUrl);
      assertEquals(200, response.getStatusCode());
   }

}