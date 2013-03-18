/*******************************************************************************
 * Copyright (C) 2009, 2012 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import org.bonitasoft.engine.api.DebugAPI;
import org.bonitasoft.engine.api.DocumentAPI;

/**
 * @author Matthieu Chaffotte
 */
public interface ProcessAPI extends org.bonitasoft.engine.api.ProcessAPI, ProcessManagementAPI, ProcessRuntimeAPI, DocumentAPI, DebugAPI {

}