/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.jboss.logging.Logger;

/**
 * Logging related to {@link org.hibernate.models.spi.AnnotationDescriptorRegistry}
 *
 * @author Steve Ebersole
 */
public interface ModelsAnnotationLogging {
	String NAME = ModelsLogging.NAME + ".annotations";

	Logger MODELS_ANNOTATION_LOGGER = Logger.getLogger( NAME );
}
