/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Models a wildcard type declaration.
 *
 * @see java.lang.reflect.WildcardType
 *
 * @author Steve Ebersole
 */
public interface WildcardTypeDetails extends TypeDetails {
	@Nullable
	TypeDetails getBound();

	boolean isExtends();

	@Nullable
	TypeDetails getExtendsBound();

	@Nullable
	TypeDetails getSuperBound();

	@Nonnull
	@Override
	default Kind getTypeKind() {
		return Kind.WILDCARD_TYPE;
	}

	@Nonnull
	@Override
	default WildcardTypeDetails asWildcardType() {
		return this;
	}
}
