/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Reads multiple values from an object instance in a single call.
 *
 * <p>Each element in the returned array corresponds to one of the
 * {@link java.lang.reflect.Member members} (fields or methods) used
 * to create this reader, in the same order they were specified.
 *
 * <p>Obtain an instance via
 * {@link HibernateAccessorFactory#multiValueReader(java.lang.reflect.Member...)}.
 */
public interface HibernateAccessorMultiValueReader {

	/**
	 * Reads all values from the given object instance.
	 *
	 * @param instance the object to read from
	 * @return an array of values, one per member, in declaration order
	 * @throws HibernateAccessorException if any read operation fails
	 */
	Object[] get(Object instance);
}
