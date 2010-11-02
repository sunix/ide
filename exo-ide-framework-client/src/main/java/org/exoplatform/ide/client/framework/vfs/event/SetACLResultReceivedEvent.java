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
package org.exoplatform.ide.client.framework.vfs.event;

import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 *This event fires if ACL successful set.<br>
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 21, 2010 $
 *
 */
public class SetACLResultReceivedEvent extends GwtEvent<SetACLResultReceivedHandler>
{

   public static GwtEvent.Type<SetACLResultReceivedHandler> TYPE = new Type<SetACLResultReceivedHandler>();

   private Item item;

   /**
    * @param item
    */
   public SetACLResultReceivedEvent(Item item)
   {
      this.item = item;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<SetACLResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(SetACLResultReceivedHandler handler)
   {
      handler.onSetACLResultReceived(this);
   }

   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }

}
