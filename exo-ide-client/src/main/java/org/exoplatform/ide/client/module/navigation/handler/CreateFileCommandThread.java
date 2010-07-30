/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.handler;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.editor.EditorUtil;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.template.FileTemplates;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateNewFileEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateNewFileHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.webdav.NodeTypeUtil;
import org.exoplatform.ide.client.template.CreateFileFromTemplateForm;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CreateFileCommandThread implements CreateNewFileHandler, CreateFileFromTemplateHandler,
   TemplateListReceivedHandler, RegisterEventHandlersHandler, ItemsSelectedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationContext context;

   private List<Item> selectedItems = new ArrayList<Item>();

   public CreateFileCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);

      eventBus.addHandler(RegisterEventHandlersEvent.TYPE, this);
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      eventBus.addHandler(CreateNewFileEvent.TYPE, this);
      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onCreateNewFile(CreateNewFileEvent event)
   {
      Item item = selectedItems.get(0);

      String extension = IDEMimeTypes.getExtensionsMap().get(event.getMimeType());

      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/") + 1);
      }

      String content = FileTemplates.getTemplateFor(event.getMimeType());

      String fileName = "Untitled file." + extension;
      int index = 1;
      while (context.getOpenedFiles().get(href + fileName) != null)
      {
         fileName = "Untitled file " + index + "." + extension;
         index++;
      }

      File newFile = new File(href + fileName);
      newFile.setContentType(event.getMimeType());
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(event.getMimeType()));
      newFile.setIcon(ImageUtil.getIcon(event.getMimeType()));
      newFile.setNewFile(true);
      newFile.setContent(content);
      newFile.setContentChanged(true);

      try
      {
         Editor editor = EditorUtil.getEditor(event.getMimeType(), context);
         eventBus.fireEvent(new EditorOpenFileEvent(newFile, editor));
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError("Can't find editor for type <b>" + event.getMimeType() + "</b>");
      }
   }

   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      TemplateService.getInstance().getTemplates();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler#onTemplateListReceived(org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent)
    */
   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      handlers.removeHandler(TemplateListReceivedEvent.TYPE);
      context.setTemplateList(event.getTemplateList());
      new CreateFileFromTemplateForm(eventBus, selectedItems, context.getTemplateList().getTemplates());
   }

}
