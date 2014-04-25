/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.core;

import com.codenvy.ide.api.ui.IconRegistry;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vitaly Parfonov
 */
@Singleton
public class IconRegistryImpl implements IconRegistry {

    private Map<String, String> icons = new HashMap<>();
    
    private Map<String, SVGResource> svgIcons = new HashMap<>();

    @Override
    public Image getIcon(String iconId) {
        Image iconImage = getIconIfExist(iconId);
        if (iconImage == null) {
            String pref = iconId.split("\\.")[0];
            String defIconId = iconId.replaceFirst(pref, "default");
            if (icons.containsKey(defIconId)) {
                String url = GWT.getModuleBaseForStaticFiles() + icons.get(defIconId);
                return new Image(url);
            } else {
                return getDefaultIcon();
            }
        } else {
            return iconImage;
        }
    }

    @Override
    public Image getIconIfExist(String iconId) {
        if (icons.containsKey(iconId)) {
            String url = GWT.getModuleBaseForStaticFiles() + icons.get(iconId);
            return new Image(url);
        } else {
            return null;
        }
    }
    
    @Override
    public SVGImage getSVGIconIfExist(String iconId) {
        if (svgIcons.containsKey(iconId)) {
            return new SVGImage(svgIcons.get(iconId));
        } else {
            return null;
        }
    }

    @Override
    public Image getDefaultIcon() {
        return new Image(GWT.getModuleBaseForStaticFiles() + "default/default.jpg");
    }

    @Override
    public void registerIcon(String iconId, String iconPath) {
        icons.put(iconId, iconPath);
    }

    /** {@inheritDoc} */
    @Override
    public SVGImage getSVGIcon(String iconId) {
        if (svgIcons.containsKey(iconId)) {
            return new SVGImage(svgIcons.get(iconId));
        } else {
            String pref = iconId.split("\\.")[0];
            String defIconId = iconId.replaceFirst(pref, "default");
            if (svgIcons.containsKey(defIconId)) {
                return new SVGImage(svgIcons.get(defIconId));
            } else {
                return getDefaultSVGIcon();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public SVGImage getDefaultSVGIcon() {
        return new SVGImage(svgIcons.get("default"));
    }

    /** {@inheritDoc} */
    @Override
    public void registerSVGIcon(String iconId, SVGResource resource) {
        svgIcons.put(iconId, resource);
    }
}
