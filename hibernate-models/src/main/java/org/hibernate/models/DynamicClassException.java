/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.SourceModelContext;

/**
 * Generally indicates an attempt to resolve a {@linkplain ClassDetails}
 * into the corresponding Java {@linkplain Class} when the ClassDetails does not
 * specify a {@linkplain ClassDetails#getClassName() class name}.
 *
 * @see ClassDetails#toJavaClass()
 * @see ClassDetails#toJavaClass(ClassLoading, SourceModelContext)
 *
 * @author Steve Ebersole
 */
public class DynamicClassException extends ModelsException {
	public DynamicClassException(String message) {
		super( message );
	}

	public DynamicClassException(String message, Throwable cause) {
		super( message, cause );
	}
}
