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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.ApplicationStatus;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.ide.api.CurrentProject;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author Vitaly Parfonov
 */
public class RunnerUtils {

    @Nullable
    public static RunnerMetric getRunnerMetric(@NotNull ApplicationProcessDescriptor processDescriptor, String metricName) {
        if (processDescriptor != null) {
            for (RunnerMetric runnerStat : processDescriptor.getRunStats()) {
                if (metricName.equals(runnerStat.getName())) {
                    return runnerStat;
                }
            }
        }
        return null;
    }


    /** Checking current project launched or not.
     * Return true if status @code ApplicationStatus.NEW or ApplicationStatus.RUNNING
     * otherwise false
     */
    public static boolean isAppLaunched(@NotNull CurrentProject currentProject) {
        ApplicationProcessDescriptor processDescriptor = currentProject.getProcessDescriptor();
        if (processDescriptor == null)
            return false;
        if (processDescriptor.getStatus().equals(ApplicationStatus.NEW) ||
            processDescriptor.getStatus().equals(ApplicationStatus.RUNNING))
            return true;
        return false;
    }

}