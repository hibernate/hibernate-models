/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.bytebuddy.impl;

import org.hibernate.models.accessor.HibernateAccessorValueReader;

record HibernateAccessorByteBuddyFieldValueReader<T>(HibernateAccessorByteBuddyBulkAccessor accessor, int index) implements HibernateAccessorValueReader<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object instance) {
		return (T) accessor.readByField(instance, index);
	}
}
