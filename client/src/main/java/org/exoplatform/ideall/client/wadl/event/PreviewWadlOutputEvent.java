/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.wadl.event;

import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.GwtEvent;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PreviewWadlOutputEvent extends GwtEvent<PreviewWadlOutputHandler>
{
   public static final GwtEvent.Type<PreviewWadlOutputHandler> TYPE = new GwtEvent.Type<PreviewWadlOutputHandler>();
   
   //private String path;
   
//   public PreviewWadlOutputEvent(String path)
//   {
//      this.path = path;
//   }
//   
//   public String getPath()
//   {
//      return path;
//   }
   
   @Override
   protected void dispatch(PreviewWadlOutputHandler hendler)
   {
      hendler.onPreviewWadlOutput(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<PreviewWadlOutputHandler> getAssociatedType()
   {
      return TYPE;
   }

}
