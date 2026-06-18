/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;

class HibernateAccessorAsmMultiValueReader implements HibernateAccessorMultiValueReader {

	private final HibernateAccessorAsmBulkAccessor accessor;
	private final boolean[] isField;
	private final int[] indices;

	HibernateAccessorAsmMultiValueReader(HibernateAccessorAsmBulkAccessor accessor, boolean[] isField, int[] indices) {
		this.accessor = accessor;
		this.isField = isField;
		this.indices = indices;
	}

	@Override
	public Object[] get(Object instance) {
		final Object[] values = new Object[indices.length];
		for ( int i = 0; i < indices.length; i++ ) {
			values[i] = isField[i]
					? accessor.readByField( instance, indices[i] )
					: accessor.readByMethod( instance, indices[i] );
		}
		return values;
	}
}
