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

import com.google.gwt.http.client.URL;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.ClearFocusForm;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.exoplatform.ide.client.navigation.control.CopyItemsCommand;
import org.exoplatform.ide.client.navigation.control.CutItemsCommand;
import org.exoplatform.ide.client.navigation.control.DeleteItemCommand;
import org.exoplatform.ide.client.navigation.control.DownloadFileCommand;
import org.exoplatform.ide.client.navigation.control.DownloadZippedFolderCommand;
import org.exoplatform.ide.client.navigation.control.GetFileURLControl;
import org.exoplatform.ide.client.navigation.control.GoToFolderControl;
import org.exoplatform.ide.client.navigation.control.OpenFileByPathCommand;
import org.exoplatform.ide.client.navigation.control.OpenFileWithControl;
import org.exoplatform.ide.client.navigation.control.OpenLocalFileCommand;
import org.exoplatform.ide.client.navigation.control.PasteItemsCommand;
import org.exoplatform.ide.client.navigation.control.RefreshBrowserControl;
import org.exoplatform.ide.client.navigation.control.RenameItemCommand;
import org.exoplatform.ide.client.navigation.control.SaveAllFilesCommand;
import org.exoplatform.ide.client.navigation.control.SaveFileAsCommand;
import org.exoplatform.ide.client.navigation.control.SaveFileAsTemplateCommand;
import org.exoplatform.ide.client.navigation.control.SaveFileCommand;
import org.exoplatform.ide.client.navigation.control.SearchFilesCommand;
import org.exoplatform.ide.client.navigation.control.UploadFileCommand;
import org.exoplatform.ide.client.navigation.control.UploadFolderControl;
import org.exoplatform.ide.client.navigation.control.newitem.CreateFileFromTemplateControl;
import org.exoplatform.ide.client.navigation.control.newitem.CreateFolderControl;
import org.exoplatform.ide.client.navigation.control.newitem.NewFileCommandMenuGroup;
import org.exoplatform.ide.client.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ide.client.navigation.event.CopyItemsEvent;
import org.exoplatform.ide.client.navigation.event.CopyItemsHandler;
import org.exoplatform.ide.client.navigation.event.CreateFolderEvent;
import org.exoplatform.ide.client.navigation.event.CreateFolderHandler;
import org.exoplatform.ide.client.navigation.event.CutItemsEvent;
import org.exoplatform.ide.client.navigation.event.CutItemsHandler;
import org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.navigation.event.OpenFileByPathEvent;
import org.exoplatform.ide.client.navigation.event.OpenFileByPathHandler;
import org.exoplatform.ide.client.navigation.event.RenameItemEvent;
import org.exoplatform.ide.client.navigation.event.RenameItemHander;
import org.exoplatform.ide.client.navigation.event.SaveFileAsTemplateEvent;
import org.exoplatform.ide.client.navigation.event.SaveFileAsTemplateHandler;
import org.exoplatform.ide.client.navigation.event.UploadFileEvent;
import org.exoplatform.ide.client.navigation.event.UploadFileHandler;
import org.exoplatform.ide.client.navigation.handler.CreateFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.FileClosedHandler;
import org.exoplatform.ide.client.navigation.handler.GoToFolderCommandHandler;
import org.exoplatform.ide.client.navigation.handler.OpenFileCommandHandler;
import org.exoplatform.ide.client.navigation.handler.PasteItemsCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveAllFilesCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileAsCommandHandler;
import org.exoplatform.ide.client.navigation.handler.SaveFileCommandHandler;
import org.exoplatform.ide.client.navigation.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.navigation.ui.CreateFolderForm;
import org.exoplatform.ide.client.navigation.ui.RenameItemForm;
import org.exoplatform.ide.client.statusbar.NavigatorStatusControl;
import org.exoplatform.ide.client.template.ui.SaveAsTemplateForm;
import org.exoplatform.ide.client.upload.OpenFileByPathForm;
import org.exoplatform.ide.client.upload.OpenLocalFileForm;
import org.exoplatform.ide.client.upload.UploadFileForm;
import org.exoplatform.ide.client.upload.UploadForm;
import org.exoplatform.ide.client.versioning.control.RestoreToVersionControl;
import org.exoplatform.ide.client.versioning.control.ViewNextVersionControl;
import org.exoplatform.ide.client.versioning.control.ViewPreviousVersionControl;
import org.exoplatform.ide.client.versioning.control.ViewVersionHistoryControl;
import org.exoplatform.ide.client.versioning.control.ViewVersionListControl;
import org.exoplatform.ide.client.versioning.handler.RestoreToVersionCommandHandler;
import org.exoplatform.ide.client.versioning.handler.ShowVersionListCommandHandler;
import org.exoplatform.ide.client.versioning.handler.VersionHistoryCommandHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class NavigationModule implements UploadFileHandler, SaveFileAsTemplateHandler,
   CreateFolderHandler, CopyItemsHandler, CutItemsHandler, RenameItemHander, ApplicationSettingsReceivedHandler,
   ItemsSelectedHandler, EditorFileOpenedHandler, EditorFileClosedHandler, EntryPointChangedHandler,
   ConfigurationReceivedSuccessfullyHandler, EditorActiveFileChangedHandler, InitializeServicesHandler,
   OpenFileByPathHandler
{
   private HandlerManager eventBus;

   private ApplicationContext context;

   private ApplicationSettings applicationSettings;

   private IDEConfiguration applicationConfiguration;

   private List<Item> selectedItems = new ArrayList<Item>();

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private String entryPoint;

   private File activeFile;

   private Map<String, String> lockTokens;

   public NavigationModule(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      NewFilePopupMenuControl newFilePopupMenuControl = new NewFilePopupMenuControl();

      eventBus.fireEvent(new RegisterControlEvent(newFilePopupMenuControl, DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new NewFileCommandMenuGroup()));
      //eventBus.fireEvent(new RegisterControlEvent(new CreateProjectFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFileFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateFolderControl()));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New XML", IDE.IDE_LOCALIZATION_CONSTANT
         .controlNewXmlTitle(), IDE.IDE_LOCALIZATION_CONSTANT.controlNewXmlPrompt(), Images.FileTypes.XML,
         MimeType.TEXT_XML).setGroup(1)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New HTML", IDE.IDE_LOCALIZATION_CONSTANT
         .controlNewHtmlTitle(), IDE.IDE_LOCALIZATION_CONSTANT.controlNewHtmlPrompt(), Images.FileTypes.HTML,
         MimeType.TEXT_HTML).setGroup(1)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New TEXT", IDE.IDE_LOCALIZATION_CONSTANT
         .controlNewTextTitle(), IDE.IDE_LOCALIZATION_CONSTANT.controlNewTextPrompt(), Images.FileTypes.TXT,
         MimeType.TEXT_PLAIN).setGroup(1)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Java Script",
         IDE.IDE_LOCALIZATION_CONSTANT.controlNewJavascriptTitle(), IDE.IDE_LOCALIZATION_CONSTANT
            .controlNewJavascriptPrompt(), Images.FileTypes.JAVASCRIPT, MimeType.APPLICATION_JAVASCRIPT).setGroup(1)));
      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New CSS", IDE.IDE_LOCALIZATION_CONSTANT
         .controlNewCssTitle(), IDE.IDE_LOCALIZATION_CONSTANT.controlNewCssPrompt(), Images.FileTypes.CSS,
         MimeType.TEXT_CSS).setGroup(1)));
      /*      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New JSON File", "JSON File",
               "Create New JSON File", Images.FileTypes.JSON, MimeType.APPLICATION_JSON))); */
      eventBus.fireEvent(new RegisterControlEvent(new OpenFileWithControl()));

      //      eventBus.fireEvent(new RegisterControlEvent(new ViewItemPropertiesCommand(), DockTarget.TOOLBAR, true));

      eventBus.fireEvent(new RegisterControlEvent(new ViewVersionHistoryControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewVersionListControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewPreviousVersionControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new ViewNextVersionControl(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new RestoreToVersionControl(), DockTarget.TOOLBAR, true));

      eventBus.fireEvent(new RegisterControlEvent(new UploadFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new UploadFolderControl()));
      eventBus.fireEvent(new RegisterControlEvent(new OpenLocalFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new OpenFileByPathCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadFileCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DownloadZippedFolderCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new SaveAllFilesCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new SaveFileAsTemplateCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new CutItemsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new CopyItemsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new PasteItemsCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new RenameItemCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DeleteItemCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new SearchFilesCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new RefreshBrowserControl(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new GoToFolderControl()));
      eventBus.fireEvent(new RegisterControlEvent(new GetFileURLControl()));
      eventBus.fireEvent(new RegisterControlEvent(new NavigatorStatusControl(), DockTarget.STATUSBAR));
      //eventBus.fireEvent(new RegisterControlEvent(new CreateProjectTemplateControl()));

      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      eventBus.addHandler(OpenFileByPathEvent.TYPE, this);
      eventBus.addHandler(UploadFileEvent.TYPE, this);
      eventBus.addHandler(SaveFileAsTemplateEvent.TYPE, this);
      eventBus.addHandler(CreateFolderEvent.TYPE, this);
      eventBus.addHandler(CopyItemsEvent.TYPE, this);
      eventBus.addHandler(CutItemsEvent.TYPE, this);

      eventBus.addHandler(RenameItemEvent.TYPE, this);

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);

      new CreateFileCommandHandler(eventBus);
      new CreateFileFromTemplatePresenter(eventBus);
      new OpenFileCommandHandler(eventBus);
      new SaveFileCommandHandler(eventBus);
      new SaveFileAsCommandHandler(eventBus);
      new SaveAllFilesCommandHandler(eventBus);
      new GoToFolderCommandHandler(eventBus);
      new PasteItemsCommandHandler(eventBus, context);
      new FileClosedHandler(eventBus);

      new ShowVersionListCommandHandler(eventBus);
      new VersionHistoryCommandHandler(eventBus);
      new RestoreToVersionCommandHandler(eventBus);

      new WorkspacePresenter(eventBus);
      new SearchFilesPresenter(eventBus, selectedItems, entryPoint);
      new SearchResultsPresenter(eventBus);
      new DeleteItemsPresenter(eventBus);
      new GetItemURLPresenter(eventBus);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = applicationSettings.getValueAsMap("lock-tokens");
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      new WebDavVirtualFileSystem(eventBus, event.getLoader(), ImageUtil.getIcons(), event
         .getApplicationConfiguration().getContext());
   }

   public void onUploadFile(UploadFileEvent event)
   {
      String path = "";

      if (selectedItems == null || selectedItems.size() == 0)
      {
         if (UploadFileEvent.UploadType.FILE.equals(event.getUploadType())
            || UploadFileEvent.UploadType.FOLDER.equals(event.getUploadType()))
         {
            Dialogs.getInstance().showInfo(IDE.ERRORS_CONSTANT.navigationUploadNoTargetSelected());
            return;
         }
      }
      else
      {
         Item item = selectedItems.get(0);

         path = item.getHref();
         if (item instanceof File)
         {
            path = path.substring(path.lastIndexOf("/"));
         }
      }
      //      eventBus.fireEvent(new ClearFocusEvent());
      ClearFocusForm.getInstance().clearFocus();
      if (UploadFileEvent.UploadType.OPEN_FILE.equals(event.getUploadType()))
      {
         new OpenLocalFileForm(eventBus, selectedItems, path, applicationConfiguration);
      }
      else if (UploadFileEvent.UploadType.FILE.equals(event.getUploadType()))
      {
         new UploadFileForm(eventBus, selectedItems, path, applicationConfiguration);
      }
      else if (UploadFileEvent.UploadType.FOLDER.equals(event.getUploadType()))
      {
         new UploadForm(eventBus, selectedItems, path, applicationConfiguration);
      }

   }

   public void onSaveFileAsTemplate(SaveFileAsTemplateEvent event)
   {
      new SaveAsTemplateForm(eventBus, activeFile);
   }

   public void onCreateFolder(CreateFolderEvent event)
   {
      Item item = selectedItems.get(0);
      String itemHref = item.getHref();
      if (item instanceof File)
      {
         itemHref = itemHref.substring(0, itemHref.lastIndexOf("/") + 1);
      }

      final String href = itemHref;

      final CreateFolderForm form = new CreateFolderForm(eventBus, selectedItems.get(0), href);
      form.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            form.closeForm();
         }
      });

      form.getCreateButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            createFolder(href, form);
         }
        
      });

      form.getFolderNameFiledKeyPressed().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
            {
               createFolder(href, form);
            }
         }
      });
   }
   
   /**
    * @param href - href of parent folder
    * @param form - {@link CreateFolderForm}
    */
   private void createFolder(final String href, final CreateFolderForm form)
   {
      final String newFolderHref = href + URL.encodePathSegment(form.getFolderNameField().getValue()) + "/";
      Folder newFolder = new Folder(newFolderHref);
      VirtualFileSystem.getInstance().createFolder(newFolder, new AsyncRequestCallback<Folder>()
      {
         @Override
         protected void onSuccess(Folder result)
         {
            eventBus.fireEvent(new RefreshBrowserEvent(new Folder(href), result));
            form.closeForm();
         }
      });
   }

   public void onCopyItems(CopyItemsEvent event)
   {
      context.getItemsToCopy().clear();
      context.getItemsToCut().clear();
      context.getItemsToCopy().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onCutItems(CutItemsEvent event)
   {
      context.getItemsToCut().clear();
      context.getItemsToCopy().clear();

      context.getItemsToCut().addAll(selectedItems);
      eventBus.fireEvent(new ItemsToPasteSelectedEvent());
   }

   public void onRenameItem(RenameItemEvent event)
   {
      new RenameItemForm(eventBus, selectedItems, openedFiles, lockTokens);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   public void onOpenFileByPath(OpenFileByPathEvent event)
   {
      new OpenFileByPathForm(eventBus);
   }

}
