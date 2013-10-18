/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server.builder;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Signals that an error occurs with Codenvy extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: BuilderException.java Oct 17, 2013 4:03:36 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class BuilderException extends Exception {
    private int responseStatus = INTERNAL_SERVER_ERROR.getStatusCode();

    /**
     * Constructs a RunnerException with the specified detail message.
     *
     * @param message the detail message
     */
    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(int responseStatus, String message) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public BuilderException(Throwable cause) {
        super(cause);
    }

    public BuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
