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
package com.codenvy.ide.everrest;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.user.User;

import org.apache.catalina.websocket.StreamInbound;
import org.everrest.core.DependencySupplier;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.ApplicationProviderBinder;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.EverrestProcessor;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.tools.SimplePrincipal;
import org.everrest.websockets.EverrestWebSocketServlet;
import org.everrest.websockets.WSConnectionImpl;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/** @author andrew00x */
@Singleton
public class CodenvyEverrestWebSocketServlet extends EverrestWebSocketServlet {
    static final String ENVIRONMENT_CONTEXT = "ide.websocket." + EnvironmentContext.class.getName();

    @Override
    protected EverrestProcessor getEverrestProcessor() {
        final ServletContext servletContext = getServletContext();
        final EverrestConfiguration config = (EverrestConfiguration)servletContext.getAttribute(EverrestConfiguration.class.getName());
        final DependencySupplier dependencies = (DependencySupplier)servletContext.getAttribute(DependencySupplier.class.getName());
        final ResourceBinder resources = (ResourceBinder)servletContext.getAttribute(ResourceBinder.class.getName());
        final ProviderBinder providers = (ProviderBinder)servletContext.getAttribute(ApplicationProviderBinder.class.getName());
        final EverrestConfiguration copy = new EverrestConfiguration(config);
        copy.setProperty(EverrestConfiguration.METHOD_INVOKER_DECORATOR_FACTORY, WebSocketMethodInvokerDecoratorFactory.class.getName());
        return new EverrestProcessor(resources, providers, dependencies, copy, null);
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest req) {
        WSConnectionImpl wsConnection = (WSConnectionImpl)super.createWebSocketInbound(s, req);
        wsConnection.getHttpSession().setAttribute(ENVIRONMENT_CONTEXT, EnvironmentContext.getCurrent());
        return wsConnection;
    }

    @Override
    protected SecurityContext createSecurityContext(HttpServletRequest req) {
        final User user = EnvironmentContext.getCurrent().getUser();
        if (user == null) {
            return super.createSecurityContext(req);
        }
        final Principal principal = new SimplePrincipal(user.getName());
        final boolean secure = req.isSecure();
        final String authType = req.getAuthType();

        return new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(String role) {
                return user.isMemberOf(role);
            }

            @Override
            public boolean isSecure() {
                return secure;
            }

            @Override
            public String getAuthenticationScheme() {
                return authType;
            }
        };
    }
}
