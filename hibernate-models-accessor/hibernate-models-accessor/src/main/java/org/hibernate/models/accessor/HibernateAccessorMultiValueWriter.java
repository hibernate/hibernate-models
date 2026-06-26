/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Writes multiple values to an object instance in a single call.
 *
 * <p>Each element in the values array corresponds to one of the
 * {@link java.lang.reflect.Member members} (fields or methods) used
 * to create this writer, in the same order they were specified.
 *
 * <p>Obtain an instance via
 * {@link HibernateAccessorFactory#multiValueWriter(Class, java.lang.reflect.Member...)}.
 */
public interface HibernateAccessorMultiValueWriter {

	/**
	 * Sets all values on the given object instance.
	 *
	 * @param instance the object to write to
	 * @param values an array of values, one per member, in declaration order
	 * @throws HibernateAccessorException if any write operation fails
	 */
	void set(Object instance, Object[] values);
}
