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
package org.exoplatform.ide.client.project.explorer.ui;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FileTreeItem extends ProjectExplorerTreeItem {

    public FileTreeItem(FileModel file) {
        super(file);
    }

    @Override
    public List<Item> getItems() {
        return null;
    }

    @Override
    public void refresh(boolean expand) {
        render();
    }
    
    @Override
    public boolean select(Item item) {
        if (item.getId().equals(((Item)getUserObject()).getId()))
        {
            getTree().setSelectedItem(this);
            getTree().ensureSelectedItemVisible();
            return true;
        }

        return false;
    }

}