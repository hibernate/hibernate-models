/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

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
}
