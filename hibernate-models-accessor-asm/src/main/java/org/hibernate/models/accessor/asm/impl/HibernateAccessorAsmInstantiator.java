/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;

record HibernateAccessorAsmInstantiator<T>(HibernateAccessorAsmBulkAccessor accessor, int index) implements HibernateAccessorInstantiator<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T create(Object... args) {
		return (T) accessor.newInstance(index, args);
	}
}
