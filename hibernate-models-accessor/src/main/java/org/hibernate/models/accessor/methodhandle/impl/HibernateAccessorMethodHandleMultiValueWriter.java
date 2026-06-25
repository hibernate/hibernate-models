/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.methodhandle.impl;

import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;

public class HibernateAccessorMethodHandleMultiValueWriter implements HibernateAccessorMultiValueWriter {

	private final HibernateAccessorValueWriter[] writers;

	public HibernateAccessorMethodHandleMultiValueWriter(HibernateAccessorValueWriter[] writers) {
		this.writers = writers;
	}

	@Override
	public void set(Object instance, Object[] values) {
		for ( int i = 0; i < writers.length; i++ ) {
			writers[i].set( instance, values[i] );
		}
	}
}
