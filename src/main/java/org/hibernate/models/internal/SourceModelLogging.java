/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

/**
 * todo : find the proper min/max id range
 *
 * @author Steve Ebersole
 */
@MessageLogger( projectCode = "HHH" )
@ValidIdRange( min = 999901, max = 999999 )
public interface SourceModelLogging extends BasicLogger {
	String NAME = "org.hibernate.models.source";

	Logger SOURCE_MODEL_LOGGER = Logger.getLogger( NAME );
}
