/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.cloudfoundry.client.control;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.update.UpdateInstancesEvent;

/**
 * Control to update number of instances of application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateInstancesControl.java Jul 18, 2011 10:19:37 AM vereshchaka $
 */
public class UpdateInstancesControl extends AbstractCloudFoundryControl {

    private static final String ID = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesControlId();

    private static final String TITLE = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesControlTitle();

    private static final String PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.updateInstancesControlPrompt();

    /**
     *
     */
    public UpdateInstancesControl(PAAS_PROVIDER paasProvider) {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudFoundryClientBundle.INSTANCE.appInstances(),
                  CloudFoundryClientBundle.INSTANCE.appInstancesDisabled());
        setEvent(new UpdateInstancesEvent(paasProvider));
    }

}