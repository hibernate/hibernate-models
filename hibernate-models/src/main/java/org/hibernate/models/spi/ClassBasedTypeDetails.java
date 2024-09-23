/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.hibernate.models.internal.ClassTypeDetailsImpl;

/**
 * Types which are expressible as a {@linkplain #getClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface ClassBasedTypeDetails extends TypeDetails {
	/**
	 * Type details for Object
	 */
	ClassTypeDetails OBJECT_TYPE_DETAILS = new ClassTypeDetailsImpl( ClassDetails.OBJECT_CLASS_DETAILS, Kind.CLASS );

	/**
	 * Details for {@code Class.class}
	 */
	TypeDetails CLASS_TYPE_DETAILS = new ClassTypeDetailsImpl( ClassDetails.CLASS_CLASS_DETAILS, Kind.CLASS );

	/**
	 * Details for {@code void.class}
	 */
	TypeDetails VOID_TYPE_DETAILS = new ClassTypeDetailsImpl( ClassDetails.VOID_CLASS_DETAILS, Kind.VOID );

	/**
	 * Details for {@code Void.class}
	 */
	TypeDetails VOID_OBJECT_TYPE_DETAILS = new ClassTypeDetailsImpl( ClassDetails.VOID_CLASS_DETAILS, Kind.VOID );

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
