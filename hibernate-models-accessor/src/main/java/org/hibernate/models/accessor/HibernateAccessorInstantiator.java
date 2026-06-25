/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Creates new instances of a type by invoking a constructor.
 *
 * <p>Obtain an instance via {@link HibernateAccessorFactory#instantiator(java.lang.reflect.Constructor)}.
 *
 * @param <T> the type this instantiator creates
 */
public interface HibernateAccessorInstantiator<T> {

	/**
	 * Creates a new instance by invoking the underlying constructor with the given arguments.
	 *
	 * @param args the constructor arguments
	 * @return a new instance of {@code T}
	 * @throws HibernateAccessorException if instantiation fails
	 */
	T create(Object... args);
}
