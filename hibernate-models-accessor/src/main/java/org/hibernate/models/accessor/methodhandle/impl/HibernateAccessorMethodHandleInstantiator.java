/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.methodhandle.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;

public class HibernateAccessorMethodHandleInstantiator<T> implements HibernateAccessorInstantiator<T> {

	private final MethodHandle target;

	public HibernateAccessorMethodHandleInstantiator(MethodHandle target) {
		this.target = target;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T create(Object... args) {
		try {
			return (T) target.invokeWithArguments(args);
		}
		catch (Throwable t) {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw CoreLog.INSTANCE.errorInvokingHandle(target, Arrays.toString(args), t, t.getMessage());
		}
	}
}
