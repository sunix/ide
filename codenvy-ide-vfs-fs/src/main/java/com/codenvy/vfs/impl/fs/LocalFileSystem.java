/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFileSystemImpl;
import com.codenvy.api.vfs.server.VirtualFileSystemUserContext;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.observation.EventListenerList;
import com.codenvy.api.vfs.server.search.SearcherProvider;
import com.codenvy.api.vfs.server.util.LinksHelper;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.ACLCapability;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.BasicPermissions;
import com.codenvy.api.vfs.shared.dto.VirtualFileSystemInfo.QueryCapability;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.dto.server.DtoFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of VirtualFileSystem for local filesystem.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class LocalFileSystem extends VirtualFileSystemImpl {
    final String            vfsId;
    final URI               baseUri;
    final EventListenerList listeners;
    final FSMountPoint      mountPoint;
    final SearcherProvider  searcherProvider;

    public LocalFileSystem(String vfsId,
                           URI baseUri,
                           EventListenerList listeners,
                           VirtualFileSystemUserContext userContext,
                           FSMountPoint mountPoint,
                           SearcherProvider searcherProvider) {
        super(vfsId, baseUri, listeners, userContext, mountPoint, searcherProvider);
        this.vfsId = vfsId;
        this.baseUri = baseUri;
        this.listeners = listeners;
        this.mountPoint = mountPoint;
        this.searcherProvider = searcherProvider;
    }

    @Override
    public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException {
        final VirtualFileSystemInfo vfsInfo = DtoFactory.getInstance().createDto(VirtualFileSystemInfo.class);
        final BasicPermissions[] basicPermissions = BasicPermissions.values();
        final List<String> permissions = new ArrayList<>(basicPermissions.length);
        for (BasicPermissions bp : basicPermissions) {
            permissions.add(bp.value());
        }
        vfsInfo.setId(vfsId);
        vfsInfo.setVersioningSupported(false);
        vfsInfo.setLockSupported(true);
        vfsInfo.setAnonymousPrincipal(VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL);
        vfsInfo.setAnyPrincipal(VirtualFileSystemInfo.ANY_PRINCIPAL);
        vfsInfo.setPermissions(permissions);
        vfsInfo.setAclCapability(ACLCapability.MANAGE);
        vfsInfo.setQueryCapability(searcherProvider == null ? QueryCapability.NONE : QueryCapability.FULLTEXT);
        vfsInfo.setUrlTemplates(LinksHelper.createUrlTemplates(baseUri, (String)EnvironmentContext.getCurrent().getVariable(
                EnvironmentContext.WORKSPACE_NAME)));
        final Folder root = (Folder)fromVirtualFile(getMountPoint().getRoot(), true, PropertyFilter.ALL_FILTER);
        vfsInfo.setRoot(root);
        return vfsInfo;
    }
}