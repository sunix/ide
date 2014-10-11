/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.extension.runner.client.wizard;

import elemental.dom.Element;
import elemental.events.KeyboardEvent;
import elemental.events.MouseEvent;
import elemental.html.SpanElement;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.Resources;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class SelectRunnerPageViewImpl implements SelectRunnerPageView {
    private static SelectRunnerViewImplUiBinder ourUiBinder = GWT.create(SelectRunnerViewImplUiBinder.class);
    private final DockLayoutPanel       rootElement;
    private final Tree<Object>          tree;
    private final RunnerEnvironmentTree root;

    @UiField
    TextBox     recommendedMemory;
    @UiField
    TextArea    runnerDescription;
    @UiField
    SimplePanel treeContainer;
    private ActionDelegate delegate;

    private Map<String, RunnerEnvironment> environmentMap      = new HashMap<>();

    @Inject
    public SelectRunnerPageViewImpl(Resources resources, DtoFactory dtoFactory) {
        rootElement = ourUiBinder.createAndBindUi(this);
        recommendedMemory.getElement().setAttribute("type", "number");
        recommendedMemory.getElement().setAttribute("step", "128");
        recommendedMemory.getElement().setAttribute("min", "0");

        root = dtoFactory.createDto(RunnerEnvironmentTree.class);
        tree = Tree.create(resources, new RunnersDataAdapter(), new RunnersRenderer());
        treeContainer.add(tree);
        tree.setTreeEventHandler(new Tree.Listener<Object>() {
            @Override
            public void onNodeAction(TreeNodeElement<Object> node) {

            }

            @Override
            public void onNodeClosed(TreeNodeElement<Object> node) {

            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Object> node) {

            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Object> node, MouseEvent event) {

            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Object> node, MouseEvent event) {

            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Object> node) {

            }

            @Override
            public void onNodeSelected(TreeNodeElement<Object> node, SignalEvent event) {
                Object data = node.getData();
                if (data instanceof RunnerEnvironment) {
                    delegate.environmentSelected((RunnerEnvironment)data);
                } else {
                    delegate.environmentSelected(null);
                }
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {

            }

            @Override
            public void onRootDragDrop(MouseEvent event) {

            }

            @Override
            public void onKeyboard(KeyboardEvent event) {

            }
        });
    }

    @UiHandler("recommendedMemory")
    void recommendedMemoryChanged(KeyUpEvent event) {
        delegate.recommendedMemoryChanged();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public int getRecommendedMemorySize() {
        try {
            return Integer.parseInt(recommendedMemory.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setRecommendedMemorySize(int recommendedRam) {
        recommendedMemory.setText(String.valueOf(recommendedRam));
    }

    @Override
    public void showRunnerDescriptions(String description) {
        runnerDescription.setText(description);
    }

    @Override
    public void addRunner(RunnerEnvironmentTree environmentTree) {
        root.getChildren().add(environmentTree);
        tree.getModel().setRoot(root);
        tree.renderTree();
        collectRunnerEnvironments(environmentTree);
    }

    private void collectRunnerEnvironments(RunnerEnvironmentTree environmentTree) {
        if (environmentTree.getEnvironments() != null) {
            for (RunnerEnvironment runnerEnvironment : environmentTree.getEnvironments()) {
                environmentMap.put(runnerEnvironment.getId(), runnerEnvironment);
            }
        }

        for (RunnerEnvironmentTree runnerEnvironmentTree : environmentTree.getChildren()) {
            collectRunnerEnvironments(runnerEnvironmentTree);
        }
    }

    @Override
    public void selectRunnerEnvironment(String environmentId) {
        if (environmentMap.containsKey(environmentId)) {
            tree.getSelectionModel().selectSingleNode(environmentMap.get(environmentId));
            delegate.environmentSelected(environmentMap.get(environmentId));
        }
    }

    interface SelectRunnerViewImplUiBinder
            extends UiBinder<DockLayoutPanel, SelectRunnerPageViewImpl> {
    }

    class RunnersRenderer implements NodeRenderer<Object> {

        @Override
        public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
            return null;
        }

        @Override
        public SpanElement renderNodeContents(Object data) {
            SpanElement divElement = Elements.createSpanElement();
            if (data instanceof RunnerEnvironmentTree) {
                divElement.setInnerText(((RunnerEnvironmentTree)data).getDisplayName());
            } else if (data instanceof RunnerEnvironment) {
                divElement.setInnerText(((RunnerEnvironment)data).getDisplayName());
            }

            return divElement;
        }

        @Override
        public void updateNodeContents(TreeNodeElement<Object> treeNode) {

        }
    }
}