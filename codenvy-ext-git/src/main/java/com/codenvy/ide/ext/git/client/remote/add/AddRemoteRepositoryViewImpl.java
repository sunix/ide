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
package com.codenvy.ide.ext.git.client.remote.add;

import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link AddRemoteRepositoryView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class AddRemoteRepositoryViewImpl extends Window implements AddRemoteRepositoryView {
    interface AddRemoteRepositoryViewImplUiBinder extends UiBinder<Widget, AddRemoteRepositoryViewImpl> {
    }

    private static AddRemoteRepositoryViewImplUiBinder ourUiBinder = GWT.create(AddRemoteRepositoryViewImplUiBinder.class);

    @UiField
    TextBox name;
    @UiField
    TextBox url;
    Button  btnOk;
    Button  btnCancel;
    @UiField(provided = true)
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected AddRemoteRepositoryViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle("Add remote repository");
        this.setWidget(widget);
        
        btnCancel = createButton(locale.buttonCancel(), "", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnOk = createButton(locale.buttonOk(), "", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onOkClicked();
            }
        });
        getFooter().add(btnOk);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getName() {
        return name.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(@NotNull String name) {
        this.name.setText(name);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getUrl() {
        return url.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setUrl(@NotNull String url) {
        this.url.setText(url);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableOkButton(boolean enable) {
        btnOk.setEnabled(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler({"name", "url"})
    public void onValueChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}