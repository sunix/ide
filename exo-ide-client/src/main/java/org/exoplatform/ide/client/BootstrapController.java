/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.workspace.WorkspacePeresenter;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.ResourceException;
import org.exoplatform.ide.resources.ResourceProvider;
import org.exoplatform.ide.resources.event.StartableExtension;
import org.exoplatform.ide.resources.event.ExtensionInitializedEvent;
import org.exoplatform.ide.resources.event.ExtensionInitializedHandler;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.properties.Property;

import java.util.Date;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 13, 2012  
 */
public class BootstrapController
{

   WorkspacePeresenter workspacePeresenter;

   EventBus eventBus;

   ResourceProvider resourceProvider;

   ProjectExplorerPresenter projectExpolrerPresenter;

   @Inject
   public BootstrapController(WorkspacePeresenter workspacePeresenter, ResourceProvider resourceProvider,
      ProjectExplorerPresenter projectExpolorerPresenter, EventBus eventBus)
   {
      this.workspacePeresenter = workspacePeresenter;
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.projectExpolrerPresenter = projectExpolorerPresenter;

      JsonArray<StartableExtension> pendingServices = JsonCollections.<StartableExtension> createArray();

      pendingServices.add(resourceProvider);

      initializeHandlers(pendingServices);
      initializeServices(resourceProvider);
   }

   /**
    * @param resourceProvider
    */
   public void initializeServices(ResourceProvider resourceProvider)
   {
      try
      {
         resourceProvider.start();
      }
      catch (Exception e)
      {
         GWT.log("BootstrapController:Failed to start resource provider" + e.getMessage() + ">" + e);
         RootLayoutPanel.get().add(
            new Label("BootstrapController:Failed to start resource provider" + e.getMessage() + ">" + e));
      }
   }

   /**
    * @param pendingServices
    */
   private void initializeHandlers(final JsonArray<StartableExtension> pendingServices)
   {
      eventBus.addHandler(ExtensionInitializedEvent.TYPE, new ExtensionInitializedHandler()
      {

         @Override
         public void onExtensionInitialized(ExtensionInitializedEvent event)
         {
            pendingServices.remove(event.getExtension());
            // services started
            if (pendingServices.size() == 0)
            {
               GWT.log("All services initialized. Starting.");
               onInitialized();
            }
         }
      });
   }

   /**
    * 
    */
   void onInitialized()
   {
      workspacePeresenter.go(RootLayoutPanel.get());

      try
      {
         resourceProvider.createProject("Test Project " + (new Date().getTime()),
            JsonCollections.<Property> createArray(), new AsyncCallback<Project>()
            {

               @Override
               public void onSuccess(final Project project)
               {
                  try
                  {
                     project.createFolder(project, "Test Folder", new AsyncCallback<Folder>()
                     {

                        @Override
                        public void onSuccess(Folder result)
                        {
                           try
                           {
                              project.createFile(result, "Test file on FS", "This is file content of the file from VFS", "text/text-pain",
                                 new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                       projectExpolrerPresenter.setContent(project.getParent());
                                    }

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       GWT.log("Error creating demo folder" + caught);
                                    }
                                 });
                           }
                           catch (ResourceException e)
                           {
                              GWT.log("Error creating demo file" + e);
                           }
                        }

                        @Override
                        public void onFailure(Throwable caught)
                        {
                           GWT.log("Error creating demo folder" + caught);
                        }
                     });
                  }
                  catch (ResourceException e)
                  {
                     GWT.log("Error creating demo project" + e);
                  }
               }

               @Override
               public void onFailure(Throwable caught)
               {
                  GWT.log("Error creating demo content" + caught);
               }
            });
      }
      catch (ResourceException e)
      {
         GWT.log("Error creating demo content" + e);
      }
   }
}
