/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;

/**
 * @author Steve Ebersole
 */
public interface MutableClassDetailsRegistry extends ClassDetailsRegistry {
	/**
	 * Adds a managed-class descriptor using its {@linkplain ClassDetails#getName() name}
	 * as the registration key.
	 */
	void addClassDetails(@Nonnull ClassDetails classDetails);

	/**
	 * Adds a managed-class descriptor using the given {@code name} as the registration key
	 */
	void addClassDetails(@Nonnull String name, @Nonnull ClassDetails classDetails);

	/**
	 * Resolve (find or create) ClassDetails by name.  If there is currently no
	 * such registration, one is created using the specified {@code creator}.
	 *
	 * @return The resolved details, or {@code null} for {@code "void"}, without invoking the creator
	 */
	@Nullable
	ClassDetails resolveClassDetails(@Nonnull String name, @Nonnull ClassDetailsCreator creator);

	/**
	 * Create a CLass Details
	 */
	@FunctionalInterface
	interface  ClassDetailsCreator {
		/**
		 * @throws UnknownClassException
		 */
		@Nonnull
		ClassDetails createClassDetails(@Nonnull String name) throws UnknownClassException;
	}
}
