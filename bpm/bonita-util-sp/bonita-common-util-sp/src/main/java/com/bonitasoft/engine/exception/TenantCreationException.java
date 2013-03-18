/*******************************************************************************
 * Copyright (C) 2009, 2012 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.exception;

import org.bonitasoft.engine.exception.BonitaException;

/**
 * @author Lu Kai
 * @author Matthieu Chaffotte
 */
public class TenantCreationException extends BonitaException {

    private static final long serialVersionUID = 3451245519674055831L;

    public TenantCreationException(final String message) {
        super(message);
    }

    public TenantCreationException(final Throwable cause) {
        super(cause);
    }

    public TenantCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}