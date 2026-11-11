/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.WildcardTypeDetailsImpl;

/**
 * Container for constant static {@link TypeDetails} references used often.
 */
public final class StandardTypeDetails {
	/**
	 * Type details for Object
	 */
	@Nonnull
	public static final ClassTypeDetails OBJECT_TYPE_DETAILS = new ClassTypeDetailsImpl(
			ClassDetails.OBJECT_CLASS_DETAILS,
			TypeDetails.Kind.CLASS
	);

	/**
	 * Details for {@code Class.class}
	 */
	@Nonnull
	public static final TypeDetails CLASS_TYPE_DETAILS = new ClassTypeDetailsImpl(
			ClassDetails.CLASS_CLASS_DETAILS,
			TypeDetails.Kind.CLASS
	);

	/**
	 * Details for {@code void.class}
	 */
	@Nonnull
	public static final TypeDetails VOID_TYPE_DETAILS = new ClassTypeDetailsImpl(
			ClassDetails.VOID_CLASS_DETAILS,
			TypeDetails.Kind.VOID
	);

	/**
	 * Details for {@code Void.class}
	 */
	@Nonnull
	public static final TypeDetails VOID_OBJECT_TYPE_DETAILS = new ClassTypeDetailsImpl(
			ClassDetails.VOID_CLASS_DETAILS,
			TypeDetails.Kind.VOID
	);

	/**
	 * A wildcard without a bound.  In other words, {@code ?}.
	 */
	WildcardTypeDetails UNBOUNDED = new WildcardTypeDetailsImpl( null, true );

	// restrict instantiation
	private StandardTypeDetails() {
	}
}
