/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.project;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.console.Console;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.core.event.RefreshBrowserEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAutoBeanFactory;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.services.ManageServicesEvent;
import com.codenvy.ide.extension.cloudfoundry.client.services.ManageServicesHandler;
import com.codenvy.ide.extension.cloudfoundry.client.services.ManageServicesPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.start.StartApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.update.UpdateApplicationEvent;
import com.codenvy.ide.extension.cloudfoundry.client.update.UpdateApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.update.UpdatePropertiesPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryProjectPresenter implements CloudFoundryProjectView.ActionDelegate, ManageServicesHandler
// ProjectOpenedHandler, ProjectClosedHandler,  ManageCloudFoundryProjectHandler,  ActiveProjectChangedHandler
{
   private CloudFoundryProjectView view;

   private ApplicationInfoPresenter applicationInfoPresenter;

   private UnmapUrlPresenter unmapUrlPresenter;

   private UpdatePropertiesPresenter updateProperyPresenter;

   private ManageServicesPresenter manageServicesPresenter;

   private UpdateApplicationPresenter updateApplicationPresenter;

   private EventBus eventBus;

   private ResourceProvider resourceProvider;

   private Console console;

   private CloudFoundryApplication application;

   private CloudFoundryLocalizationConstant constant;

   private CloudFoundryAutoBeanFactory autoBeanFactory;

   private StartApplicationPresenter startAppPresenter;

   private DeleteApplicationPresenter deleteAppPresenter;

   private AsyncCallback<String> appInfoChangedCallback = new AsyncCallback<String>()
   {
      @Override
      public void onSuccess(String result)
      {
         Project openedProject = resourceProvider.getActiveProject();
         if (result != null && openedProject != null && openedProject.getId().equals(result))
         {
            getApplicationInfo(openedProject);
         }
      }

      @Override
      public void onFailure(Throwable caught)
      {
         // do nothing
      }
   };

   @Inject
   protected CloudFoundryProjectPresenter(CloudFoundryProjectView view,
      ApplicationInfoPresenter applicationInfoPresenter, UnmapUrlPresenter unmapUrlPresenter,
      UpdatePropertiesPresenter updateProperyPresenter, ManageServicesPresenter manageServicesPresenter,
      UpdateApplicationPresenter updateApplicationPresenter, EventBus eventBus, ResourceProvider resourceProvider,
      Console console, CloudFoundryLocalizationConstant constant, CloudFoundryAutoBeanFactory autoBeanFactory,
      StartApplicationPresenter startAppPresenter, DeleteApplicationPresenter deleteAppPresenter)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.applicationInfoPresenter = applicationInfoPresenter;
      this.unmapUrlPresenter = unmapUrlPresenter;
      this.updateProperyPresenter = updateProperyPresenter;
      this.eventBus = eventBus;
      this.resourceProvider = resourceProvider;
      this.console = console;
      this.manageServicesPresenter = manageServicesPresenter;
      this.updateApplicationPresenter = updateApplicationPresenter;
      this.constant = constant;
      this.autoBeanFactory = autoBeanFactory;
      this.startAppPresenter = startAppPresenter;
      this.deleteAppPresenter = deleteAppPresenter;

      this.eventBus.addHandler(ManageServicesEvent.TYPE, this);
   }

   /**
    * Show dialog.
    */
   public void showDialog()
   {
      getApplicationInfo(resourceProvider.getActiveProject());

      view.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCloseClicked()
   {
      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onUpdateClicked()
   {
      // TODO
      //      IDE.eventBus().fireEvent(new UpdateApplicationEvent());
      eventBus.fireEvent(new UpdateApplicationEvent());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onLogsClicked()
   {
      getLogs();
   }

   protected void getLogs()
   {
      try
      {
         CloudFoundryClientService.getInstance().getLogs(resourceProvider.getVfsId(),
            resourceProvider.getActiveProject().getId(),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  console.print("<pre>" + result.toString() + "</pre>");
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception.getMessage()));
                  console.print(exception.getMessage());
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e.getMessage()));
         console.print(e.getMessage());

         e.printStackTrace();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onServicesClicked()
   {
      // TODO
      //      IDE.fireEvent(new ManageServicesEvent(application));
      eventBus.fireEvent(new ManageServicesEvent(application));
   }

   /**
    * Get application properties.
    * 
    * @param project
    */
   protected void getApplicationInfo(final Project project)
   {
      try
      {
         AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
         AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
            new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

         CloudFoundryClientService.getInstance().getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null,
            null, new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, new LoggedInHandler()
            {
               @Override
               public void onLoggedIn()
               {
                  getApplicationInfo(project);
               }
            }, null, eventBus, console, constant)
            {
               @Override
               protected void onSuccess(CloudFoundryApplication result)
               {
                  // TODO
                  //                  if (display == null)
                  //                  {
                  //                     display = GWT.create(Display.class);
                  //                     bindDisplay();
                  //                     IDE.getInstance().openView(display.asView());
                  //                  }
                  application = result;
                  displayApplicationProperties(result);
               }
            });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
         console.print(e.getMessage());
      }
   }

   protected void displayApplicationProperties(CloudFoundryApplication application)
   {
      view.setApplicationName(application.getName());
      view.setApplicationInstances(String.valueOf(application.getInstances()));
      view.setApplicationMemory(String.valueOf(application.getResources().getMemory()) + "MB");
      view.setApplicationModel(String.valueOf(application.getStaging().getModel()));
      view.setApplicationStack(String.valueOf(application.getStaging().getStack()));
      view.setApplicationStatus(String.valueOf(application.getState()));

      if (application.getUris() != null && application.getUris().size() > 0)
      {
         view.setApplicationUrl(application.getUris().get(0));
      }
      else
      {
         //Set empty field if we specialy unmap all urls and closed url controller window, if whe don't do this, in
         //info window will be appear old url, that is not good
         view.setApplicationUrl(null);
      }
      boolean isStarted = ("STARTED".equals(application.getState()));
      view.setStartButtonEnabled(!isStarted);
      view.setStopButtonEnabled(isStarted);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onDeleteClicked()
   {
      deleteAppPresenter.deleteApp(null, null, new AsyncCallback<String>()
      {
         @Override
         public void onSuccess(String result)
         {
            Project openedProject = resourceProvider.getActiveProject();
            if (result != null && openedProject != null
               && result.equals(openedProject.getPropertyValue("cloudfoundry-application")))
            {
               eventBus.fireEvent(new RefreshBrowserEvent(openedProject));
            }
         }

         @Override
         public void onFailure(Throwable caught)
         {
            // do nothing
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onInfoClicked()
   {
      applicationInfoPresenter.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onStartClicked()
   {
      startAppPresenter.startApp(null, appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onStopClicked()
   {
      startAppPresenter.stopApp(null, appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onRestartClicked()
   {
      startAppPresenter.restartApp(null, appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditMemoryClicked()
   {
      updateProperyPresenter.showUpdateMemoryDialog(appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditUrlClicked()
   {
      unmapUrlPresenter.showDialog(appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onEditInstancesClicked()
   {
      updateProperyPresenter.showUpdateInstancesDialog(appInfoChangedCallback);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onManageServices(ManageServicesEvent event)
   {
      getApplicationInfo(resourceProvider.getActiveProject());
   }
}