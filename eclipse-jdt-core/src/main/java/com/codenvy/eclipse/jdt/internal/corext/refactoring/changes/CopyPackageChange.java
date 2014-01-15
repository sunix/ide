/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.changes;

import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.jdt.core.IPackageFragment;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.JavaModelException;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import com.codenvy.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import com.codenvy.eclipse.jdt.internal.corext.util.Messages;
import com.codenvy.eclipse.jdt.ui.JavaElementLabels;
import com.codenvy.eclipse.ltk.core.refactoring.Change;

public class CopyPackageChange extends PackageReorgChange {

    public CopyPackageChange(IPackageFragment pack, IPackageFragmentRoot dest, INewNameQuery nameQuery) {
        super(pack, dest, nameQuery);
    }

    @Override
    protected Change doPerformReorg(IProgressMonitor pm) throws JavaModelException, OperationCanceledException {
        getPackage().copy(getDestination(), null, getNewName(), true, pm);
        return null;
    }

    @Override
    public String getName() {
        String packageName = JavaElementLabels.getElementLabel(getPackage(), JavaElementLabels.ALL_DEFAULT);
        String destinationName = JavaElementLabels.getElementLabel(getDestination(), JavaElementLabels.ALL_DEFAULT);
        return Messages.format(RefactoringCoreMessages.CopyPackageChange_copy,
                               new String[]{packageName, destinationName});
    }
}