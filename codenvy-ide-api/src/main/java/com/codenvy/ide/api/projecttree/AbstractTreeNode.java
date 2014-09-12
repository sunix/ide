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
package com.codenvy.ide.api.projecttree;

import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.projecttree.generic.ProjectRootNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nonnull;

/**
 * An <code>AbstractTreeNode</code> is a super-class for all implementation of nodes in a project tree.
 * An <code>AbstractTreeNode</code> may also hold a reference to an associated object,
 * the use of which is left to the user.
 *
 * @param <T>
 *         the type of the associated data
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractTreeNode<T> {
    protected AbstractTreeNode<?>                  parent;
    protected T                                    data;
    protected Array<AbstractTreeNode<?>>           children;
    protected EventBus                             eventBus;
    private   SVGImage                             icon;
    private   TreeNodeElement<AbstractTreeNode<?>> treeNodeElement;

    /**
     * Creates new node with the specified parent, associated data and display name.
     *
     * @param parent
     *         parent node
     * @param data
     *         an object this node encapsulates
     * @param eventBus
     */
    public AbstractTreeNode(AbstractTreeNode<?> parent, T data, EventBus eventBus) {
        this.parent = parent;
        this.data = data;
        this.eventBus = eventBus;
        children = Collections.createArray();
    }

    /**
     * Returns this node's parent node.
     *
     * @return this node's parent node
     */
    public AbstractTreeNode<?> getParent() {
        return parent;
    }

    /**
     * Sets the new parent node for this node.
     *
     * @param parent
     *         the new parent node
     */
    public void setParent(AbstractTreeNode<?> parent) {
        this.parent = parent;
    }

    /**
     * Returns the object represented by this node                                   .
     *
     * @return the associated data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the new associated data for this node.
     *
     * @param data
     *         the new associated data
     */
    public void setData(T data) {
        this.data = data;
    }

    /** Returns project which contains this node. */
    public ProjectRootNode getProject() {
        AbstractTreeNode<?> parent = getParent();
        while (!(parent instanceof ProjectRootNode)) {
            parent = parent.getParent();
        }
        return (ProjectRootNode)parent;
    }

    /** Returns the node's display name. */
    @Nonnull
    public abstract String getDisplayName();

    /** Provides an SVG icon to be used for graphical representation of the node. */
    public SVGImage getDisplayIcon() {
        return icon;
    }

    /** Set an SVG icon to be used for graphical representation of the node. */
    public void setDisplayIcon(SVGImage icon) {
        this.icon = icon;
    }

    /**
     * Determines may the node be expanded.
     *
     * @return <code>true</code> - if node shouldn't never be expanded in the tree,
     * <code>false</code> - if node may be expanded
     */
    public abstract boolean isLeaf();

    /**
     * Returns an array of all this node's child nodes. The array will always
     * exist (i.e. never <code>null</code>) and be of length zero if this is
     * a leaf node.
     *
     * @return an array of all this node's child nodes
     */
    @Nonnull
    public Array<AbstractTreeNode<?>> getChildren() {
        return children;
    }

    /**
     * Set node's children.
     *
     * @param children
     *         array of new children for this node
     */
    public void setChildren(Array<AbstractTreeNode<?>> children) {
        this.children = children;
    }

    /**
     * Refresh node's children.
     *
     * @param callback
     *         callback to return node with refreshed children
     */
    public abstract void refreshChildren(AsyncCallback<AbstractTreeNode<?>> callback);

    /** Process an action on the node (e.g. double-click on the node in the view). */
    public void processNodeAction() {
    }

    /** Defines whether the node may be renamed. */
    public boolean isRenamable() {
        return false;
    }

    /**
     * Override this method to provide a way to rename node.
     * <p/>
     * Sub-classes should invoke {@code super.delete} at the end of this method.
     *
     * @param newName
     *         new name
     */
    public void rename(String newName) {
        eventBus.fireEvent(NodeChangedEvent.createNodeRenamedEvent(this));
    }

    /** Defines whether the node may be deleted. */
    public boolean isDeletable() {
        return false;
    }

    /**
     * Override this method to provide a way to delete node.
     * <p/>
     * Sub-classes should invoke {@code super.delete} at the end of this method.
     */
    public void delete() {
        if (parent != null) {
            parent.getChildren().remove(this);
            eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent(parent));
        }
        // do not reset parent in order to know which parent this node belonged to before deleting
    }

    /**
     * Returns the rendered {@link com.codenvy.ide.ui.tree.TreeNodeElement} that is a representation of node.
     * <p/>
     * Used internally and not intended to be used directly.
     *
     * @return the rendered {@link com.codenvy.ide.ui.tree.TreeNodeElement}
     */
    public TreeNodeElement<AbstractTreeNode<?>> getTreeNodeElement() {
        return treeNodeElement;
    }

    /**
     * Sets the rendered {@link TreeNodeElement} that is a representation of node.
     * <p/>
     * Used internally and not intended to be used directly.
     *
     * @param treeNodeElement
     *         the rendered {@link TreeNodeElement}
     */
    public void setTreeNodeElement(TreeNodeElement<AbstractTreeNode<?>> treeNodeElement) {
        this.treeNodeElement = treeNodeElement;
    }
}
