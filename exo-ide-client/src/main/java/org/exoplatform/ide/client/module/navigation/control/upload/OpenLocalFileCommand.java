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
package org.exoplatform.ide.client.module.navigation.control.upload;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.navigation.event.upload.UploadFileEvent;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenLocalFileCommand extends IDEControl implements ItemsSelectedHandler, PanelSelectedHandler
{

   private final static String ID = "File/Open Local File...";

   private final static String TITLE = "Open Local File...";

   private final static String PROMPT = "Open Local File...";

   private boolean browserPanelSelected = true;

   private List<Item> selectedItems = new ArrayList<Item>();

   public OpenLocalFileCommand(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(PROMPT);
      //setIcon(Images.MainMenu.UPLOAD_FILE);
      setImages(IDEImageBundle.INSTANCE.openLocalFile(), IDEImageBundle.INSTANCE.openLocalFileDisabled());
      setEvent(new UploadFileEvent(true));
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemsSelectedEvent.TYPE, this);
      addHandler(PanelSelectedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (browserPanelSelected)
      {
         if (selectedItems.size() == 1)
         {
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
         }
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      updateEnabling();
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserPanelSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
   }

}
