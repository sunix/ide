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
package com.codenvy.ide.api.ui.wizard.paas;

import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;

/**
 * The abstract implementation of page that the PaaS provides.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractPaasPage extends AbstractWizardPage {
    private String paasID;

    /**
     * Create PaaS page.
     *
     * @param caption
     * @param image
     * @param paasID
     */
    public AbstractPaasPage(@Nullable String caption, @Nullable ImageResource image, @NotNull String paasID) {
        super(caption, image);
        this.paasID = paasID;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
        PaaS paas = wizardContext.getData(PAAS);
        return paas != null && paas.getId().equals(paasID);
    }
}