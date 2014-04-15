// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.texteditor;

import elemental.html.DivElement;
import elemental.dom.Element;

import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.mvp.UiComponent;
import com.codenvy.ide.util.dom.DomUtils;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;


/** The main content area on the CodePerspective. */
public class EditableContentArea extends UiComponent<EditableContentArea.View> {

    /** Static factory method for obtaining an instance of the EditableContentArea. */
    public static EditableContentArea create(View view) {
        return new EditableContentArea(view);
    }

    /** Type for things that can be added to the content area of the CodePerspective. */
    public interface Content {

        /**
         * @return The {@link Element} that we set as the contents of the content
         *         area.
         */
        Element getContentElement();

        /**
         * Called when the content is displayed.
         * <p/>
         * It's possible that element returned by {@link #getContentElement()} was removed from DOM and
         * re-added, so this callback is a good place to re-initialize any values that may have been
         * cleared.
         */
        void onContentDisplayed();
    }

    /** Style names. */
    public interface Css extends CssResource {
        String base();

        String contentHeader();

        String contentContainer();

        int editableContentAreaRight();
    }

    public interface Resources extends ClientBundle {

        @Source({"com/codenvy/ide/common/constants.css", "EditableContentArea.css", "com/codenvy/ide/api/ui/style.css"})
        Css editableContentAreaCss();
    }

    /** The View for the EditableContentArea. */
    public static class View extends CompositeView<Void> {
        //    private DivElement header;
        private DivElement content;

        //    private final WorkspaceLocationBreadcrumbs.View breadcrumbsView;
        //    private final EditorToolBar.View editorToolBarView;
        private final Css css;

        public View(Resources res) {
            super(Elements.createDivElement(res.editableContentAreaCss().base()));
            this.css = res.editableContentAreaCss();

            //      // Instantiate sub-views.
            //      this.breadcrumbsView = new WorkspaceLocationBreadcrumbs.View(res);
            //      this.editorToolBarView = new EditorToolBar.View(res);

            createDom();
        }

        public Element getContentElement() {
            return content;
        }

        //    public Element getHeaderElement() {
        //      return header;
        //    }

        //    public EditorToolBar.View getEditorToolBarView() {
        //      return editorToolBarView;
        //    }
        //
        //    public WorkspaceLocationBreadcrumbs.View getBreadcrumbsView() {
        //      return breadcrumbsView;
        //    }

        public int getDefaultEditableContentAreaRight() {
            return css.editableContentAreaRight();
        }

        private void createDom() {
            Element elem = getElement();
            //      header = Elements.createDivElement(css.contentHeader());
            content = Elements.createDivElement(css.contentContainer());

            //      elem.appendChild(header);
            elem.appendChild(content);
            //      header.appendChild(breadcrumbsView.getElement());
            //      header.appendChild(editorToolBarView.getElement());
        }
    }

    private Content currentContent;

    //  private final EditorToolBar toolBar;

    EditableContentArea(View view) {
        super(view);
        //    this.toolBar = toolBar;
    }

    //  public EditorToolBar getEditorToolBar() {
    //    return toolBar;
    //  }

    public Content getCurrentContent() {
        return currentContent;
    }

    /**
     * Sets the contents of the content area under the header.
     *
     * @param content
     *         the content to display, or null to clear content
     * @param showHistoryIcon
     *         whether or not to show the history button for this content.
     */
    public void setContent(Content content, boolean showHistoryIcon) {
        if (currentContent == content) {
            return;
        }

        if (currentContent != null) {
            DomUtils.removeFromParent(currentContent.getContentElement());
        }

        getView().getContentElement().setInnerHTML("");
        if (content != null) {
            getView().getContentElement().appendChild(content.getContentElement());
        }
        currentContent = content;
        currentContent.onContentDisplayed();

        //    getEditorToolBar().getView().setHistoryButtonVisible(showHistoryIcon);

        //    setLocationBreadcrumbsVisibility(true);
    }

    /**
     * Sets the contents of the content area under the header. Assumes content can
     * display history and shows history icon. (Use
     * {@link #setContent(Content, boolean)} to customize).
     *
     * @param content
     *         the content to display, or null to clear content
     */
    public void setContent(Content content) {
        setContent(content, true);
    }

    //  public void setLocationBreadcrumbsVisibility(boolean visible) {
    //    CssUtils.setDisplayVisibility2(getView().breadcrumbsView.getElement(), visible);
    //  }
}
