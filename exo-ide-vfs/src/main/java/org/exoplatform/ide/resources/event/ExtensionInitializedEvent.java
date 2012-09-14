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
package org.exoplatform.ide.resources.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event notifying that service have started successfully. Used to determine the moment when all services
 * started and UI can be launched.
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ExtensionInitializedEvent extends GwtEvent<ExtensionInitializedHandler>
{
   public static Type<ExtensionInitializedHandler> TYPE = new Type<ExtensionInitializedHandler>();

   private StartableExtension extemsion;

   /**
    * @param extension
    */
   public ExtensionInitializedEvent(StartableExtension extension)
   {
      this.extemsion = extension;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Type<ExtensionInitializedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return Extension firing the event 
    */
   public StartableExtension getExtension()
   {
      return extemsion;
   }

   @Override
   protected void dispatch(ExtensionInitializedHandler handler)
   {
      handler.onExtensionInitialized(this);
   }
}
