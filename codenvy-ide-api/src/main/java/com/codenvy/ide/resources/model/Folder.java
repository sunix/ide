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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.marshal.JSONDeserializer;
import com.google.gwt.json.client.JSONObject;


/**
 * Represents the folder containing {@link Resource}s.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class Folder extends Resource {
    public static final String FOLDER_MIME_TYPE = "text/directory";

    public static final String TYPE = "folder";

    private JsonArray<Resource> children = JsonCollections.<Resource>createArray();

    /**
     * Empty instance of Folder.
     * Not intended to be used by clients.
     */
    protected Folder() {
        this(TYPE, FOLDER_MIME_TYPE);
    }

    /** For subclassing */
    protected Folder(String itemType, String mimeType) {
        super(itemType);
        this.mimeType = mimeType;
    }

    public Folder(JSONObject itemObject) {
        this();
        init(itemObject);
    }

    public void init(JSONObject itemObject) {
        id = itemObject.get("id").isString().stringValue();
        name = itemObject.get("name").isString().stringValue();
        if (itemObject.get("mimeType").isString() != null) {
            mimeType = itemObject.get("mimeType").isString().stringValue();
        }
        //path = itemObject.get("path").isString().stringValue();
        // no longer exists
        //      parentId =
        //         (itemObject.get("parentId").isNull() != null) ? null : itemObject.get("parentId").isString().stringValue();
        creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
        links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
    }

    public String createPath(String childName) {
        return getPath() + "/" + childName;
    }

    /** @return the children */
    public JsonArray<Resource> getChildren() {
        return children;
    }

    /**
     * @param children
     *         the children to set
     */
    public void setChildren(JsonArray<Resource> children) {
        this.children = children;
    }

    /**
     * Recursively looks for the Resource
     *
     * @param id
     * @return resource or null if not found
     */
    public Resource findResourceById(String id) {
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.getId().equals(id)) {
                return child;
            }

            if (child instanceof Folder) {
                Resource resourceById = ((Folder)child).findResourceById(id);
                if (resourceById != null) {
                    return resourceById;
                }
            }
        }
        return null;
    }

    /**
     * Looks for the Child Resource, without recursive calls.
     *
     * @param id
     * @return resource or null if not found
     */
    public Resource findChildById(String id) {
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.getId().equals(id)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Recursively looks for the Resource
     *
     * @param name
     * @param type
     * @return resource or null if not found
     */
    public Resource findResourceByName(String name, String type) {
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.getName().equals(name) && child.getResourceType().equals(type)) {
                return child;
            }

            if (child instanceof Folder) {
                Resource resourceById = ((Folder)child).findResourceByName(name, type);
                if (resourceById != null) {
                    return resourceById;
                }
            }
        }
        return null;
    }

    /**
     * Internal add to list.
     * Sets proper parent
     *
     * @param resource
     */
    public void addChild(Resource resource) {
        children.add(resource);
        resource.setParent(this);
    }

    /**
     * Internal remove from list
     *
     * @param resource
     */
    public void removeChild(Resource resource) {
        children.remove(resource);
        resource.setParent(null);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFile() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFolder() {
        return true;
    }
}