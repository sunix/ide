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
package org.exoplatform.ide.client.framework.form;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class FormDeselectedEvent extends GwtEvent<FormDeselectedHandler>
{

   public static final GwtEvent.Type<FormDeselectedHandler> TYPE = new GwtEvent.Type<FormDeselectedHandler>();

   private String formId;

   public FormDeselectedEvent(String formId)
   {
      this.formId = formId;
   }

   public String getFormId()
   {
      return formId;
   }

   @Override
   protected void dispatch(FormDeselectedHandler handler)
   {
      handler.onFormDeselected(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FormDeselectedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
