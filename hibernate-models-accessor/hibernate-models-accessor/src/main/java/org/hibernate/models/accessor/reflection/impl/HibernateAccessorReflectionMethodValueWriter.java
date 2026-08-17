/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.reflection.impl;

import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class HibernateAccessorReflectionMethodValueWriter implements HibernateAccessorValueWriter {

	private final Method method;

	public HibernateAccessorReflectionMethodValueWriter(Method setter) {
		this.method = setter;
		method.setAccessible(true);
	}

	@Override
	public void set(Object instance, Object value) {
		try {
			method.invoke(instance, value);
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
		HibernateAccessorReflectionMethodValueWriter other = (HibernateAccessorReflectionMethodValueWriter) obj;
		return Objects.equals(method, other.method);
	}
}
