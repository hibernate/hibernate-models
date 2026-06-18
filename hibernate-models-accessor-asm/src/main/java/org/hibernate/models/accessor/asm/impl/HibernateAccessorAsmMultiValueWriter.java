/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;

class HibernateAccessorAsmMultiValueWriter implements HibernateAccessorMultiValueWriter {

	private final HibernateAccessorAsmBulkAccessor accessor;
	private final boolean[] isField;
	private final int[] indices;

	HibernateAccessorAsmMultiValueWriter(HibernateAccessorAsmBulkAccessor accessor, boolean[] isField, int[] indices) {
		this.accessor = accessor;
		this.isField = isField;
		this.indices = indices;
	}

	@Override
	public void set(Object instance, Object[] values) {
		for ( int i = 0; i < indices.length; i++ ) {
			if ( isField[i] ) {
				accessor.writeByField( instance, indices[i], values[i] );
			}
			else {
				accessor.writeByMethod( instance, indices[i], values[i] );
			}
		}
	}
}
