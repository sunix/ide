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
package com.codenvy.ide.ui;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author Vitaly Parfonov
 */
public interface Resources extends com.google.gwt.resources.client.ClientBundle {

    @Source("dialogs/ask_64.png")
    ImageResource ask64();

    @Source({"Styles.css", "com/codenvy/ide/api/ui/style.css"})
    Styles styles();
}
