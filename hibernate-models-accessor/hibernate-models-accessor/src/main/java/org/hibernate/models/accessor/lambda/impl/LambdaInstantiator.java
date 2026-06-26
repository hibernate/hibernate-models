/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.lambda.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.util.Arrays;

public class LambdaInstantiator<T> implements HibernateAccessorInstantiator<T> {
	private final MethodHandle handle;

	public LambdaInstantiator(MethodHandles.Lookup lookup, Constructor<T> constructor) {
		try {
			this.handle = lookup.unreflectConstructor(constructor)
					.asSpreader(Object[].class, constructor.getParameterCount());
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(constructor, e, e.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T create(Object... args) {
		try {
			return (T) handle.invoke(args);
		}
		catch (Throwable t) {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw CoreLog.INSTANCE.errorInvokingHandle(handle, Arrays.toString(args), t, t.getMessage());
		}
	}
}
