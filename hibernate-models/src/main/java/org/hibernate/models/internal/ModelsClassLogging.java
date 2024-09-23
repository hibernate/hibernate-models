/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.jboss.logging.Logger;

/**
 * Logging related to {@link org.hibernate.models.spi.ClassDetailsRegistry}
 *
 * @author Steve Ebersole
 */
public interface ModelsClassLogging {
	String NAME = ModelsLogging.NAME + ".classes";

	Logger MODELS_CLASS_LOGGER = Logger.getLogger( NAME );
}
