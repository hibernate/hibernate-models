/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.reflection.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

public class HibernateAccessorReflectionConstructorInstantiator<T> implements HibernateAccessorInstantiator<T> {

	private final Constructor<T> constructor;

	public HibernateAccessorReflectionConstructorInstantiator(Constructor<T> constructor) {
		this.constructor = constructor;
		constructor.setAccessible(true);
	}

	@Override
	public T create(Object... args) {
		try {
			return constructor.newInstance(args);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw CoreLog.INSTANCE.errorInvokingMember(constructor, Arrays.toString(args), e, e.getMessage());
		}
		catch (InvocationTargetException e) {
			Throwable thrown = e.getCause();
			if (thrown instanceof Error) {
				throw (Error) thrown;
			}
			else {
				throw CoreLog.INSTANCE.errorInvokingMember(constructor, Arrays.toString(args), thrown, thrown.getMessage());
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + constructor + "]";
	}

	@Override
	public int hashCode() {
		return constructor.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}
		HibernateAccessorReflectionConstructorInstantiator<?> other = (HibernateAccessorReflectionConstructorInstantiator<?>) obj;
		return Objects.equals(constructor, other.constructor);
	}
}
