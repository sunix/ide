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
package org.exoplatform.ide.jrebel.server;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: JRebelProfiler.java 34027 19.12.12 17:03Z vzhukovskii $
 */
public class JRebelProfiler
{
   private Log logger = ExoLogger.getLogger(JRebelProfiler.class);

   public JRebelProfiler()
   {
   }

   public void getProfileInfo(String userId, String firstName, String lastName, String phone)
      throws JRebelProfilerException
   {
      logger.debug("Info for jRebel: " + userId + ":" + firstName + ":" + lastName + ":" + phone);
   }
}