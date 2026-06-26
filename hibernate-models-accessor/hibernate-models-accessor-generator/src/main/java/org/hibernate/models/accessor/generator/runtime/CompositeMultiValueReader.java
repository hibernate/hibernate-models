/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.runtime;

import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueReader;

public class CompositeMultiValueReader implements HibernateAccessorMultiValueReader {

	private final HibernateAccessorValueReader<?>[] readers;

	public CompositeMultiValueReader(HibernateAccessorValueReader<?>[] readers) {
		this.readers = readers;
	}

	@Override
	public Object[] get(Object instance) {
		Object[] values = new Object[readers.length];
		for ( int i = 0; i < readers.length; i++ ) {
			values[i] = readers[i].get( instance );
		}
		return values;
	}
}
