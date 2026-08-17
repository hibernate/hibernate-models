/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.asm.spi.HibernateAccessorAsmBulkAccessor;

record HibernateAccessorAsmFieldValueWriter(HibernateAccessorAsmBulkAccessor accessor, int index) implements HibernateAccessorValueWriter {

	@Override
	public void set(Object instance, Object value) {
		accessor.writeByField(instance, index, value);
	}
}
