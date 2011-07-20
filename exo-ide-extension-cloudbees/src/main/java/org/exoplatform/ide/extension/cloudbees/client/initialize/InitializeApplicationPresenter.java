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
package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.java.client.JavaClientService;
//import org.exoplatform.ide.extension.java.client.create.CleanProjectEvent;
import org.exoplatform.ide.extension.java.shared.MavenResponse;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationPresenter.java Jun 23, 2011 12:49:09 PM vereshchaka $
 *
 */
public class InitializeApplicationPresenter implements ViewClosedHandler, InitializeApplicationHandler, ItemsSelectedHandler
{

   interface Display extends IsView
   {
      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getDomainField();

      HasValue<String> getNameField();

      HasValue<String> getApplicationIdField();

      void enableCreateButton(boolean enable);

      void focusInApplicationNameField();

      void setDomainValues(String[] domains);

   }

   private String[] domains;

   private Display display;

   private String warUrl;

   private String workDir;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;

   public InitializeApplicationPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(InitializeApplicationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            packageApplication();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getDomainField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            setApplicationId();
         }
      });

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() == null || event.getValue().isEmpty())
            {
               display.enableCreateButton(false);
            }
            else
            {
               display.enableCreateButton(true);
            }
            setApplicationId();
         }
      });

      display.setDomainValues(domains);
      display.enableCreateButton(false);
      setApplicationId();
   }
   
   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationHandler#onInitializeApplication(org.exoplatform.ide.extension.cloudbees.client.initialize.InitializeApplicationEvent)
    */
   @Override
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      getDomains();
   }
   
   private void getDomains()
   {
      CloudBeesClientService.getInstance().getDomains(
         new CloudBeesAsyncRequestCallback<List<String>>(eventBus, new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               getDomains();
            }
         }, null)
         {
            @Override
            protected void onSuccess(List<String> result)
            {
               showView(result);
            }
         });
   }

   private void packageApplication()
   {
      workDir = getWorkDir();
      
      JavaClientService.getInstance().packageProject(workDir, new AsyncRequestCallback<MavenResponse>()
      {
         @Override
         protected void onSuccess(MavenResponse result)
         {
            String output = result.getOutput().replace("\n", "<br>");
            eventBus.fireEvent(new OutputEvent(output, Type.INFO));
            warUrl = (String)result.getResult().get("war");
            doDeployApplication();
         }
      });
   }
   
   private LoggedInHandler deployWarLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         doDeployApplication();
      }
   };
   
   private LoginCanceledHandler deployWarLoginCanceledHandler = new LoginCanceledHandler()
   {
      @Override
      public void onLoginCanceled()
      {
         //if user clicks Cancel, than clean project
         JavaClientService.getInstance().cleanProject(workDir, new AsyncRequestCallback<MavenResponse>()
            {
               @Override
               protected void onSuccess(MavenResponse result)
               {
                  eventBus.fireEvent(new RefreshBrowserEvent());
                  String output = result.getOutput().replace("\n", "<br>");
                  eventBus.fireEvent(new OutputEvent(output, Type.INFO));
               }
            });
      }
   };

   private void doDeployApplication()
   {
      final String applicationId = display.getApplicationIdField().getValue();

      CloudBeesClientService.getInstance().deployWar(applicationId, warUrl, null,
         new CloudBeesAsyncRequestCallback<Map<String, String>>(eventBus, deployWarLoggedInHandler,
            deployWarLoginCanceledHandler)
         {
            @Override
            protected void onSuccess(final Map<String, String> deployResult)
            {
               JavaClientService.getInstance().cleanProject(workDir, new AsyncRequestCallback<MavenResponse>()
               {
                  @Override
                  protected void onSuccess(MavenResponse result)
                  {
                     eventBus.fireEvent(new RefreshBrowserEvent());
                     String output = result.getOutput().replace("\n", "<br>");
                     eventBus.fireEvent(new OutputEvent(output, Type.INFO));

                     output = CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationSuccess() + "<br>";
                     output += CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationInfo() + "<br>";

                     Iterator<Entry<String, String>> it = deployResult.entrySet().iterator();
                     while (it.hasNext())
                     {
                        Entry<String, String> entry = (Entry<String, String>)it.next();
                        output += entry.getKey() + " : " + entry.getValue() + "<br>";
                     }
                     eventBus.fireEvent(new OutputEvent(output, Type.INFO));
                     closeView();
                  }
               });

            }

            @Override
            protected void onFailure(final Throwable exception)
            {
               JavaClientService.getInstance().cleanProject(workDir, new AsyncRequestCallback<MavenResponse>()
               {
                  @Override
                  protected void onSuccess(MavenResponse result)
                  {
                     eventBus.fireEvent(new RefreshBrowserEvent());
                     String output = result.getOutput().replace("\n", "<br>");
                     eventBus.fireEvent(new OutputEvent(output, Type.INFO));
                     eventBus.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                        .deployApplicationFailureMessage(), Type.INFO));
                     super.onFailure(exception);
                  }
               });
            }
         });
   }

   /**
    * Set the application id, which has the next form:
    * "domain/aplicationName".
    */
   private void setApplicationId()
   {
      final String domain = display.getDomainField().getValue();
      String name = display.getNameField().getValue();
      if (name == null)
         name = "";
      display.getApplicationIdField().setValue(domain + "/" + name);
   }

   /**
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

   private void showView(List<String> domains)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         this.domains = new String[domains.size()];
         this.domains = domains.toArray(this.domains);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.focusInApplicationNameField();
      }
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   private String getWorkDir()
   {
      Item item = selectedItems.get(0);
      String workDir = item.getHref();
      if (item instanceof File)
      {
         workDir = workDir.substring(0, workDir.lastIndexOf("/") + 1);
      }
      return workDir;
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

}
