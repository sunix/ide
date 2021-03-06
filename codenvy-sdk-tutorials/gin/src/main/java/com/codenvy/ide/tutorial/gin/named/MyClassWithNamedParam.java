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
package com.codenvy.ide.tutorial.gin.named;

import com.codenvy.ide.api.parts.ConsolePart;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * The class that uses {@link Named} annotation for defining string value.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyClassWithNamedParam {
    private ConsolePart console;
    private String      someText;

    @Inject
    public MyClassWithNamedParam(ConsolePart console, @Named("myString") String someText) {
        this.console = console;
        this.someText = someText;
    }

    public void doSomething() {
        console.print(someText);
    }
}