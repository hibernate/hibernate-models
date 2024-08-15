/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import org.hibernate.models.internal.PrimitiveKind;

/**
 * Models a primitive type
 *
 * @see Class#isPrimitive
 * @see org.jboss.jandex.PrimitiveType
 *
 * @author Steve Ebersole
 */
public interface PrimitiveTypeDetails extends ClassBasedTypeDetails {
	ClassDetails getClassDetails();

	char toCode();

	@Override
	Kind getTypeKind();

	PrimitiveKind getPrimitiveKind();

	@Override
	PrimitiveTypeDetails asPrimitiveType();
}
