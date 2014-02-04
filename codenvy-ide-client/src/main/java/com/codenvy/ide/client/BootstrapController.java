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
package com.codenvy.ide.client;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.TemplateClientService;
import com.codenvy.ide.api.template.TemplateDescriptorRegistry;
import com.codenvy.ide.api.ui.theme.Style;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.projecttype.ProjectTypeDescriptionClientService;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Map;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController {

    private ProjectTypeDescriptionClientService projectTypeService;
    private ProjectTypeDescriptorRegistry       projectTypeDescriptorRegistry;
    private TemplateClientService               templateClientService;
    private TemplateDescriptorRegistry          templateDescriptorRegistry;
    private DtoFactory                          dtoFactory;

    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspaceProvider
     * @param styleInjector
     * @param extensionInitializer
     * @param preferencesManager
     * @param userService
     * @param projectTypeService
     * @param projectTypeDescriptorRegistry
     * @param templateClientService
     * @param templateDescriptorRegistry
     * @param resourceProvider
     * @param dtoRegistrar
     * @param dtoFactory
     * @param themeAgent
     */
    @Inject
    public BootstrapController(final Provider<ComponentRegistry> componentRegistry,
                               final Provider<WorkspacePresenter> workspaceProvider,
                               final StyleInjector styleInjector,
                               final ExtensionInitializer extensionInitializer,
                               final PreferencesManagerImpl preferencesManager,
                               UserClientService userService,
                               final ProjectTypeDescriptionClientService projectTypeService,
                               final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                               TemplateClientService templateClientService,
                               final TemplateDescriptorRegistry templateDescriptorRegistry,
                               final ResourceProvider resourceProvider,
                               DtoRegistrar dtoRegistrar,
                               final DtoFactory dtoFactory,
                               final ThemeAgent themeAgent) {
        this.projectTypeService = projectTypeService;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.templateClientService = templateClientService;
        this.templateDescriptorRegistry = templateDescriptorRegistry;
        this.dtoFactory = dtoFactory;

        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_base.js").setWindow(ScriptInjector.TOP_WINDOW)
                      .setCallback(new Callback<Void, Exception>() {
                          @Override
                          public void onSuccess(Void result) {
                              ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_parsers.js")
                                            .setWindow(ScriptInjector.TOP_WINDOW).inject();
                          }

                          @Override
                          public void onFailure(Exception reason) {
                          }
                      }).inject();

        try {
            dtoRegistrar.registerDtoProviders();
            userService.getUser(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(final String result) {
                    final User user = dtoFactory.createDtoFromJson(result, User.class);
                    Map<String, String> attributes = user.getProfileAttributes();
                    preferencesManager.load(attributes);
                    String theme = preferencesManager.getValue("Theme");
                    if (theme != null) {
                        Style.setTheme(themeAgent.getTheme(theme));
                        themeAgent.setCurrentThemeId(theme);
                    } else {
                        Style.setTheme(themeAgent.getDefault());
                        themeAgent.setCurrentThemeId(themeAgent.getDefault().getId());
                    }
                    styleInjector.inject();

                    // initialize components
                    componentRegistry.get().start(new Callback<Void, ComponentException>() {
                        @Override
                        public void onSuccess(Void result) {
                            // instantiate extensions
                            extensionInitializer.startExtensions();
                            // Start UI
                            SimplePanel mainPanel = new SimplePanel();
                            RootLayoutPanel.get().add(mainPanel);
                            WorkspacePresenter workspacePresenter = workspaceProvider.get();

                            workspacePresenter.setUpdateButtonVisibility(Utils.isAppLaunchedInSDKRunner());

                            final String userId = user.getUserId();
                            if (userId.equals("__anonim")) {
                                workspacePresenter.setVisibleLoginButton(true);
                                workspacePresenter.setVisibleLogoutButton(false);
                            } else {
                                workspacePresenter.setVisibleLoginButton(false);
                                workspacePresenter.setVisibleLogoutButton(true);
                            }

                            // Display IDE
                            workspacePresenter.go(mainPanel);
                            // Display list of projects in project explorer
                            resourceProvider.showListProjects();
                        }

                        @Override
                        public void onFailure(ComponentException caught) {
                            Log.error(BootstrapController.class, "FAILED to start service:" + caught.getComponent(), caught);
                        }
                    });

                    initializeProjectTypeDescriptorRegistry();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(BootstrapController.class, exception);
                }
            });
        } catch (RequestException e) {
            Log.error(BootstrapController.class, e);
        }
    }

    private void initializeProjectTypeDescriptorRegistry() {
        try {
            projectTypeService.getProjectTypes(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    projectTypeDescriptorRegistry.registerDescriptors(
                            dtoFactory.createListDtoFromJson(result, ProjectTypeDescriptor.class));
                    initializeProjectTemplateRegistry();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(BootstrapController.class, exception);
                }
            });
        } catch (RequestException e) {
            Log.error(BootstrapController.class, e);
        }
    }

    private void initializeProjectTemplateRegistry() {
        Array<ProjectTypeDescriptor> descriptors = projectTypeDescriptorRegistry.getDescriptors();
        for (ProjectTypeDescriptor descriptor : descriptors.asIterable()) {
            try {
                templateClientService.getTemplates(descriptor, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(String result) {
                        templateDescriptorRegistry.registerTemplates(
                                dtoFactory.createListDtoFromJson(result, ProjectTemplateDescriptor.class));
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(BootstrapController.class, exception);
                    }
                });
            } catch (RequestException e) {
                Log.error(BootstrapController.class, e);
            }
        }
    }
}
