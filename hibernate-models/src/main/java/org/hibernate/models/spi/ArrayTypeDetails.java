/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

/**
 * Models a Java array type.
 *
 * @author Steve Ebersole
 */
public interface ArrayTypeDetails extends ClassBasedTypeDetails {
	@Nonnull
	ClassDetails getArrayClassDetails();

	@Nonnull
	@Override
	default ClassDetails getClassDetails() {
		return getArrayClassDetails();
	}

	@Nonnull
	TypeDetails getConstituentType();

	int getDimensions();

	@Nonnull
	@Override
	default ArrayTypeDetails asArrayType() {
		return this;
	}

	@Nonnull
	@Override
	default Kind getTypeKind() {
		return Kind.ARRAY;
	}

}
