/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorValueWriter;

record HibernateAccessorAsmMethodValueWriter(HibernateAccessorAsmBulkAccessor accessor, int index) implements HibernateAccessorValueWriter {

	@Override
	public void set(Object instance, Object value) {
		accessor.writeByMethod(instance, index, value);
	}
}
