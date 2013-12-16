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
package com.codenvy.ide.ext.git.client.fetch;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;

/**
 * Presenter for fetching changes from remote repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 1:33:17 PM anya $
 */
@Singleton
public class FetchPresenter implements FetchView.ActionDelegate {
    private FetchView               view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private Project                 project;
    private NotificationManager     notificationManager;
    private DtoFactory              dtoFactory;

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
    public FetchPresenter(FetchView view, GitClientService service, ResourceProvider resourceProvider,
                          GitLocalizationConstant constant, NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        view.setRemoveDeleteRefs(false);
        view.setFetchAllBranches(true);
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then get the list of branches (remote and
     * local).
     */
    private void getRemotes() {
        final String projectId = project.getId();

        try {
            service.remoteList(resourceProvider.getVfsInfo().getId(), projectId, null, true,
                               new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                   @Override
                                   protected void onSuccess(String result) {
                                       Array<Remote> remotes = dtoFactory.createListDtoFromJson(result, Remote.class);
                                       getBranches(projectId, LIST_REMOTE);
                                       getBranches(projectId, LIST_LOCAL);
                                       view.setEnableFetchButton(!result.isEmpty());
                                       view.setRepositories(remotes);
                                       view.showDialog();
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       String errorMessage =
                                               exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                       Window.alert(errorMessage);
                                       view.setEnableFetchButton(false);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteListFailed();
            Window.alert(errorMessage);
            view.setEnableFetchButton(false);
        }
    }

    /**
     * Get the list of branches.
     *
     * @param projectId
     *         Git repository work tree location
     * @param remoteMode
     *         is a remote mode
     */
    private void getBranches(@NotNull String projectId, @NotNull final String remoteMode) {
        try {
            service.branchList(resourceProvider.getVfsInfo().getId(), projectId, remoteMode,
                               new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                   @Override
                                   protected void onSuccess(String result) {
                                       Array<Branch> branches = dtoFactory.createListDtoFromJson(result, Branch.class);
                                       if (LIST_REMOTE.equals(remoteMode)) {
                                           view.setRemoteBranches(getRemoteBranchesToDisplay(view.getRepositoryName(), branches));
                                       } else {
                                           view.setLocalBranches(getLocalBranchesToDisplay(branches));
                                       }
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       String errorMessage =
                                               exception.getMessage() != null ? exception.getMessage() : constant.branchesListFailed();
                                       Notification notification = new Notification(errorMessage, ERROR);
                                       notificationManager.showNotification(notification);
                                       view.setEnableFetchButton(false);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.branchesListFailed();
            Notification notification = new Notification(errorMessage, ERROR);
            notificationManager.showNotification(notification);
            view.setEnableFetchButton(false);
        }
    }

    /**
     * Set values of remote branches: filter remote branches due to selected remote repository.
     *
     * @param remoteName
     *         remote name
     * @param remoteBranches
     *         remote branches
     */
    @NotNull
    private Array<String> getRemoteBranchesToDisplay(@NotNull String remoteName, @NotNull Array<Branch> remoteBranches) {
        Array<String> branches = Collections.createArray();

        if (remoteBranches.isEmpty()) {
            branches.add("master");
            return branches;
        }

        String compareString = "refs/remotes/" + remoteName + "/";
        for (int i = 0; i < remoteBranches.size(); i++) {
            Branch branch = remoteBranches.get(i);
            String branchName = branch.getName();
            if (branchName.startsWith(compareString)) {
                branches.add(branchName.replaceFirst(compareString, ""));
            }
        }

        if (branches.isEmpty()) {
            branches.add("master");
        }
        return branches;
    }

    /**
     * Set values of local branches.
     *
     * @param localBranches
     *         local branches
     */
    @NotNull
    private Array<String> getLocalBranchesToDisplay(@NotNull Array<Branch> localBranches) {
        Array<String> branches = Collections.createArray();

        if (localBranches.isEmpty()) {
            branches.add("master");
            return branches;
        }

        String compareString = "refs/heads/";
        for (int i = 0; i < localBranches.size(); i++) {
            Branch branch = localBranches.get(i);
            String branchName = branch.getName().replaceFirst(compareString, "");
            branches.add(branchName);
        }

        return branches;
    }

    /** {@inheritDoc} */
    @Override
    public void onFetchClicked() {
        final String remoteUrl = view.getRepositoryUrl();
        String remoteName = view.getRepositoryName();
        boolean removeDeletedRefs = view.isRemoveDeletedRefs();
        
        try {
            service.fetchWS(resourceProvider.getVfsInfo().getId(), project, remoteName, getRefs(), removeDeletedRefs, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    Notification notification = new Notification(constant.fetchSuccess(remoteUrl), INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception, remoteUrl);
                }
            });
        } catch (WebSocketException e) {
            doFetchREST(remoteName, removeDeletedRefs, remoteUrl);
        }
        view.close();
    }

    /** Perform fetch from remote repository (sends request over HTTP). */
    private void doFetchREST(@NotNull String remoteName, boolean removeDeletedRefs, @NotNull final String remoteUrl) {
        try {
            service.fetch(resourceProvider.getVfsInfo().getId(), project, remoteName, getRefs(), removeDeletedRefs,
                          new AsyncRequestCallback<String>() {
                              @Override
                              protected void onSuccess(String result) {
                                  Notification notification = new Notification(constant.fetchSuccess(remoteUrl), INFO);
                                  notificationManager.showNotification(notification);
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  handleError(exception, remoteUrl);
                              }
                          });
        } catch (RequestException e) {
            handleError(e, remoteUrl);
        }
    }

    /** @return list of refs to fetch */
    @NotNull
    private List<String> getRefs() {
        if (view.isFetchAllBranches()){
            return new ArrayList<String>();
        }
        
        String localBranch = view.getLocalBranch();
        String remoteBranch = view.getRemoteBranch();
        String remoteName = view.getRepositoryName();
        String refs = localBranch.isEmpty() ? remoteBranch
                                            : "refs/heads/" + localBranch + ":" + "refs/remotes/" + remoteName + "/" + remoteBranch;
        return new ArrayList<String>(Arrays.asList(refs));
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param t
     *         exception what happened
     */
    private void handleError(@NotNull Throwable t, @NotNull String remoteUrl) {
        String errorMessage = (t.getMessage() != null) ? t.getMessage() : constant.fetchFail(remoteUrl);
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
        boolean isFetchAll = view.isFetchAllBranches();
        view.setEnableLocalBranchField(!isFetchAll);
        view.setEnableRemoteBranchField(!isFetchAll);
    }
}