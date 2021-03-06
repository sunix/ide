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
package com.codenvy.ide.api.notification;

import com.codenvy.ide.api.ui.workspace.PartPresenter;

import javax.validation.constraints.NotNull;

/**
 * The manager for notifications. Used to show notifications and change their states.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface NotificationManager  extends PartPresenter{
    /**
     * Show notification.
     *
     * @param notification
     *         notification that need to show
     */
    void showNotification(@NotNull Notification notification);
}