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
package org.exoplatform.ideall.client.model.settings;

import org.exoplatform.ideall.client.model.ApplicationContext;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class SettingsService
{

   private static SettingsService instance;

   public static SettingsService getInstance()
   {
      return instance;
   }

   protected SettingsService()
   {
      instance = this;
   }
   
   public abstract void getSettings(ApplicationContext context);

   public abstract void saveSetting(ApplicationContext context);
   
   protected abstract void getSettings(ApplicationContext context, String url);
   
   protected abstract void saveSettings(ApplicationContext context, String url);
   
}
