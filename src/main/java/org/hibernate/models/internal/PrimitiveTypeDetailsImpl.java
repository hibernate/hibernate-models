/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.PrimitiveTypeDetails;
import org.hibernate.models.spi.TypeDetails;

/**
 * @author Steve Ebersole
 */
public record PrimitiveTypeDetailsImpl(ClassDetails classDetails) implements PrimitiveTypeDetails {
	@Override
	public Kind getTypeKind() {
		return Kind.PRIMITIVE;
	}

	@Override
	public PrimitiveKind getPrimitiveKind() {
		return PrimitiveKind.resolveFromClassDetails( classDetails() );
	}

	@Override
	public PrimitiveTypeDetails asPrimitiveType() {
		return this;
	}

	@Override
	public TypeDetails resolveTypeVariable(String identifier) {
		return this;
	}

	@Override
	public ClassDetails getClassDetails() {
		return classDetails();
	}

	@Override public char toCode() {
		final Class<?> javaClass = classDetails.toJavaClass();
		if ( javaClass == byte.class ) {
			return 'B';
		}
		if ( javaClass == char.class ) {
			return 'C';
		}
		if ( javaClass == double.class ) {
			return 'D';
		}
		if ( javaClass == float.class ) {
			return 'F';
		}
		if ( javaClass == int.class ) {
			return 'I';
		}
		if ( javaClass == long.class ) {
			return 'J';
		}
		if ( javaClass == short.class ) {
			return 'S';
		}
		assert javaClass == boolean.class;
		return 'Z';
	}
}
