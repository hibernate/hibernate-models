/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.methodhandle.impl;

import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueReader;

public class HibernateAccessorMethodHandleMultiValueReader implements HibernateAccessorMultiValueReader {

	private final HibernateAccessorValueReader<?>[] readers;

	public HibernateAccessorMethodHandleMultiValueReader(HibernateAccessorValueReader<?>[] readers) {
		this.readers = readers;
	}

	@Override
	public Object[] get(Object instance) {
		final Object[] values = new Object[readers.length];
		for ( int i = 0; i < readers.length; i++ ) {
			values[i] = readers[i].get( instance );
		}
		return values;
	}
}
