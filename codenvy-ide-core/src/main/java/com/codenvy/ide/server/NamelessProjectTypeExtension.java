/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

/** @author Artem Zatsarynnyy */
@Singleton
public class NamelessProjectTypeExtension implements ProjectTypeExtension {

    @Inject
    public NamelessProjectTypeExtension(ProjectTypeDescriptionRegistry registry) {
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType(Constants.NAMELESS_ID, Constants.NAMELESS_NAME, Constants.NAMELESS_CATEGORY);
    }

    @Override
    public List<Attribute> getPredefinedAttributes() {
        return Collections.singletonList(new Attribute(Constants.LANGUAGE, "nameless"));
    }

    @Override
    public List<ProjectTemplateDescription> getTemplates() {
        return Collections.emptyList();
    }
}
