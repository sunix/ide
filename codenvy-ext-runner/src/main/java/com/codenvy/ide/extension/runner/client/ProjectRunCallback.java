/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.resources.model.Project;

/**
 * Notified when app will be launched.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectRunCallback {
    /**
     * Notified when app will be launched.
     *
     * @param appDescriptor
     *         descriptor of application that was run
     * @param project
     *         project that was run
     */
    void onRun(ApplicationProcessDescriptor appDescriptor, Project project);
}
