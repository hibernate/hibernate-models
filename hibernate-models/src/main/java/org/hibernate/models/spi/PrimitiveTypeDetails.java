/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

import org.hibernate.models.internal.PrimitiveKind;

/**
 * Models a primitive type
 *
 * @see Class#isPrimitive
 *
 * @author Steve Ebersole
 */
public interface PrimitiveTypeDetails extends ClassBasedTypeDetails {
	@Nonnull
	ClassDetails getClassDetails();

	char toCode();

	@Nonnull
	@Override
	Kind getTypeKind();

	@Nonnull
	PrimitiveKind getPrimitiveKind();

	@Nonnull
	@Override
	PrimitiveTypeDetails asPrimitiveType();
}
