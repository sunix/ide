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
package org.exoplatform.ide.codeassistant.jvm;

/**
 *
 */
public class CodeAssistantException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -2413708596186268688L;

    private int status;

    public CodeAssistantException(int status, String message) {
        super(message);
        this.setStatus(status);
    }

    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *         the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

}