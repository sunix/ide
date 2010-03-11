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
package org.exoplatform.ideall.client.editor.custom;

import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedHandler;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithPresenter implements FileContentReceivedHandler, ApplicationContextSavedHandler
{

   public interface Display
   {

      void closeForm();

      EditorsListGrid getEditorsListGrid();

      HasValue<Boolean> getIsDefaultCheckItem();

      HasClickHandlers getOkButton();

      void enableOpenButton();

      HasClickHandlers getCancelButton();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private Handlers handlers;

   private Editor selectedEditor;

   public OpenFileWithPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ApplicationContextSavedEvent.TYPE, this);
      
      display = d;
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }
      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            openFile();
         }
      });

      display.getEditorsListGrid().addSelectionHandler(new SelectionHandler<Editor>()
      {

         public void onSelection(SelectionEvent<Editor> event)
         {
            if (event.getSelectedItem() == selectedEditor)
            {
               return;
            }

            selectedEditor = event.getSelectedItem();
            display.enableOpenButton();
         }

      });

      fillEditorsListGrid();

   }

   private void fillEditorsListGrid()
   {

      String mimeType = ((File)context.getSelectedItems().get(0)).getContentType();

      try
      {
         List<Editor> editorsItems = EditorFactory.getEditors(mimeType);
         display.getEditorsListGrid().setValue(editorsItems);
      }
      catch (EditorNotFoundException e)
      {
         String message = "Editors not found";
         eventBus.fireEvent(new ExceptionThrownEvent(new Exception(message)));
      }
   }

   private void openFile()
   {
      if (display.getIsDefaultCheckItem().getValue() == null || display.getIsDefaultCheckItem().getValue() == false)
      {
         context.setSelectedEditorDescriptor(selectedEditor.getDescription());
         DataService.getInstance().getFileContent((File)context.getSelectedItems().get(0));
      }
      else
      {
         String mimeType = ((File)context.getSelectedItems().get(0)).getContentType();
         
         context.getDefaultEditors().put(mimeType, selectedEditor.getDescription());
         
         SettingsService.getInstance().saveSetting(context);
      }

   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      display.closeForm();
   }

   public void onApplicationContextSaved(ApplicationContextSavedEvent event)
   {
      DataService.getInstance().getFileContent((File)context.getSelectedItems().get(0));
   }

}
