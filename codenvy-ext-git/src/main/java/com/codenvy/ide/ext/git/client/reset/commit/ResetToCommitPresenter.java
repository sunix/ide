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
package com.codenvy.ide.ext.git.client.reset.commit;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.git.shared.DiffRequest.DiffType.RAW;

/**
 * Presenter for resetting head to commit.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class ResetToCommitPresenter implements ResetToCommitView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       ResetToCommitView       view;
    private       GitServiceClient        service;
    private       Revision                selectedRevision;
    private       ResourceProvider        resourceProvider;
    private       GitLocalizationConstant constant;
    private       NotificationManager     notificationManager;
    private       String                  projectId;
    private       EditorAgent             editorAgent;
    private       EventBus                eventBus;
    private       List<EditorPartPresenter> openedEditors;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param notificationManager
     */
    @Inject
    public ResetToCommitPresenter(ResetToCommitView view, GitServiceClient service, ResourceProvider resourceProvider,
                                  GitLocalizationConstant constant, NotificationManager notificationManager,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory, EditorAgent editorAgent,
                                  EventBus eventBus) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.editorAgent = editorAgent;
        this.eventBus = eventBus;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();

        service.log(projectId, false,
                    new AsyncRequestCallback<LogResponse>(dtoUnmarshallerFactory.newUnmarshaller(LogResponse.class)) {
                        @Override
                        protected void onSuccess(LogResponse result) {
                            selectedRevision = null;
                            view.setRevisions(result.getCommits());
                            view.setMixMode(true);
                            view.setEnableResetButton(false);
                            view.showDialog();
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.logFailed();
                            Notification notification = new Notification(errorMessage, ERROR);
                            notificationManager.showNotification(notification);
                        }
                    }
                   );
    }

    /** {@inheritDoc} */
    @Override
    public void onResetClicked() {
        view.close();

        openedEditors = new ArrayList<>();
        final List<String> listOpenedFiles = new ArrayList<>();

        for (EditorPartPresenter partPresenter : editorAgent.getOpenedEditors().getValues().asIterable()) {
            openedEditors.add(partPresenter);
            listOpenedFiles.add(partPresenter.getEditorInput().getFile().getRelativePath());
        }

        getDiff(listOpenedFiles, selectedRevision.getId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResetToCommitPresenter.class, "can not get diff for commit " + selectedRevision.getId());
            }

            @Override
            public void onSuccess(String diff) {
                reset(diff);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRevisionSelected(@NotNull Revision revision) {
        selectedRevision = revision;
        view.setEnableResetButton(true);
    }

    private void getDiff(List<String> listFiles, final String commit, final AsyncCallback<String> callback) {
        String projectId = resourceProvider.getActiveProject().getId();
        service.diff(projectId, listFiles, RAW, true, 10, commit, false, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String diff) {
                callback.onSuccess(diff);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void reset(final String diff){
        ResetRequest.ResetType type = view.isMixMode() ? ResetRequest.ResetType.MIXED : null;
        type = (type == null && view.isSoftMode()) ? ResetRequest.ResetType.SOFT : type;
        type = (type == null && view.isHardMode()) ? ResetRequest.ResetType.HARD : type;
        type = (type == null && view.isKeepMode()) ? ResetRequest.ResetType.KEEP : type;
        type = (type == null && view.isMergeMode()) ? ResetRequest.ResetType.MERGE : type;

        service.reset(projectId, selectedRevision.getId(), type, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                refreshProject(diff);
            }

            @Override
            protected void onFailure(Throwable exception) {
                String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.resetFail();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    private void refreshProject(final String diff){
        resourceProvider.getActiveProject().refreshChildren(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                for (EditorPartPresenter partPresenter : openedEditors) {
                    final File file = partPresenter.getEditorInput().getFile();
                    String filePath = file.getRelativePath();

                    if (diff.contains(filePath)) {
                        int firstIndex = diff.indexOf(filePath);
                        int lastIndex = diff.lastIndexOf(filePath);
                        String between = diff.substring(firstIndex, lastIndex);

                        if (between.contains("new file mode")) {
                            //<code>diff</code> contains the string "new file mode" in the case if working tree has file
                            // that is not exist in the commit to reset. So this file is necessary to close.
                            eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));
                        } else {
                            //File is changed in the commit to reset, so this file is necessary to refresh
                            refreshFile(file, partPresenter);
                        }
                    }
                    Notification notification = new Notification(constant.resetSuccessfully(), INFO);
                    notificationManager.showNotification(notification);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResetToCommitPresenter.class, "can not refresh children for project");
            }
        });
    }

    private void refreshFile(final File file, final EditorPartPresenter partPresenter){
        final Project project = resourceProvider.getActiveProject();
        project.findResourceByPath(file.getPath(), new AsyncCallback<Resource>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResetToCommitPresenter.class, "can not find file " + file.getPath());
            }

            @Override
            public void onSuccess(final Resource result) {
                updateOpenedFile((File) result, partPresenter);
            }
        });
    }

    private void updateOpenedFile(final File file, final EditorPartPresenter partPresenter) {
        resourceProvider.getActiveProject().getContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                try {
                    partPresenter.getEditorInput().setFile(result);
                    partPresenter.init(partPresenter.getEditorInput());

                } catch (EditorInitException event) {
                    Log.error(ResetToCommitPresenter.class, "can not initializes the editor with the given input " + event);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResetToCommitPresenter.class, "can not get content for file " + file.getRelativePath());
            }
        });
    }
}

