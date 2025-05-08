/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Types which are expressible as a {@linkplain #getClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface ClassBasedTypeDetails extends TypeDetails {
	ClassDetails getClassDetails();

	@Override
	default String getName() {
		return getClassDetails().getName();
	}

	@Override
	default boolean isImplementor(Class<?> checkType) {
		return getClassDetails().isImplementor( checkType );
	}
}
