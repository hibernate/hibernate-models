/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.bytebuddy.spi;

public interface HibernateAccessorByteBuddyBulkAccessor {

	Object readByField(Object instance, int index);

	void writeByField(Object instance, int index, Object value);

	Object readByMethod(Object instance, int index);

	void writeByMethod(Object instance, int index, Object value);

	Object newInstance(int index, Object[] args);
}
