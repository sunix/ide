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
package org.exoplatform.ide.extension.maven.server;

/**
 * Thrown if build request was rejected by remote build server.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@SuppressWarnings("serial")
public class BuilderException extends Exception
{
   /** HTTP status of response from builder. */
   private final int responseStatus;

   /** Content type of response from builder. */
   private final String contentType;

   /**
    * @param responseStatus HTTP status of response from builder
    * @param message text message
    * @param contentType content type of response from builder
    */
   public BuilderException(int responseStatus, String message, String contentType)
   {
      super(message);
      this.responseStatus = responseStatus;
      this.contentType = contentType;
   }

   public int getResponseStatus()
   {
      return responseStatus;
   }

   public String getContentType()
   {
      return contentType;
   }
}
