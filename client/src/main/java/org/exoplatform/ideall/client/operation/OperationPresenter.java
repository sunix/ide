/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.operation;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.event.file.SaveFileAsEvent;
import org.exoplatform.ideall.client.event.file.SaveFileAsHandler;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.gadget.GadgetMetadata;
import org.exoplatform.ideall.client.model.gadget.GadgetService;
import org.exoplatform.ideall.client.model.gadget.TokenResponse;
import org.exoplatform.ideall.client.model.gadget.event.GadgetMetadaRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.event.GadgetMetadaRecievedHandler;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedHandler;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.operation.output.OutputEvent;
import org.exoplatform.ideall.client.operation.output.OutputHandler;
import org.exoplatform.ideall.client.operation.preview.PreviewFileEvent;
import org.exoplatform.ideall.client.operation.preview.PreviewFileHandler;
import org.exoplatform.ideall.client.operation.properties.event.ShowPropertiesEvent;
import org.exoplatform.ideall.client.operation.properties.event.ShowPropertiesHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OperationPresenter implements ShowPropertiesHandler, EditorActiveFileChangedHandler, OutputHandler,
   PreviewFileHandler, GadgetMetadaRecievedHandler, SecurityTokenRecievedHandler, SaveFileAsHandler
{

   public interface Display
   {

      void showOutput();

      void showProperties(File file);

      void showPreview(String path);

      void closePreviewTab();

      void closePropertiesTab();

      void changeActiveFile(File file);

      void showGadget(GadgetMetadata metadata);
      
      void closeGadgetPreviewTab();

   }

   private Display display;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   public OperationPresenter(HandlerManager eventBus, ApplicationContext context)
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
      display = d;

      handlers.addHandler(ShowPropertiesEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(OutputEvent.TYPE, this);
      handlers.addHandler(PreviewFileEvent.TYPE, this);
      handlers.addHandler(GadgetMetadaRecievedEvent.TYPE, this);
      handlers.addHandler(SecurityTokenRecievedEvent.TYPE, this);

      handlers.addHandler(SaveFileAsEvent.TYPE, this);
   }

   public void onShowProperties(ShowPropertiesEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      display.showProperties(context.getActiveFile());
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      display.closePreviewTab();
      display.closeGadgetPreviewTab();
      
      if (event.getFile() == null)
      {
         display.closePropertiesTab();
      }
      else
      {
         display.changeActiveFile(event.getFile());
      }
   }

   public void onOutput(OutputEvent event)
   {
      eventBus.fireEvent(new RestorePerspectiveEvent());
      display.showOutput();
   }

   public void onPreviewFile(PreviewFileEvent event)
   {
//      TODO
      display.closePreviewTab();
      display.closeGadgetPreviewTab();
      File file = context.getActiveFile();

      if (file.isNewFile())
      {
         Dialogs.getInstance().showInfo("You should save the file!");
         return;
      }
      
      eventBus.fireEvent(new RestorePerspectiveEvent());
      
      if (MimeType.GOOGLE_GADGET.equals(file.getContentType()))
      {
         previewGadget();
      }
      else
      {
         display.showPreview(file.getHref());
      }
   }

   private void previewGadget()
   {
//      //FIXME:  
//      //TODO
//      String gadgetURL =
//         Location.getProtocol() + "//" + Location.getHost() + Configuration.getInstance().getPublicContext() + "/jcr"
//            + context.getActiveFile().getPath();
//      String owner = "root";
//      String viewer = "root";
//      Long moduleId = 0L;
//      String container = "default";
//      String domain = null;
//      TokenRequest tokenRequest = new TokenRequest(URL.encode(gadgetURL), owner, viewer, moduleId, container, domain);
//      GadgetService.getInstance().getSecurityToken(tokenRequest);
   }

   public void onSecurityTokenRecieved(SecurityTokenRecievedEvent securityTokenRecievedEvent)
   {
      TokenResponse tokenResponse = securityTokenRecievedEvent.getTokenResponse();
      GadgetService.getInstance().getGadgetMetadata(tokenResponse);
   }

   public void onMetadataRecieved(GadgetMetadaRecievedEvent event)
   {
      display.showGadget(event.getMetadata());
   }

   public void onSaveFileAs(SaveFileAsEvent event)
   {
      display.closePropertiesTab();
   }

}
