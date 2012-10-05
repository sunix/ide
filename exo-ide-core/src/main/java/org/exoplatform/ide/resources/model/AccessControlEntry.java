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
package org.exoplatform.ide.resources.model;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;

/**
 * Representation of Access Control Entry used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AccessControlEntry.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class AccessControlEntry
{
   /** Principal name. */
   private String principal;

   /** Permissions. */
   private JsonArray<String> permissions;

   /** Empty AccessControlEntry instance. Both principal and permissions are not set. */
   public AccessControlEntry()
   {
   }

   /**
    * AccessControlEntry instance with specified principal and permissions.
    *
    * @param principal principal
    * @param permissions permissions
    */
   public AccessControlEntry(String principal, JsonArray<String> permissions)
   {
      this.principal = principal;
      this.permissions = permissions;
   }

   /** @return principal's permissions */
   public JsonArray<String> getPermissions()
   {
      if (permissions == null)
      {
         permissions = JsonCollections.<String>createArray();
      }
      return permissions;
   }

   /** @param permissions new set of permissions */
   public void setPermissions(JsonArray<String> permissions)
   {
      this.permissions = permissions;
   }

   /** @return principal name */
   public String getPrincipal()
   {
      return principal;
   }

   /** @param principal principal name */
   public void setPrincipal(String principal)
   {
      this.principal = principal;
   }

   /** @see java.lang.Object#toString() */
   @Override
   public String toString()
   {
      return "AccessControlEntry [principal=" + principal + ", permissions=" + permissions + ']';
   }
}