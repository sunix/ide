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
package com.codenvy.ide.api.selection;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;

/**
 * This class contains a single selected object or the bulk of selected objects.
 * Selection can contain any type of Objects and any number of them.
 * <br/>
 * Single selection can be created using {@link Selection#Selection(Object)} constructor
 * that accept one Object.
 * <br/>
 * Multiselection can be created with the help of {@link Selection#Selection(com.codenvy.ide.collections.Array)}.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class Selection<T> {
    Array<T> elements;

    /** Creates an empty selection */
    public Selection() {
        this(Collections.<T>createArray());
    }

    /**
     * Creates SingleSelection, with only one item in it.
     *
     * @param item
     *         actual Selected object
     */
    public Selection(T item) {
        this();
        elements.add(item);
    }

    /**
     * Creates a MultiSelection, with the list of objects. <br/>
     * Please note, if list contains zero elements, Selection is considered as empty,
     * if single element placed in the list, the Selection is considered as SingleSelection.
     *
     * @param list
     */
    public Selection(Array<T> list) {
        elements = list;
    }

    /**
     * @return The first element in MultiSelection, the only element in SingleSelection
     *         and null otherwise.
     */
    public T getFirstElement() {
        return elements.isEmpty() ? null : elements.get(0);
    }

    /** @return <code>true</code> if Selection is empty. */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /** @return <code>true</code> if Selection contains only one element. */
    public boolean isSingleSelection() {
        return elements.size() == 1;
    }

    /** @return <code>true</code> if Selection contains multiple elements. */
    public boolean isMultiSelection() {
        return elements.size() > 1;
    }

    /** @return the copy of Selection. */
    public Array<T> getAll() {
        Array<T> copy = Collections.createArray();
        copy.addAll(elements);
        return copy;
    }

}
