/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newresource.NewResourcePageView.ActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Provides selecting kind of file which user wish to create.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewResourcePagePresenter extends AbstractWizardPagePresenter implements ActionDelegate {
    private NewResourcePageView view;
    private WizardPagePresenter next;

    /**
     * Create presenter.
     *
     * @param resources
     * @param view
     */
    @Inject
    protected NewResourcePagePresenter(Resources resources, NewResourcePageView view, WizardAgentImpl wizardAgent) {
        super("Create a new resource", resources.newResourceIcon());
        this.view = view;
        this.view.setDelegate(this);
        JsonArray<NewResourceWizardData> resourceWizards = wizardAgent.getNewResourceWizards();
        this.view.setResourceWizard(resourceWizards);
    }

    /** {@inheritDoc} */
    public boolean isCompleted() {
        return next != null;
    }

    /** {@inheritDoc} */
    public boolean hasNext() {
        return next != null;
    }

    /** {@inheritDoc} */
    public WizardPagePresenter flipToNext() {
        next.setPrevious(this);
        next.setUpdateDelegate(delegate);
        return next;
    }

    /** {@inheritDoc} */
    public String getNotice() {
        if (next == null) {
            return "Please, select resource type.";
        }

        return null;
    }

    /** {@inheritDoc} */
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    public void selectedFileType(NewResourceWizardData newResourceWizard) {
        next = newResourceWizard.getWizardPage();
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    public boolean canFinish() {
        return false;
    }
}