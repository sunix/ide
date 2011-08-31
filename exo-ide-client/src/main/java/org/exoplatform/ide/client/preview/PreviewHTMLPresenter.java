/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.preview;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewHTMLPresenter implements PreviewHTMLHandler, ViewClosedHandler, EditorActiveFileChangedHandler
{

   public interface Display
   {

      /**
       * ID of Preview View
       */
      String ID = "idePreviewHTMLView";

      /**
       * Shows preview
       * 
       * @param url
       */
      void showPreview(String url);

      /**
       * Sets is preview available
       * 
       * @param available
       */
      void setPreviewAvailable(boolean available);

      void setMessage(String message);

   }
   
   private static final String PREVIEW_NOT_AVAILABLE = org.exoplatform.ide.client.IDE.OPERATION_CONSTANT.previewNotAvailable();
   
   private static final String PREVIEW_NOT_AVAILABLE_SAVE_FILE = org.exoplatform.ide.client.IDE.OPERATION_CONSTANT.previewNotAvailableSaveFile();

   /**
    * Instance of attached Display
    */
   private Display display;

   private FileModel activeFile;

   public PreviewHTMLPresenter()
   {
      IDE.EVENT_BUS.addHandler(PreviewHTMLEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      
      IDE.getInstance().addControl(new PreviewHTMLControl(), DockTarget.TOOLBAR, true);
   }

   /**
    * Do preview HTML file
    * 
    * @see org.exoplatform.ide.client.preview.event.PreviewHTMLHandler#onPreviewHTMLFile(org.exoplatform.ide.client.preview.event.PreviewHTMLEvent)
    */
   @Override
   public void onPreviewHTMLFile(PreviewHTMLEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView((View)display);
      }
      previewActiveFile();
   }

   private void previewActiveFile()
   {
      if (activeFile == null)
      {
         display.setPreviewAvailable(false);
         display.setMessage(PREVIEW_NOT_AVAILABLE);
         return;
      }

      if (MimeType.TEXT_HTML.equals(activeFile.getMimeType()))
      {
         if (!activeFile.isPersisted())
         {
            display.setPreviewAvailable(false);
            display.setMessage(PREVIEW_NOT_AVAILABLE_SAVE_FILE);
         }
         else
         {
            display.setPreviewAvailable(true);
            display.showPreview(activeFile.getLinkByRelation(File.REL_CONTENT).getHref());
         }
      }
      else
      {
         display.setPreviewAvailable(false);
         display.setMessage(PREVIEW_NOT_AVAILABLE);
      }
   }

   /**
    * Handler of ViewClosed event.
    * Clear display instance if closed view is Preview.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (display != null)
      {
         previewActiveFile();
      }
   }

}
