/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Models a wildcard type declaration.
 *
 * @see java.lang.reflect.WildcardType
 *
 * @author Steve Ebersole
 */
public interface WildcardTypeDetails extends TypeDetails {
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
