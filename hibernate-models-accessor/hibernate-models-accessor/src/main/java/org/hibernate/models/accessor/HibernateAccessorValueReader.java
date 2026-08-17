/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Reads a value from a field or getter method on an object instance.
 *
 * <p>Obtain an instance via {@link HibernateAccessorFactory#valueReader(java.lang.reflect.Field)}
 * or {@link HibernateAccessorFactory#valueReader(java.lang.reflect.Method)}.
 *
 * @param <T> the type of the value being read
 */
public interface HibernateAccessorValueReader<T> {

	/**
	 * Reads the value from the given object instance.
	 *
	 * @param instance the object to read from
	 * @return the value read from the instance
	 * @throws HibernateAccessorException if the read operation fails
	 */
	T get(Object instance);
}
