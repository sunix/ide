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
package org.exoplatform.ide.client.navigation.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveAllFilesEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class SaveAllFilesControl extends SimpleControl implements IDEControl, EditorFileContentChangedHandler,
   EditorActiveFileChangedHandler, EditorFileOpenedHandler, EditorFileClosedHandler, VfsChangedHandler,
   FileSavedHandler
{

   public static final String ID = "File/Save All";

   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.saveAllControl();

   private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();

   /**
    * Current workspace's href.
    */
   private VirtualFileSystemInfo vfsInfo;

   /**
    * 
    */
   public SaveAllFilesControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(IDEImageBundle.INSTANCE.saveAll(), IDEImageBundle.INSTANCE.saveAllDisabled());
      setEvent(new SaveAllFilesEvent());
      setGroupName(GroupNames.SAVE);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
      IDE.addHandler(FileSavedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * Update control's state.
    */
   private void updateState()
   {
      if (vfsInfo == null)
      {
         setVisible(false);
         return;
      }
      setVisible(true);

      boolean enable = false;
      for (FileModel file : openedFiles.values())
      {
         if (file.isPersisted() && file.isContentChanged())
         {
            enable = true;
            break;
         }
      }
      setEnabled(enable);
   }

   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      updateState();
   }

   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      updateState();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      updateState();
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      updateState();
   }

}