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

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Window;

import com.codenvy.api.project.gwt.client.ProjectTypeDescriptionServiceClient;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.ide.Constants;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.theme.Style;
import com.codenvy.ide.api.ui.theme.Theme;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Map;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController {

    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private       PreferencesManagerImpl              preferencesManager;
    private       ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient;
    private       ProjectTypeDescriptorRegistry       projectTypeDescriptorRegistry;
    private       IconRegistry                        iconRegistry;
    private       ThemeAgent                          themeAgent;

    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspaceProvider
     * @param styleInjector
     * @param extensionInitializer
     * @param preferencesManager
     * @param userProfileService
     * @param projectTypeDescriptionServiceClient
     * @param projectTypeDescriptorRegistry
     * @param resourceProvider
     * @param dtoRegistrar
     * @param themeAgent
     */
    @Inject
    public BootstrapController(final Provider<ComponentRegistry> componentRegistry,
                               final Provider<WorkspacePresenter> workspaceProvider,
                               final StyleInjector styleInjector,
                               final ExtensionInitializer extensionInitializer,
                               final PreferencesManagerImpl preferencesManager,
                               final UserProfileServiceClient userProfileService,
                               final ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient,
                               final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                               final IconRegistry iconRegistry,
                               final ResourceProvider resourceProvider,
                               DtoRegistrar dtoRegistrar,
                               final ThemeAgent themeAgent,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory, Resources resources) {
        this.preferencesManager = preferencesManager;
        this.projectTypeDescriptionServiceClient = projectTypeDescriptionServiceClient;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.iconRegistry = iconRegistry;
        this.themeAgent = themeAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

        //Is necessary for loading IDE styles before standard GWT one:
//        setTheme();
//        styleInjector.inject();

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

        dtoRegistrar.registerDtoProviders();
        registerDefaultIcon(resources);
        userProfileService.getCurrentProfile(null,
                                             new AsyncRequestCallback<Profile>(dtoUnmarshallerFactory.newUnmarshaller(Profile.class)) {
                                                 @Override
                                                 protected void onSuccess(final Profile profile) {
                                                     Map<String, String> attributes = profile.getPreferences();
                                                     preferencesManager.load(attributes);

                                                     setTheme();
                                                     styleInjector.inject();

                                                     Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                                                         @Override
                                                         public void execute() {
                                                             componentRegistry.get().start(new Callback<Void, ComponentException>() {
                                                                 @Override
                                                                 public void onSuccess(Void result) {
                                                                     // instantiate extensions
                                                                     extensionInitializer.startExtensions();
                                                                     // Start UI
                                                                     SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();


                                                                     RootLayoutPanel.get().add(mainPanel);
                                                                     WorkspacePresenter workspacePresenter = workspaceProvider.get();

                                                                     workspacePresenter
                                                                             .setUpdateButtonVisibility(Utils.isAppLaunchedInSDKRunner());

                                                                     // Display IDE
                                                                     workspacePresenter.go(mainPanel);
                                                                     if (Utils.getProjectToOpen() != null) {
                                                                         resourceProvider.getProject(Utils.getProjectToOpen(),
                                                                                                     new AsyncCallback<Project>() {
                                                                                                         @Override
                                                                                                         public void onFailure(
                                                                                                                 Throwable throwable) {
                                                                                                             // Display list of projects
                                                                                                             // in project explorer
                                                                                                             resourceProvider
                                                                                                                     .showListProjects();
                                                                                                         }

                                                                                                         @Override
                                                                                                         public void onSuccess(
                                                                                                                 Project project) {

                                                                                                         }
                                                                                                     });
                                                                     } else {
                                                                         // Display list of projects in project explorer
                                                                         resourceProvider.showListProjects();
                                                                     }


                                                                     Window window = Browser.getWindow();
                                                                     window.addEventListener(Event.FOCUS, new EventListener() {
                                                                         @Override
                                                                         public void handleEvent(Event evt) {
                                                                             Log.info(BootstrapController.class,
                                                                                      evt.getType() + " " + evt.getTimeStamp());
                                                                         }
                                                                     }, true);

                                                                     window.addEventListener(Event.BLUR, new EventListener() {
                                                                         @Override
                                                                         public void handleEvent(Event evt) {
                                                                             Log.info(BootstrapController.class,
                                                                                      evt.getType() + "  " + evt.getTimeStamp());
                                                                         }
                                                                     }, true);

                                                                 }

                                                                 @Override
                                                                 public void onFailure(ComponentException caught) {
                                                                     Log.error(BootstrapController.class,
                                                                               "FAILED to start service:" + caught.getComponent(), caught);

                                                                     // Handle error when receiving profile.
                                                                     initializationFailed(caught.getMessage());
                                                                 }
                                                             });

                                                             initializeProjectTypeDescriptorRegistry();
                                                         }
                                                     });
                                                 }

                                                 @Override
                                                 protected void onFailure(Throwable exception) {
                                                     Log.error(BootstrapController.class, exception);
                                                 }
                                             }
                                            );
    }

    /**
     * Call this method to handle any of initialization errors. If a function window["on-initialization-failed"] is set, it will be called
     * using 'message' string as a parameter.
     *
     * @param message
     *         error message
     */
    private native void initializationFailed(String message) /*-{
        if ($wnd["on-initialization-failed"]) {
            $wnd["on-initialization-failed"](message);
        }
    }-*/;

    private void setTheme() {
        String storedThemeId = preferencesManager.getValue("Theme");
        storedThemeId = storedThemeId != null ? storedThemeId : themeAgent.getCurrentThemeId();
        Theme themeToSet = storedThemeId != null ? themeAgent.getTheme(storedThemeId) : themeAgent.getDefault();
        Style.setTheme(themeToSet);
        themeAgent.setCurrentThemeId(themeToSet.getId());
    }

    private void initializeProjectTypeDescriptorRegistry() {
        projectTypeDescriptionServiceClient
                .getProjectTypes(new AsyncRequestCallback<Array<ProjectTypeDescriptor>>(
                        dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectTypeDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<ProjectTypeDescriptor> result) {
                        for (int i = 0; i < result.size(); i++) {
                            if (!result.get(i).getProjectTypeId().equalsIgnoreCase(Constants.NAMELESS_ID))// skip
                                // unknown
                                // project
                                // type
                                // user
                                // can select this project type need
                                // use BaseProjectType instead
                                projectTypeDescriptorRegistry.registerDescriptor(result.get(i));
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(BootstrapController.class, exception);
                    }
                });
    }

    
    private void registerDefaultIcon(Resources resources) {
        iconRegistry.registerSVGIcon("default.projecttype.small.icon", resources.defaultProject());
        iconRegistry.registerSVGIcon("default.folder.small.icon", resources.defaultFolder());
        iconRegistry.registerSVGIcon("default.file.small.icon", resources.defaultFile());
        iconRegistry.registerSVGIcon("default", resources.defaultIcon());
    }

}
