/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.reflection.impl;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HibernateAccessorReflectionFactory implements HibernateAccessorFactory {

	public static final HibernateAccessorReflectionFactory INSTANCE = new HibernateAccessorReflectionFactory();

	private HibernateAccessorReflectionFactory() {
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		return new HibernateAccessorReflectionConstructorInstantiator<>(constructor);
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		return new HibernateAccessorReflectionFieldValueReader<>(field);
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		return new HibernateAccessorReflectionMethodValueReader<>(method);
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		return new HibernateAccessorReflectionFieldValueWriter(field);
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		return new HibernateAccessorReflectionMethodValueWriter(setter);
	}

}
