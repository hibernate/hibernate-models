/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.bytebuddy.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;

record HibernateAccessorByteBuddyInstantiator<T>(HibernateAccessorByteBuddyBulkAccessor accessor, int index) implements HibernateAccessorInstantiator<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T create(Object... args) {
		return (T) accessor.newInstance(index, args);
	}
}
