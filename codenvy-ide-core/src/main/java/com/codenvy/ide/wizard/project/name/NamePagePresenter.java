/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.project.name;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


/**
 * @author Evgen Vidolob
 */
@Singleton
public class NamePagePresenter extends AbstractWizardPage implements NamePageView.ActionDelegate {

    private NamePageView              view;
    private ProjectServiceClient      projectService;
    private DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private ResourceProvider          resourceProvider;
    private ProjectTypeWizardRegistry wizardRegistry;
    private ProjectWizard             wizard;

    @Inject
    public NamePagePresenter(NamePageView view, ProjectServiceClient projectService, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             ResourceProvider resourceProvider, ProjectTypeWizardRegistry wizardRegistry) {
        super("Name", null);
        this.view = view;
        this.projectService = projectService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.resourceProvider = resourceProvider;
        this.wizardRegistry = wizardRegistry;
        view.setDelegate(this);
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
            return !view.getProjectName().equals("");
    }

    @Override
    public void focusComponent() {
        view.focusOnNameField();
    }

    @Override
    public void setContext(@NotNull WizardContext wizardContext) {
        super.setContext(wizardContext);
    }

    @Override
    public void removeOptions() {

    }

    @Override
    public void commit(@NotNull final CommitCallback callback) {
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE);
        if (templateDescriptor == null && wizard != null) {
            wizard.onFinish();
            return;
        }
        final String projectName = view.getProjectName();
        projectService.importProject(projectName, templateDescriptor.getSources(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                                                 @Override
                                                 public void onSuccess(Project project) {
//                                                     wizardContext.putData(PROJECT, result);
                                                     callback.onSuccess();
                                                 }

                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     callback.onFailure(caught);
                                                 }
                                             });
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             callback.onFailure(exception);
                                         }
                                     }
                                    );
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        wizard = null;
        ProjectTypeDescriptor descriptor = wizardContext.getData(ProjectWizard.PROJECT_TYPE);
        if(descriptor != null){
            wizard = wizardRegistry.getWizard(descriptor.getProjectTypeId());
            if (wizard != null) {
                wizard.flipToFirst();
            }
        }
    }

    public Array<String> getStepsCaptions() {
        Array<String> stringArray = Collections.createArray("Choose Project", getCaption());
        if(wizardContext.getData(ProjectWizard.PROJECT_TEMPLATE) != null) {
            return stringArray;
        }
        if(wizard != null){
            stringArray.addAll(wizard.getStepsCaptions());
            return stringArray;
        }
        return Collections.createArray("");
    }

    public Array<WizardPage> getNextPages(){
        if(wizard != null){
            return wizard.getPages();
        }
        return null;
    }

    @Override
    public void projectNameChanged(String name) {
        wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
        delegate.updateControls();
    }
}
