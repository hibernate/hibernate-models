/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

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

	@Override
	PrimitiveTypeDetails asPrimitiveType();
}
