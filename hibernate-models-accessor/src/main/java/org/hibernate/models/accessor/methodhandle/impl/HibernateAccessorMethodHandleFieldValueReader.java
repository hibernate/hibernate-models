/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.methodhandle.impl;

import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.invoke.MethodHandle;

public class HibernateAccessorMethodHandleFieldValueReader<T> implements HibernateAccessorValueReader<T> {
	private final MethodHandle getter;

	public HibernateAccessorMethodHandleFieldValueReader(MethodHandle getter) {
		this.getter = getter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object instance) {
		try {
			return (T) getter.invoke(instance);
		}
		catch (Throwable t) {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw CoreLog.INSTANCE.errorInvokingHandle(getter, String.valueOf(instance), t, t.getMessage());
		}
	}
}
