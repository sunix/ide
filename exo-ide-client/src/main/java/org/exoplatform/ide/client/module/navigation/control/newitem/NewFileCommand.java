/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.module.navigation.control.newitem;

import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointEvent;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointHandler;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NewFileCommand extends IDEControl implements PanelSelectedHandler, SwitchEntryPointHandler,
   EntryPointChangedHandler
{

   private boolean browserSelected = false;

   private String entryPoint;

   public NewFileCommand(String id, HandlerManager eventBus, String title, String prompt, String icon, GwtEvent<?> event)
   {
      super(id, eventBus);
      setTitle(title);
      setPrompt(prompt);
      setIcon(icon);
      setEvent(event);
   }

   public NewFileCommand(String id, HandlerManager eventBus, String title, String prompt, ImageResource normalIcon,
      ImageResource disabledIcon, GwtEvent<?> event)
   {
      super(id, eventBus);
      setTitle(title);
      setPrompt(prompt);
      setImages(normalIcon, disabledIcon);
      setEvent(event);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(PanelSelectedEvent.TYPE, this);
      addHandler(SwitchEntryPointEvent.TYPE, this);
      addHandler(EntryPointChangedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (entryPoint == null)
      {
         setEnabled(false);
         return;
      }

      if (browserSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }

   public void onSwitchEntryPoint(SwitchEntryPointEvent event)
   {
      updateEnabling();
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

}
