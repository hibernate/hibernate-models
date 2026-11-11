/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

/**
 * Types which are expressible as a {@linkplain #getClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface ClassBasedTypeDetails extends TypeDetails {
	@Nonnull
	ClassDetails getClassDetails();

	@Nonnull
	@Override
	default String getName() {
		return getClassDetails().getName();
	}

	@Override
	default boolean isImplementor(@Nonnull Class<?> checkType) {
		return getClassDetails().isImplementor( checkType );
	}
}
