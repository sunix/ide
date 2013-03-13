/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.extension;

import com.codenvy.ide.json.JsonStringMap;

/**
 * Provides information about Extensions, their description, version and the list of dependencies.
 * Currently for information purposes only
 * TODO: connect with ExtensionInitializer or ExtensionManager
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface ExtensionRegistry
{

   /**
    * Returns the map of Extension ID to {@link ExtensionDescription}.
    * 
    * @return
    */
   public JsonStringMap<ExtensionDescription> getExtensionDescriptions();

}