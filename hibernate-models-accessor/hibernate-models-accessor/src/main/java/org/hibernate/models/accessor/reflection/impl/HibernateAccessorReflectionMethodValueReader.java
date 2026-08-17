/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.reflection.impl;

import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class HibernateAccessorReflectionMethodValueReader<T> implements HibernateAccessorValueReader<T> {

	private final Method method;

	public HibernateAccessorReflectionMethodValueReader(Method getter) {
		this.method = getter;
		method.setAccessible(true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object instance) {
		try {
			return (T) method.invoke(instance);
		}
		catch (RuntimeException | IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorInvokingMember(method, Objects.toString(instance), e, e.getMessage());
		}
		catch (InvocationTargetException e) {
			Throwable thrown = e.getCause();
			if (thrown instanceof Error) {
				throw (Error) thrown;
			}
			else {
				throw CoreLog.INSTANCE.errorInvokingMember(method, Objects.toString(instance), thrown, thrown.getMessage());
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + method + "]";
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}
		HibernateAccessorReflectionMethodValueReader<?> other = (HibernateAccessorReflectionMethodValueReader<?>) obj;
		return method.equals(other.method);
	}
}
