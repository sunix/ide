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
package org.exoplatform.ide.client.download;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.navigation.event.DownloadFileEvent;
import org.exoplatform.ide.client.navigation.event.DownloadFileHandler;
import org.exoplatform.ide.client.navigation.event.DownloadZippedFolderEvent;
import org.exoplatform.ide.client.navigation.event.DownloadZippedFolderHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DownloadForm implements DownloadFileHandler, DownloadZippedFolderHandler, ItemsSelectedHandler,
   ConfigurationReceivedSuccessfullyHandler
{

   private final String CONTEXT_DOWNLOAD = "/ide/downloadcontent";

   private AbsolutePanel panel;

   private Item selectedItem;

   private IDEConfiguration applicationConfiguration;

   public DownloadForm(HandlerManager eventBus)
   {
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(DownloadFileEvent.TYPE, this);
      eventBus.addHandler(DownloadZippedFolderEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);

      panel = new AbsolutePanel();
      panel.getElement().getStyle().setWidth(1, Unit.PX);
      panel.getElement().getStyle().setHeight(1, Unit.PX);
      panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
      RootPanel.get().add(panel, -10000, -10000);
   }

   private void downloadResource()
   {
      //Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
      String fileName = selectedItem.getName();

//      if (fileName.endsWith("/"))
//      {
//         fileName = fileName.substring(0, fileName.length() - 1);
//      }
//      fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

      if (!(selectedItem instanceof FileModel))
      {
         fileName += ".zip";
      }

      String path = selectedItem.getId();
      String url = applicationConfiguration.getContext() + CONTEXT_DOWNLOAD + "/" + fileName + "?repoPath=" + path;
      String iframe =
         "<iframe src=\"" + url
            + "\" frameborder=0 width=\"100%\" height=\"100%\" style=\"overflow:visible;\"></iframe>";
      panel.getElement().setInnerHTML(iframe);
   }

   public void onDownloadFile(DownloadFileEvent event)
   {
      downloadResource();
   }

   public void onDownloadZippedFolder(DownloadZippedFolderEvent event)
   {
      downloadResource();
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 0)
      {
         selectedItem = null;
      }
      else
      {
         selectedItem = event.getSelectedItems().get(0);
      }
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

}
