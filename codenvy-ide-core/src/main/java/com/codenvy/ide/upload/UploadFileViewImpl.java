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
package com.codenvy.ide.upload;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link UploadFileView}.
 *
 * @author Roman Nikitenko.
 */
public class UploadFileViewImpl extends Window implements UploadFileView {

    public interface UploadFileViewBinder extends UiBinder<Widget, UploadFileViewImpl> {
    }

    Button btnCancel;
    Button btnUpload;

    @UiField
    FormPanel uploadForm;

    FileUpload     file;
    ActionDelegate delegate;

    /** Create view. */
    @Inject
    public UploadFileViewImpl(UploadFileViewBinder uploadFileViewBinder, CoreLocalizationConstant locale) {

        this.setTitle(locale.uploadFileTitle());
        setWidget(uploadFileViewBinder.createAndBindUi(this));
        bind();
        
        btnCancel = createButton(locale.cancel(), "file-uploadFile-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnUpload = createButton(locale.uploadButton(), "file-uploadFile-upload", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onUploadClicked();
            }
        });
        getFooter().add(btnUpload);
    }

    /** Bind handlers. */
    private void bind() {
        uploadForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                delegate.onSubmitComplete(event.getResults());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        file = new FileUpload();
        file.setHeight("22px");
        file.setWidth("100%");
        file.setName("file");
        file.ensureDebugId("file-uploadFile-ChooseFile");
        file.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                delegate.onFileNameChanged();
            }
        });
        uploadForm.add(file);

        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
        uploadForm.remove(file);
        file = null;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledUploadButton(boolean enabled) {
        btnUpload.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEncoding(@NotNull String encodingType) {
        uploadForm.setEncoding(encodingType);
    }

    /** {@inheritDoc} */
    @Override
    public void setAction(@NotNull String url) {
        uploadForm.setAction(url);
        uploadForm.setMethod(FormPanel.METHOD_POST);
    }

    /** {@inheritDoc} */
    @Override
    public void submit() {
        uploadForm.submit();
    }

    /** {@inheritDoc} */
    @Override
    public String getFileName() {
        return file.getName();
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }

}
