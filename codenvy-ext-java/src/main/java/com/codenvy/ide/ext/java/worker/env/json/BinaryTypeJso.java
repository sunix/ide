/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.worker.env.json;

import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.js.JsoArray;

/**
 * @author Evgen Vidolob
 */
public class BinaryTypeJso extends Jso {

    protected BinaryTypeJso() {
    }

    public final native JsoArray<AnnotationJso> getAnnotations() /*-{
            return this["annotations"];
    }-*/;

    public final native String getEnclosingMethod() /*-{
        return this["enclosingMethod"];
    }-*/;

    public final native String getEnclosingTypeName() /*-{
        return this["enclosingTypeName"];
    }-*/;

    public final native JsoArray<FieldJso> getFields() /*-{
        return this["fields"];
    }-*/;

    public final native String getGenericSignature() /*-{
        return this["genericSignature"];
    }-*/;

    public final native JsoArray<String> getInterfaceNames() /*-{
        return this["interfaceNames"];
    }-*/;

    public final native JsoArray<MemberTypesJso> getMemberTypes() /*-{
        return this["memberTypes"];
    }-*/;

    public final native JsoArray<MethodJso> getMethods() /*-{
        return this["methods"]
    }-*/;

    public final native JsoArray<JsoArray<String>> getMissingTypeNames() /*-{
        return this["missingTypeNames"];
    }-*/;

    public final native String getName() /*-{
        return this["name"];
    }-*/;

    public final native String getSourceName() /*-{
        return this["sourceName"];
    }-*/;

    public final native String getSuperclassName() /*-{
        return this["superclassName"];
    }-*/;

    public final native String getTagBits() /*-{
        return this["tagBits"];
    }-*/;

    public final native boolean isAnonymous() /*-{
        return this["anonymous"];
    }-*/;

    public final native boolean isLocal() /*-{
        return this["local"];
    }-*/;

    public final native boolean isMember() /*-{
        return this["member"];
    }-*/;

    public final native String getSourceFileName() /*-{
        return this["sourceFileName"];
    }-*/;

    public final native int getModifiers() /*-{
        return this["modifiers"];
    }-*/;

    public final native boolean isBinaryType() /*-{
        return this["binaryType"];
    }-*/;

    public final native String getFileName() /*-{
        return this["fileName"];
    }-*/;

}
