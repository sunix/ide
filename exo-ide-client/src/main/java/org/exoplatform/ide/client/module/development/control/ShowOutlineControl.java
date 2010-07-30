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
package org.exoplatform.ide.client.module.development.control;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.cookie.event.BrowserCookiesUpdatedEvent;
import org.exoplatform.ide.client.cookie.event.BrowserCookiesUpdatedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.form.FormClosedEvent;
import org.exoplatform.ide.client.framework.form.FormClosedHandler;
import org.exoplatform.ide.client.framework.form.FormOpenedEvent;
import org.exoplatform.ide.client.framework.form.FormOpenedHandler;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.outline.CodeHelperForm;
import org.exoplatform.ide.client.outline.OutlineTreeGrid;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Cookies;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class ShowOutlineControl extends IDEControl implements EditorActiveFileChangedHandler,
   BrowserCookiesUpdatedHandler, FormOpenedHandler, FormClosedHandler
{

   public static final String ID = "View/Show \\ Hide Outline";

   public static final String TITLE = "Outline";

   public static final String PROMPT_SHOW = "Show Outline";

   public static final String PROMPT_HIDE = "Hide Outline";

   private static final String COOKIE_OUTLINE = "outline";

   private boolean showOutLine = "true".equals(Cookies.getCookie(COOKIE_OUTLINE));
   
   private boolean outLineFormOpened = false;

   public ShowOutlineControl(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setImages(IDEImageBundle.INSTANCE.outline(), IDEImageBundle.INSTANCE.outlineDisabled());
      setEvent(new ShowOutlineEvent(true));
      setEnabled(true);
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(BrowserCookiesUpdatedEvent.TYPE, this);
      addHandler(FormOpenedEvent.TYPE, this);
      addHandler(FormClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         return;
      }

      boolean visible = OutlineTreeGrid.haveOutline(event.getFile());
      setVisible(visible);
   }

   /**
    * Update the control state - change prompt and event parameter.
    */
   private void update()
   {
      if (showOutLine)
      {
         setPrompt(PROMPT_HIDE);
         setEvent(new ShowOutlineEvent(false));
      }
      else
      {
         setPrompt(PROMPT_SHOW);
         setEvent(new ShowOutlineEvent(true));
      }
   }

   /**
    * @see org.exoplatform.ide.client.cookie.event.BrowserCookiesUpdatedHandler#onBrowserCookiesUpdated(org.exoplatform.ide.client.cookie.event.BrowserCookiesUpdatedEvent)
    */
   public void onBrowserCookiesUpdated(BrowserCookiesUpdatedEvent event)
   {
      update();
   }
   
   private void updateControlEnabling() {
      setSelected(outLineFormOpened);

      if (outLineFormOpened)
      {
         setEvent(new ShowOutlineEvent(false));
      }
      else
      {
         setEvent(new ShowOutlineEvent(true));
      }               
   }

   public void onFormOpened(FormOpenedEvent event)
   {
      if (CodeHelperForm.ID.equals(event.getFormId())) {
         outLineFormOpened = true;
         updateControlEnabling();
      }
   }

   public void onFormClosed(FormClosedEvent event)
   {
      if (CodeHelperForm.ID.equals(event.getFormId())) {
         outLineFormOpened = false;
         updateControlEnabling();
      }
   }

}
