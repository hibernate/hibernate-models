/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * Models a Java array type.
 *
 * @author Steve Ebersole
 */
public interface ArrayTypeDetails extends ClassBasedTypeDetails {
	ClassDetails getArrayClassDetails();

	@Override
	default ClassDetails getClassDetails() {
		return getArrayClassDetails();
	}

	TypeDetails getConstituentType();

	int getDimensions();

	@Override
	default ArrayTypeDetails asArrayType() {
		return this;
	}

	@Override
	default Kind getTypeKind() {
		return Kind.ARRAY;
	}


}
