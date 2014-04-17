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
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private       String                  diff;

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
        ResetRequest.ResetType type = view.isMixMode() ? ResetRequest.ResetType.MIXED : null;
        type = (type == null && view.isSoftMode()) ? ResetRequest.ResetType.SOFT : type;
        type = (type == null && view.isHardMode()) ? ResetRequest.ResetType.HARD : type;
        type = (type == null && view.isKeepMode()) ? ResetRequest.ResetType.KEEP : type;
        type = (type == null && view.isMergeMode()) ? ResetRequest.ResetType.MERGE : type;

        final List<String> listOpenedFiles = new ArrayList<>();
        final Map<String, File> openedFiles = new HashMap<>();
        for (String key : editorAgent.getOpenedEditors().getKeys().asIterable()) {
            File openFile = editorAgent.getOpenedEditors().get(key).getEditorInput().getFile();
            openedFiles.put(openFile.getRelativePath(), openFile);
            listOpenedFiles.add(openFile.getRelativePath());
        }

        getDiff(listOpenedFiles, selectedRevision.getId());

        service.reset(projectId, selectedRevision.getId(), type,
                      new AsyncRequestCallback<Void>() {
                          @Override
                          protected void onSuccess(Void result) {
                              resourceProvider.getActiveProject().refreshChildren(new AsyncCallback<Project>() {
                                  @Override
                                  public void onSuccess(Project result) {
                                      for (final File file : openedFiles.values()) {
                                          String filePath = file.getRelativePath();
                                          if (diff.contains(filePath)) {
                                              int firstIndex = diff.indexOf(filePath);
                                              int lastIndex = diff.lastIndexOf(filePath);
                                              String between = diff.substring(firstIndex, lastIndex);

                                              if (between.contains("new file mode")) {
                                                  eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));
                                              } else {
                                                  updateContent(file);
                                              }
                                          }
                                      }
                                      Notification notification = new Notification(constant.resetSuccessfully(), INFO);
                                      notificationManager.showNotification(notification);
                                  }

                                  @Override
                                  public void onFailure(Throwable caught) {
                                      Log.error(ResetToCommitPresenter.class, caught);
                                  }
                              });
                              view.close();
                          }

                          @Override
                          protected void onFailure(Throwable exception) {
                              String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.resetFail();
                              Notification notification = new Notification(errorMessage, ERROR);
                              notificationManager.showNotification(notification);
                          }
                      }
                     );
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

    private void getDiff(List<String> listFiles, String commit) {
        String projectId = resourceProvider.getActiveProject().getId();
        service.diff(projectId, listFiles, RAW, false, 0, commit, false,
                     new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                         @Override
                         protected void onSuccess(String result) {
                             diff = result;
                         }

                         @Override
                         protected void onFailure(Throwable exception) {
                         }
                     });
    }

    private void updateContent(final File file) {
        final Project project = resourceProvider.getActiveProject();
        if (project != null) {
            final File fileWithDiff = (File)project.findResourceById(file.getId());

            project.getContent(fileWithDiff, new AsyncCallback<File>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(File result) {
                    project.updateContent(result, new AsyncCallback<File>() {
                        @Override
                        public void onSuccess(File result) {
                            eventBus.fireEvent(new FileEvent(result, FileEvent.FileOperation.SAVE));
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                        }
                    });
                }
            });
        }
    }
}