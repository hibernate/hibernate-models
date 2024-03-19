/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

/**
 * @author Steve Ebersole
 */
public record WildcardTypeDetailsImpl(TypeDetails bound, boolean isExtends) implements WildcardTypeDetails {

	/**
	 * Details about the type bound to the wildcard
	 */
	@Override
	public TypeDetails getBound() {
		return bound;
	}

	/**
	 * Whether the {@linkplain #bound() bound} is an extends - i.e. {@code ? extends Something}.
	 * False would imply a super - i.e. {@code ? super Something}.
	 */
	@Override
	public boolean isExtends() {
		return isExtends;
	}

	/**
	 * Returns the upper bound of this wildcard (e.g. {@code SomeType} for {@code ? extends SomeType}).
	 * <p>
	 * Returns {@code java.lang.Object} if this wildcard declares a lower bound
	 * ({@code ? super SomeType}).
	 *
	 * @return the upper bound, or {@code Object} if this wildcard has a lower bound
	 */
	@Override public TypeDetails getExtendsBound() {
		return isExtends ? bound : ClassTypeDetails.OBJECT_TYPE_DETAILS;
	}

	/**
	 * Returns the lower bound of this wildcard (e.g. {@code SomeType} for {@code ? super SomeType}).
	 * <p>
	 * Returns {@code null} if this wildcard declares an upper bound
	 * ({@code ? extends SomeType}).
	 *
	 * @return the lower bound, or {@code null} if this wildcard has an upper bound
	 */
	@Override public TypeDetails getSuperBound() {
		return isExtends ? null : bound;
	}

	@Override
	public String getName() {
		if ( isExtends && bound != null ) {
			return bound.getName();
		}
		return Object.class.getName();
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		if ( bound == null ) {
			return Object.class == checkType;
		}
		return getExtendsBound().isImplementor( checkType );
	}

	@Override
	public TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
		return null;
	}
}
