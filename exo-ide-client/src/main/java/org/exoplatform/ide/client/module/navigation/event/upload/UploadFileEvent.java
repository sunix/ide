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
package org.exoplatform.ide.client.module.navigation.event.upload;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UploadFileEvent extends GwtEvent<UploadFileHandler>
{

   public static final GwtEvent.Type<UploadFileHandler> TYPE = new GwtEvent.Type<UploadFileHandler>();

   private boolean openFile;

   public UploadFileEvent(boolean openFile)
   {
      this.openFile = openFile;
   }

   public boolean isOpenFile()
   {
      return openFile;
   }

   @Override
   protected void dispatch(UploadFileHandler handler)
   {
      handler.onUploadFile(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<UploadFileHandler> getAssociatedType()
   {
      return TYPE;
   }

}
