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
package org.exoplatform.ide.extension.php.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.extension.php.shared.ApplicationInstance;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpRuntimeServiceImpl.java Apr 17, 2013 4:19:29 PM azatsarynnyy $
 *
 */
public class PhpRuntimeServiceImpl extends PhpRuntimeService {
    
    private static final String BASE_URL = "/php/runner";
    
    private static final String LOGS = BASE_URL + "/logs";

    private static final String RUN_APPLICATION = BASE_URL + "/run";

    private static final String STOP_APPLICATION = BASE_URL + "/stop";


    private final String wsName;

    private final String restContext;

    private final MessageBus wsMessageBus;

    public PhpRuntimeServiceImpl(String restContext, String wsName, MessageBus wsMessageBus) {
        this.restContext = restContext;
        this.wsName = wsName;
        this.wsMessageBus = wsMessageBus;
        
    }

    /**
     * @see org.exoplatform.ide.extension.php.client.PhpRuntimeService#start(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void start(String vfsId, ProjectModel project, RequestCallback<ApplicationInstance> callback)
            throws WebSocketException {
        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(project.getId());
        RequestMessage message =
            RequestMessageBuilder.build(RequestBuilder.GET, wsName + RUN_APPLICATION + params).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @see org.exoplatform.ide.extension.php.client.PhpRuntimeService#stop(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException {
        String requestUrl = restContext + wsName + STOP_APPLICATION;

        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString())
                    .requestStatusHandler(new StopApplicationStatusHandler(name)).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.php.client.PhpRuntimeService#getLogs(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restContext + wsName + LOGS;
        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        Loader loader = new GWTLoader();
        loader.setMessage("Retrieving logs.... ");

        AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
    }

}