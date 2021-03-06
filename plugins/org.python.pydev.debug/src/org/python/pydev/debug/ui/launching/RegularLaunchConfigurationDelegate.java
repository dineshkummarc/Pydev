/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
/*
 * Author: atotic
 * Created: Aug 16, 2003
 */
package org.python.pydev.debug.ui.launching;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;


public class RegularLaunchConfigurationDelegate extends AbstractLaunchConfigurationDelegate{
    /**
     * @return
     */
    protected String getRunnerConfigRun(ILaunchConfiguration conf, String mode, ILaunch launch) {
        return PythonRunnerConfig.RUN_REGULAR;
    }

}
