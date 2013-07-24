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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.google.gwt.user.client.ui.ListBox;

/**
 * Wrapper on ListBox with field that marker input modifiable.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ModifiableListBox extends ListBox {
    private boolean modified;

    /**
     * If input field is modified by user.
     *
     * @return true if modified.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Set modified value.
     *
     * @param modified
     *         true if field is modified.
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}