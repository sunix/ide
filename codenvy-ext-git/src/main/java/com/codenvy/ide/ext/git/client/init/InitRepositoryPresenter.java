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
package com.codenvy.ide.ext.git.client.init;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for Git command Init Repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 9:07:58 AM anya $
 */
@Singleton
public class InitRepositoryPresenter implements InitRepositoryView.ActionDelegate {
    private InitRepositoryView view;
    private GitServiceClient        service;
    private Project                 project;
    private ResourceProvider        resourceProvider;
    private EventBus                eventBus;
    private GitLocalizationConstant constant;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param eventBus
     * @param constant
     * @param notificationManager
     */
    @Inject
    public InitRepositoryPresenter(InitRepositoryView view,
                                   GitServiceClient service,
                                   ResourceProvider resourceProvider,
                                   EventBus eventBus,
                                   GitLocalizationConstant constant,
                                   NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();

        view.setWorkDir(project.getPath());
        view.setBare(false);
        view.setEnableOkButton(true);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        boolean bare = view.isBare();
        String projectName = project.getName();
        String projectId = project.getId();
        view.close();

        try {
            service.init(projectId, projectName, bare, new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    Notification notification = new Notification(constant.initSuccess(), INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (WebSocketException e) {
            handleError(e);
        }
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(@NotNull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.initFailed();
        Notification notification = new Notification(errorMessage, ERROR);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String workDir = view.getWorkDir();
        view.setEnableOkButton(!workDir.isEmpty());
    }
}