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
package org.exoplatform.ide.client.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.vfs.event.SearchResultReceivedEvent;
import org.exoplatform.ide.client.navigation.event.SearchFilesEvent;
import org.exoplatform.ide.client.navigation.event.SearchFilesHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SearchFilesPresenter implements SearchFilesHandler, ViewOpenedHandler, ViewClosedHandler, ItemsSelectedHandler,
   EntryPointChangedHandler
{

   public interface Display extends IsView
   {

      HasClickHandlers getSearchButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getSearchContentItem();

      HasValue<String> getPathItem();

      HasValue<String> getMimeTypeItem();

      void setMimeTypeValues(String[] mimeTypes);

   }
   
   private static final String SEARCH_ERROR_MESSAGE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.searchFileSearchError();

   private Display display;

   private List<Item> selectedItems;

   private String entryPoint;

   private HandlerManager eventBus;

   public SearchFilesPresenter(HandlerManager eventBus, List<Item> selectedItems, String entryPoint)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.entryPoint = entryPoint;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
      eventBus.addHandler(SearchFilesEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getSearchButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doSearch();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      setPath();
      fillMimeTypes();
   }

   private void setPath()
   {
    //TODO
//      String path;
//      if (selectedItems.size() == 0)
//      {
//         path = "/";
//      }
//      else
//      {
//         Item selectedItem = selectedItems.get(0);
//
//         String href = selectedItem.getHref();
//         if (selectedItem instanceof File)
//         {
//            href = href.substring(0, href.lastIndexOf("/") + 1);
//         }
//
//         path = href.substring(entryPoint.length() - 1);
//      }
//
//      display.getPathItem().setValue(path);
   }

   private void doSearch()
   {
      //TODO
//      String content = display.getSearchContentItem().getValue();
//      String contentType = display.getMimeTypeItem().getValue();
//
//      Item item = selectedItems.get(0);
//
//      String path = item.getHref();
//      path = path.substring(entryPoint.length());
//      if (item instanceof File)
//      {
//         path = path.substring(0, path.lastIndexOf("/") + 1);
//      }
//
//      if (!"".equals(path) && !path.startsWith("/"))
//      {
//         path = "/" + path;
//      }
//
//      final Folder folder = new Folder(entryPoint);
//      VirtualFileSystem.getInstance().search(folder, content, contentType, path, new AsyncRequestCallback<Folder>()
//      {
//         @Override
//         protected void onSuccess(Folder result)
//         {
//            eventBus.fireEvent(new SearchResultReceivedEvent(folder));
//            IDE.getInstance().closeView(display.asView().getId());
//         }
//
//         @Override
//         protected void onFailure(Throwable exception)
//         {
//            eventBus.fireEvent(new ExceptionThrownEvent(exception, SEARCH_ERROR_MESSAGE));
//         }
//      });
   }

   private void fillMimeTypes()
   {
      String[] mimeTypes = new String[10];
      mimeTypes[0] = MimeType.TEXT_HTML;
      mimeTypes[1] = MimeType.TEXT_CSS;
      mimeTypes[2] = MimeType.TEXT_PLAIN;
      mimeTypes[3] = MimeType.APPLICATION_X_JAVASCRIPT;
      mimeTypes[4] = MimeType.APPLICATION_JAVASCRIPT;
      mimeTypes[5] = MimeType.TEXT_JAVASCRIPT;
      mimeTypes[6] = MimeType.TEXT_XML;
      mimeTypes[7] = MimeType.GROOVY_SERVICE;
      mimeTypes[8] = MimeType.APPLICATION_GROOVY;
      mimeTypes[9] = MimeType.GOOGLE_GADGET;
      display.setMimeTypeValues(mimeTypes);
   }

   public void onSearchFiles(SearchFilesEvent event)
   {
      if (display != null) {
         return;
      }
      
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
      if (display != null)
      {
         setPath();
      }
   }

   @Override
   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

}
