/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.methodhandle.impl;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class HibernateAccessorMethodHandleFactory implements HibernateAccessorFactory {

	private transient final MethodHandles.Lookup lookup;

	public HibernateAccessorMethodHandleFactory(MethodHandles.Lookup lookup) {
		this.lookup = lookup;
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		try {
			return new HibernateAccessorMethodHandleInstantiator<>(
					privateLookup(constructor.getDeclaringClass()).unreflectConstructor(constructor));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(constructor, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		try {
			return new HibernateAccessorMethodHandleFieldValueReader<>(
					privateLookup(field.getDeclaringClass()).unreflectGetter(field));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(field, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		try {
			return new HibernateAccessorMethodHandleMethodValueReader<>(
					privateLookup(method.getDeclaringClass()).unreflect(method));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(method, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		try {
			return new HibernateAccessorMethodHandleFieldValueWriter(
					privateLookup(field.getDeclaringClass()).unreflectSetter(field));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(field, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		try {
			return new HibernateAccessorMethodHandleMethodValueWriter(
					privateLookup(setter.getDeclaringClass()).unreflect(setter));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(setter, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorMultiValueReader multiValueReader(Member... members) {
		final HibernateAccessorValueReader<?>[] readers = new HibernateAccessorValueReader<?>[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			if ( member instanceof Field field ) {
				readers[i] = valueReader( field );
			}
			else if ( member instanceof Method method ) {
				readers[i] = valueReader( method );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return new HibernateAccessorMethodHandleMultiValueReader( readers );
	}

	@Override
	public HibernateAccessorMultiValueWriter multiValueWriter(Member... members) {
		final HibernateAccessorValueWriter[] writers = new HibernateAccessorValueWriter[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			if ( member instanceof Field field ) {
				writers[i] = valueWriter( field );
			}
			else if ( member instanceof Method method ) {
				writers[i] = valueWriter( method );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return new HibernateAccessorMethodHandleMultiValueWriter( writers );
	}

	private MethodHandles.Lookup privateLookup(Class<?> targetClass) throws IllegalAccessException {
		return MethodHandles.privateLookupIn(targetClass, this.lookup);
	}
}
