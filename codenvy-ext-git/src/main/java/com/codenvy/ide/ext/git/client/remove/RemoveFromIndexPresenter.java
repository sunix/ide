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
package com.codenvy.ide.ext.git.client.remove;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for removing files from index and file system.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 4:35:16 PM anya $
 */
public class RemoveFromIndexPresenter implements RemoveFromIndexView.ActionDelegate {
    private RemoveFromIndexView     view;
    private GitServiceClient        service;
    private GitLocalizationConstant constant;
    private ResourceProvider        resourceProvider;
    private Project                 project;
    private SelectionAgent          selectionAgent;
    private NotificationManager     notificationManager;

    /**
     * Create presenter
     *
     * @param view
     * @param service
     * @param constant
     * @param resourceProvider
     * @param notificationManager
     */
    @Inject
    public RemoveFromIndexPresenter(RemoveFromIndexView view, GitServiceClient service, GitLocalizationConstant constant,
                                    ResourceProvider resourceProvider, SelectionAgent selectionAgent,
                                    NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.selectionAgent = selectionAgent;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        String workDir = project.getPath();
        view.setMessage(formMessage(workDir));
        view.setRemoved(false);
        view.showDialog();
    }

    /**
     * Form the message to display for removing from index, telling the user what is gonna to be removed.
     *
     * @return {@link String} message to display
     */
    @NotNull
    private String formMessage(@NotNull String workdir) {
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();

        Resource element;
        if (selection == null) {
            element = project;
        } else {
            element = selection.getFirstElement();
        }

        String pattern = element.getPath().replaceFirst(workdir, "");
        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;

        // Root of the working tree:
        if (pattern.length() == 0 || "/".equals(pattern)) {
            return constant.removeFromIndexAll();
        }

        if (element instanceof Folder) {
            return constant.removeFromIndexFolder(pattern);
        } else {
            return constant.removeFromIndexFile(pattern);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveClicked() {
        service.remove(project.getId(), getFilePatterns(), view.isRemoved(),
                       new AsyncRequestCallback<String>() {
                           @Override
                           protected void onSuccess(String result) {
                               Notification notification = new Notification(constant.removeFilesSuccessfull(), INFO);
                               notificationManager.showNotification(notification);
                           }

                           @Override
                           protected void onFailure(Throwable exception) {
                               handleError(exception);
                           }
                       });
        view.close();
    }


    /**
     * Returns pattern of the files to be removed.
     *
     * @return pattern of the files to be removed
     */
    @NotNull
    private List<String> getFilePatterns() {
        String projectPath = project.getPath();

        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        Resource element;
        if (selection == null) {
            element = project;
        } else {
            element = selection.getFirstElement();
        }

        String pattern = element.getPath().replaceFirst(projectPath, "");
        pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;

        return (pattern.length() == 0 || "/".equals(pattern)) ? new ArrayList<String>(Arrays.asList(".")) : new ArrayList<String>(Arrays.asList(pattern));
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param e
     *         exception what happened
     */
    private void handleError(@NotNull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : constant.removeFilesFailed();
        Notification notification = new Notification(errorMessage, ERROR);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}