/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

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
	void addClassDetails(ClassDetails classDetails);

	/**
	 * Adds a managed-class descriptor using the given {@code name} as the registration key
	 */
	void addClassDetails(String name, ClassDetails classDetails);

	/**
	 * Resolve (find or create) ClassDetails by name.  If there is currently no
	 * such registration, one is created using the specified {@code creator}.
	 */
	ClassDetails resolveClassDetails(String name, ClassDetailsCreator creator);

	/**
	 * Create a CLass Details
	 */
	@FunctionalInterface
	interface  ClassDetailsCreator {
		/**
		 * @throws UnknownClassException
		 */
		ClassDetails createClassDetails(String name) throws UnknownClassException;
	}
}
