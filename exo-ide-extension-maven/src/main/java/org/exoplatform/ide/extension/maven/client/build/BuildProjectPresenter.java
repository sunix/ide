/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.maven.client.build;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.maven.client.BuilderClientService;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.control.BuildProjectControl;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectHandler;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Presenter for created builder view. The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectPresenter.java Feb 17, 2012 5:39:10 PM azatsarynnyy $
 *
 */
public class BuildProjectPresenter implements BuildProjectHandler, ItemsSelectedHandler, ViewClosedHandler,
   VfsChangedHandler
{
   public interface Display extends IsView
   {
      void output(String text);

      void startAnimation();

      void stopAnimation();
   }

   private Display display;

   private static final String BUILD_SUCCESS = BuilderExtension.LOCALIZATION_CONSTANT.buildSuccess();

   private static final String BUILD_FAILED = BuilderExtension.LOCALIZATION_CONSTANT.buildFailed();

   /**
    * Identifier of project we want to send for build.
    */
   private String projectId = null;

   /**
    * The build's identifier.
    */
   private String buildID = null;

   /**
    * Delay in millisecond between build status request.
    */
   private static final int delay = 3000;

   /**
    * Status of previously build.
    */
   private Status previousStatus = null;

   /**
    * Build of another project is performed.
    */
   private boolean buildInProgress = false;

   /**
    * View closed flag.
    */
   private boolean closed = true;

   /**
    * Selected items in browser tree.
    */
   protected List<Item> selectedItems;

   /**
    * Current virtual file system.
    */
   protected VirtualFileSystemInfo vfs;

   /**
    * Project for build.
    */
   private ProjectModel project;

   protected RequestStatusHandler statusHandler;

   public BuildProjectPresenter()
   {
      IDE.getInstance().addControl(new BuildProjectControl());

      IDE.addHandler(BuildProjectEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.event.BuildProjectHandler#onBuildProject(org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent)
    */
   @Override
   public void onBuildProject(BuildProjectEvent event)
   {
      if (buildInProgress)
      {
         String message = BuilderExtension.LOCALIZATION_CONSTANT.buildInProgress(project.getPath().substring(1));
         Dialogs.getInstance().showError(message);
         return;
      }

      project = event.getProject();
      if (project == null && makeSelectionCheck())
      {
         project = ((ItemContext)selectedItems.get(0)).getProject();
      }

      statusHandler = new BuildRequestStatusHandler(project.getPath());

      doBuild();
   }

   /**
    * Start the build of project.
    */
   private void doBuild()
   {
      projectId = project.getId();
      statusHandler.requestInProgress(projectId);

      try
      {
         BuilderClientService.getInstance().build(projectId, vfs.getId(),
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  buildID = result.substring(result.lastIndexOf("/") + 1);
                  buildInProgress = true;
                  showBuildMessage("Building project <b>" + project.getPath().substring(1) + "</b>");
                  display.startAnimation();
                  previousStatus = null;
                  refreshBuildStatusTimer.schedule(delay);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  statusHandler.requestError(projectId, exception);
                  IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
      }
   }

   /**
    * A timer for periodically sending request of build status.
    */
   private Timer refreshBuildStatusTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            AutoBean<BuildStatus> buildStatus = BuilderExtension.AUTO_BEAN_FACTORY.create(BuildStatus.class);
            AutoBeanUnmarshaller<BuildStatus> unmarshaller = new AutoBeanUnmarshaller<BuildStatus>(buildStatus);
            BuilderClientService.getInstance().status(buildID, new AsyncRequestCallback<BuildStatus>(unmarshaller)
            {
               @Override
               protected void onSuccess(BuildStatus response)
               {
                  updateBuildStatus(response);

                  Status status = response.getStatus();
                  if (status == Status.IN_PROGRESS)
                  {
                     schedule(delay);
                  }
                  else if (status == Status.FAILED)
                  {
                     showLog();
                  }
               }

               protected void onFailure(Throwable exception)
               {
                  buildInProgress = false;
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               };

            });
         }
         catch (RequestException e)
         {
            buildInProgress = false;
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   /**
    * Output a log of build.
    */
   private void showLog()
   {
      try
      {
         BuilderClientService.getInstance().log(buildID,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  showBuildMessage(result.toString());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.INFO));
      }
   }

   /**
    * Check for status and display necessary messages.
    * 
    * @param buildStatus status of build
    */
   private void updateBuildStatus(BuildStatus buildStatus)
   {
      Status status = buildStatus.getStatus();

      if (status == Status.IN_PROGRESS && previousStatus != Status.IN_PROGRESS)
      {
         previousStatus = Status.IN_PROGRESS;
         return;
      }

      if ((status == Status.SUCCESSFUL && previousStatus != Status.SUCCESSFUL)
         || (status == Status.FAILED && previousStatus != Status.FAILED))
      {
         afterBuildFinished(buildStatus);
         return;
      }
   }

   /**
    * Perform actions after build is finished.
    * 
    * @param buildStatus status of build
    */
   private void afterBuildFinished(BuildStatus buildStatus)
   {
      buildInProgress = false;
      previousStatus = buildStatus.getStatus();

      StringBuilder message =
         new StringBuilder("Building project <b>").append(project.getPath().substring(1))
            .append("</b> has been finished.\r\nResult: ").append(buildStatus.getStatus());

      if (buildStatus.getStatus() == Status.SUCCESSFUL)
      {
         IDE.fireEvent(new OutputEvent(BUILD_SUCCESS, Type.INFO));

         statusHandler.requestFinished(projectId);

         message.append("\r\nYou can download result of build by <a href=\"").append(buildStatus.getDownloadUrl())
            .append("\">this link</a>");
      }
      else if (buildStatus.getStatus() == Status.FAILED)
      {
         IDE.fireEvent(new OutputEvent(BUILD_FAILED, Type.ERROR));

         String errorMessage = buildStatus.getError();
         String exceptionMessage = "Building of project failed";
         if (errorMessage != null && !errorMessage.equals("null"))
         {
            message.append("\r\n" + errorMessage);
            exceptionMessage += ": " + errorMessage;
         }

         statusHandler.requestError(projectId, new Exception(exceptionMessage));
      }

      showBuildMessage(message.toString());
      display.stopAnimation();

      IDE.fireEvent(new ProjectBuiltEvent(buildStatus));
   }

   /**
    * Output the message and activate view if necessary.
    * 
    * @param message message for output
    */
   private void showBuildMessage(String message)
   {
      if (display != null)
      {
         if (closed)
         {
            IDE.getInstance().openView(display.asView());
            closed = false;
         }
         else
         {
            display.asView().activate();
         }
      }
      else
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         closed = false;
      }

      display.output(message);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         closed = true;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   protected boolean makeSelectionCheck()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedItemsFail());
         return false;
      }

      if (!(selectedItems.get(0) instanceof ItemContext) || ((ItemContext)selectedItems.get(0)).getProject() == null)
      {
         Dialogs.getInstance().showInfo("Project is not selected.");
         return false;
      }

      if (selectedItems.get(0).getPath().isEmpty() || selectedItems.get(0).getPath().equals("/"))
      {
         Dialogs.getInstance().showInfo(GitExtension.MESSAGES.selectedWorkace());
         return false;
      }

      return true;
   }

   /**
    * Deserializer for response's body.
    */
   private class StringUnmarshaller implements Unmarshallable<StringBuilder>
   {

      protected StringBuilder builder;

      /**
       * @param callback
       */
      public StringUnmarshaller(StringBuilder builder)
      {
         this.builder = builder;
      }

      /**
       * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
       */
      @Override
      public void unmarshal(Response response)
      {
         builder.append(response.getText());
      }

      @Override
      public StringBuilder getPayload()
      {
         return builder;
      }
   }
}
