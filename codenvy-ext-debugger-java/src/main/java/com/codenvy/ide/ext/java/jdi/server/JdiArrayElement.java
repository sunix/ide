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
package com.codenvy.ide.ext.java.jdi.server;

/**
 * Element of array in debuggee JVM.
 *
 * @author andrew00x
 */
public interface JdiArrayElement extends JdiVariable {
    /**
     * Get index of this element of array.
     *
     * @return index of this array element
     * @throws DebuggerException
     *         if an error occurs
     */
    int getIndex() throws DebuggerException;
}
