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
package org.exoplatform.ide.client.model.template;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class TemplateService
{
   private static TemplateService instance;

   public static TemplateService getInstance()
   {
      return instance;
   }

   protected TemplateService()
   {
      instance = this;
   }

   /**
    * @param callback
    */
   public abstract void getTemplates(AsyncRequestCallback<TemplateList> callback);

   /**
    * @param template
    * @param callback
    */
   public abstract void createTemplate(Template template, TemplateCreatedCallback callback);

   /**
    * @param template
    * @param callback
    */
   public abstract void deleteTemplate(Template template, TemplateDeletedCallback callback);

   public abstract void getTemplateList(String type, AsyncRequestCallback<List<TemplateNative>> callback);
   
   public abstract void createProject(String templateName, String location, AsyncRequestCallback<String> callback);
   
   public abstract void getFileContent(String templateName, AsyncRequestCallback<String> callback);

}
