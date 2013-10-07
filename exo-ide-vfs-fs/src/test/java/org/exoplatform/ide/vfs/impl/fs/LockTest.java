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
package org.exoplatform.ide.vfs.impl.fs;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

public class LockTest extends LocalFileSystemTest {
    private final String lockToken = "01234567890abcdef";

    private String folderId;
    private String folderPath;

    private String fileId;
    private String filePath;

    private String protectedFilePath;
    private String protectedFileId;

    private String lockedFileId;
    private String lockedFilePath;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        filePath = createFile(testRootPath, "LockTest_File", DEFAULT_CONTENT_BYTES);
        folderPath = createDirectory(testRootPath, "LockTest_Folder");
        lockedFilePath = createFile(testRootPath, "LockTest_LockedFile", DEFAULT_CONTENT_BYTES);
        protectedFilePath = createFile(testRootPath, "LockTes_ProtectedFile", DEFAULT_CONTENT_BYTES);

        createLock(lockedFilePath, lockToken);

        Map<Principal, Set<BasicPermissions>> permissions = new HashMap<Principal, Set<BasicPermissions>>(2);
        permissions.put(new PrincipalImpl("andrew", Principal.Type.USER), EnumSet.of(BasicPermissions.ALL));
        permissions.put(new PrincipalImpl("admin", Principal.Type.USER), EnumSet.of(BasicPermissions.READ));
        writePermissions(protectedFilePath, permissions);

        fileId = pathToId(filePath);
        folderId = pathToId(folderPath);
        lockedFileId = pathToId(lockedFilePath);
        protectedFileId = pathToId(protectedFilePath);
    }

    public void testLockFile() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + fileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        LockToken result = (LockToken)response.getEntity();
        assertEquals("Lock file not found or lock token invalid. ", result.getLockToken(), readLock(filePath));
        assertTrue("File must be locked. ", ((File)getItem(fileId)).isLocked());
    }

    public void testLockFileAlreadyLocked() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        // lock file must not be updated.
        assertEquals("Lock file not found or lock token invalid. ", lockToken, readLock(lockedFilePath));
    }

    public void testLockFileNoPermissions() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + protectedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(403, response.getStatus());
        assertNull("Lock file must not be created. ", readLock(protectedFilePath));
        assertFalse("File must not be locked. ", ((File)getItem(protectedFileId)).isLocked());
    }

    public void testLockFolder() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "lock/" + folderId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(400, response.getStatus());
        // Lock file must not be created
        assertNull(readLock(folderPath));
    }

    public void testUnlockFile() throws Exception {
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + lockToken;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, null);
        assertEquals(204, response.getStatus());
        assertNull("Lock must be removed. ", readLock(lockedFilePath));
        assertFalse("File must be unlocked. ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileNoLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId;
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertEquals("Lock must be kept. ", lockToken, readLock(lockedFilePath));
        assertTrue("Lock must be kept.  ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileWrongLockToken() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + lockedFileId + '?' + "lockToken=" + lockToken + "_WRONG";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertEquals("Lock must be kept. ", lockToken, readLock(lockedFilePath));
        assertTrue("Lock must be kept.  ", ((File)getItem(lockedFileId)).isLocked());
    }

    public void testUnlockFileNotLocked() throws Exception {
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        String requestPath = SERVICE_URI + "unlock/" + fileId + '?' + "lockToken=some_token";
        ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
        log.info(new String(writer.getBody()));
        assertEquals(423, response.getStatus());
        assertFalse(((File)getItem(fileId)).isLocked());
    }
}