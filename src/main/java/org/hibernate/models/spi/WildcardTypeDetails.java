/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import org.hibernate.models.internal.WildcardTypeDetailsImpl;

/**
 * Models a wildcard type declaration.
 *
 * @author Steve Ebersole
 */
public interface WildcardTypeDetails extends TypeDetails {
	/**
	 * A wildcard without a bound.  In other words, {@code ?}.
	 */
	WildcardTypeDetails UNBOUNDED = new WildcardTypeDetailsImpl( null, true );

	TypeDetails getBound();

	boolean isExtends();

	TypeDetails getExtendsBound();

	TypeDetails getSuperBound();

	@Override
	default Kind getTypeKind() {
		return Kind.WILDCARD_TYPE;
	}

	@Override
	default WildcardTypeDetails asWildcardType() {
		return this;
	}
}
