/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.wizard.project.main;

import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.projecttype.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class MainPagePresenter extends AbstractWizardPage implements MainPageView.ActionDelegate {

    private MainPageView                  view;
    private ProjectTypeDescriptorRegistry registry;
    private ProjectTypeWizardRegistry     wizardRegistry;
    private ProjectTypeDescriptor         typeDescriptor;
    private ProjectTemplateDescriptor     template;

    @Inject
    public MainPagePresenter(MainPageView view, ProjectTypeDescriptorRegistry registry, ProjectTypeWizardRegistry wizardRegistry) {
        super("Choose Project", null);
        this.view = view;
        this.registry = registry;
        this.wizardRegistry = wizardRegistry;
        view.setDelegate(this);
    }

    @Override
    public void projectNameChanged(String name) {
        RegExp regExp = RegExp.compile("^[A-Za-z0-9_-]+$");
        if (regExp.test(name)) {
            wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
            view.removeNameError();
        } else {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
            view.showNameError();
        }
        delegate.updateControls();
    }

    @Override
    public void projectDescriptionChanged(String projectDescriptionValue) {
        wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, projectDescriptionValue);
    }

    @Override
    public void projectVisibilityChanged(Boolean aPublic) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
    }

    @Override
    public ProjectWizardView.ActionDelegate getProjectWizardDelegate() {
        return (ProjectWizardView.ActionDelegate)delegate;
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return (typeDescriptor != null || template != null) && wizardContext.getData(ProjectWizard.PROJECT_NAME) != null;
    }

    @Override
    public void focusComponent() {

    }

    @Override
    public void removeOptions() {

    }

    @Override
    public void go(AcceptsOneWidget container) {
        view.reset();
        typeDescriptor = null;
        template = null;
        Map<String, Set<ProjectTypeDescriptor>> descriptorsByCategory = new HashMap<>();
        Array<ProjectTypeDescriptor> descriptors = registry.getDescriptors();
        Map<String, Set<ProjectTemplateDescriptor>> samples = new HashMap<>();
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        for (ProjectTypeDescriptor descriptor : descriptors.asIterable()) {
            if (wizardRegistry.getWizard(descriptor.getProjectTypeId()) != null) {
                if (!descriptorsByCategory.containsKey(descriptor.getProjectTypeCategory())) {
                    descriptorsByCategory.put(descriptor.getProjectTypeCategory(), new HashSet<ProjectTypeDescriptor>());
                }
                descriptorsByCategory.get(descriptor.getProjectTypeCategory()).add(descriptor);
            }
            if (project == null) {
                if (descriptor.getTemplates() != null && !descriptor.getTemplates().isEmpty()) {
                    for (ProjectTemplateDescriptor templateDescriptor : descriptor.getTemplates()) {
                        String category = templateDescriptor.getCategory() == null ? ProjectTemplateDescription.defaultCategory
                                                                                   : templateDescriptor.getCategory();
                        if (!samples.containsKey(category)) {
                            samples.put(category, new HashSet<ProjectTemplateDescriptor>());
                        }
                        samples.get(category).add(templateDescriptor);
                    }
                }
            }
        }
        container.setWidget(view);

        view.setProjectTypeCategories(descriptorsByCategory, samples);
        if (project != null) {
            view.selectProjectType(project.getProjectTypeId());
            view.setVisibility(project.getVisibility().equals("public"));
            view.setName(project.getName());
            view.setDescription(project.getDescription());
        }
    }

    @Override
    public void projectTypeSelected(ProjectTypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
        template = null;
        wizardContext.putData(ProjectWizard.PROJECT_TYPE, typeDescriptor);
        wizardContext.removeData(ProjectWizard.PROJECT_TEMPLATE);
        delegate.updateControls();
        view.enableInput();
    }

    @Override
    public void projectTemplateSelected(ProjectTemplateDescriptor template) {
        this.template = template;
        wizardContext.putData(ProjectWizard.PROJECT_TEMPLATE, template);
        wizardContext.removeData(ProjectWizard.PROJECT_TYPE);
        typeDescriptor = null;
        delegate.updateControls();
    }
}
