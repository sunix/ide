/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.shared;

import com.codenvy.ide.dtogen.shared.ClientToServerDto;
import com.codenvy.ide.dtogen.shared.RoutingType;
import com.codenvy.ide.json.JsonStringMap;

/**
 * Represent information about attributes what need to update.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RoutingType(type = RoutingTypes.UPDATE_USER_ATTRIBUTES)
public interface UpdateUserAttributes extends ClientToServerDto {

    /**
     * Returns user's id.
     *
     * @return user's id
     */
    String getUserId();

    /**
     * Returns user's attributes with new value what need to update.
     *
     * @return user's attributes
     */
    JsonStringMap<String> getAttributes();
}