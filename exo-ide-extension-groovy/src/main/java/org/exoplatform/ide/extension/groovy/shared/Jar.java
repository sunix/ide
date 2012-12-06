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
package org.exoplatform.ide.extension.groovy.shared;

import java.util.List;

/**
 * Interface describes the JAR file.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Jar.java Mar 22, 2012 10:31:44 AM azatsarynnyy $
 *
 */
public interface Jar
{

   /**
    * Returns path to the JAR file.
    * 
    * @return path to the JAR file
    */
   public String getPath();

   /**
    * Sets new path of this JAR file.
    * 
    * @param path new path of this JAR file
    */
   public void setPath(String path);

   /**
    * Gets the list of attributes.
    * 
    * @return list of attributes
    */
   public List<Attribute> getAttributes();

   /**
    * Sets new list of attributes.
    * 
    * @param attributes list of attributes
    */
   public void setAttributes(Attribute attributes);
}