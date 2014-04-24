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
package com.codenvy.ide.api.parts.base;

import com.codenvy.ide.api.ui.workspace.AbstractPartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStack;

import javax.validation.constraints.NotNull;

/**
 * Base presenter for parts that support minimizing by part toolbar button.
 *
 * @author Evgen Vidolob
 */
public abstract class BasePresenter extends AbstractPartPresenter implements BaseActionDelegate {
    protected PartStack partStack;

    protected BasePresenter() {
    }

    /** {@inheritDoc} */
    @Override
    public void minimize() {
        if (partStack != null) {
            partStack.hidePart(this);
        }
    }

    /**
     * Set PartStack where this part added.
     *
     * @param partStack
     */
    public void setPartStack(@NotNull PartStack partStack) {
        this.partStack = partStack;
    }
}
