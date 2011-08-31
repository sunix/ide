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
package org.exoplatform.ide.client.navigation.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedEvent;
import org.exoplatform.ide.client.navigation.event.ItemsToPasteSelectedHandler;
import org.exoplatform.ide.client.navigation.event.PasteItemsCompleteEvent;
import org.exoplatform.ide.client.navigation.event.PasteItemsCompleteHandler;
import org.exoplatform.ide.client.navigation.event.PasteItemsEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators", "developers"})
public class PasteItemsCommand extends MultipleSelectionItemsCommand implements ItemsToPasteSelectedHandler,
   PasteItemsCompleteHandler, ItemsSelectedHandler
{
   public static final String ID = "Edit/Paste Item(s)";

   private final static String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsTitleControl();

   private final static String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.pasteItemsPromptControl();

   private boolean pastePrepared = false;

   public PasteItemsCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.paste(), IDEImageBundle.INSTANCE.pasteDisabled());
      setEvent(new PasteItemsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.navigation.control.MultipleSelectionItemsCommand#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ItemsToPasteSelectedEvent.TYPE, this);
      eventBus.addHandler(PasteItemsCompleteEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      super.initialize(eventBus);
   }

   public void onItemsToPasteSelected(ItemsToPasteSelectedEvent event)
   {
      pastePrepared = true;
      setEnabled(true);
   }

   public void onPasteItemsComlete(PasteItemsCompleteEvent event)
   {
      setEnabled(false);
      pastePrepared = false;
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1)
      {

         updateEnabling();
      }
      else
      {
         setEnabled(false);
      }

   }

   @Override
   protected void updateEnabling()
   {
      if (browserSelected)
      {
         if (pastePrepared)
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

}
