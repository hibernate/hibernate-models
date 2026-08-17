/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Writes a value to a field or setter method on an object instance.
 *
 * <p>Obtain an instance via {@link HibernateAccessorFactory#valueWriter(java.lang.reflect.Field)}
 * or {@link HibernateAccessorFactory#valueWriter(java.lang.reflect.Method)}.
 */
public interface HibernateAccessorValueWriter {

	/**
	 * Sets the value on the given object instance.
	 *
	 * @param instance the object to write to
	 * @param value the value to set
	 * @throws HibernateAccessorException if the write operation fails
	 */
	void set(Object instance, Object value);
}
