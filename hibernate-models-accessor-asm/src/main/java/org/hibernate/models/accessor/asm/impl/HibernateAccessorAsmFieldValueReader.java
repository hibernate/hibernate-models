/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorValueReader;

record HibernateAccessorAsmFieldValueReader<T>(HibernateAccessorAsmBulkAccessor accessor, int index) implements HibernateAccessorValueReader<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object instance) {
		return (T) accessor.readByField(instance, index);
	}
}
