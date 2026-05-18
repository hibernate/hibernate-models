/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

public interface HibernateAccessorAsmBulkAccessor {

	Object readByField(Object instance, int index);

	void writeByField(Object instance, int index, Object value);

	Object readByMethod(Object instance, int index);

	void writeByMethod(Object instance, int index, Object value);

	Object newInstance(int index, Object[] args);
}
