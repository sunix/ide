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

import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.client.extensionsPart.ExtensionsPage;
import com.codenvy.ide.client.marshaller.UserUnmarshaller;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Performs initial application startup
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class BootstrapController {
    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspacePeresenter
     * @param styleInjector
     * @param extensionInitializer
     * @param extensionsPage
     * @param preferencesManager
     * @param userService
     */
    @Inject
    public BootstrapController(final ComponentRegistry componentRegistry, final Provider<WorkspacePresenter> workspaceProvider,
                               StyleInjector styleInjector, final ExtensionInitializer extensionInitializer,
                               final ExtensionsPage extensionsPage, final PreferencesManagerImpl preferencesManager,
                               UserClientService userService) {
        styleInjector.inject();

        try {
            DtoClientImpls.UserImpl user = DtoClientImpls.UserImpl.make();
            UserUnmarshaller unmarshaller = new UserUnmarshaller(user);
            userService.getUser(new AsyncRequestCallback<User>(unmarshaller) {
                @Override
                protected void onSuccess(User user) {
                    JsonStringMap<String> attributes = user.getProfileAttributes();
                    preferencesManager.load(attributes);

                    // initialize components
                    componentRegistry.start(new Callback<Void, ComponentException>() {
                        @Override
                        public void onSuccess(Void result) {
                            // instantiate extensions
                            extensionInitializer.startExtensions();
                            // Start UI
                            SimplePanel mainPanel = new SimplePanel();
                            RootLayoutPanel.get().add(mainPanel);
                            WorkspacePresenter workspacePresenter = workspaceProvider.get();
                            // Display IDE
                            workspacePresenter.go(mainPanel);
                            // TODO FOR DEMO
                            workspacePresenter.openPart(extensionsPage, PartStackType.EDITING);
                        }

                        @Override
                        public void onFailure(ComponentException caught) {
                            Log.error(BootstrapController.class, "FAILED to start service:" + caught.getComponent(), caught);
                        }
                    });
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
