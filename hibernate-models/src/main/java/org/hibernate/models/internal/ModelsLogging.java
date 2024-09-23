/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.jboss.logging.Logger;

/**
 * Base logging for hibernate-models
 *
 * @author Steve Ebersole
 */
public interface ModelsLogging {
	String NAME = "org.hibernate.models";

	Logger MODELS_LOGGER = Logger.getLogger( NAME );
}
