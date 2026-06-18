/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class HibernateAccessorAsmFactory implements HibernateAccessorFactory {

	private static final ConcurrentHashMap<Class<?>, HibernateAccessorAsmClassAccessorInfo> cache = new ConcurrentHashMap<>();
	private transient final MethodHandles.Lookup lookup;

	public HibernateAccessorAsmFactory(MethodHandles.Lookup lookup) {
		this.lookup = lookup;
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(constructor.getDeclaringClass());
		return new HibernateAccessorAsmInstantiator<>(info.bulkAccessor(), info.constructorIndex(constructor));
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
		return new HibernateAccessorAsmFieldValueReader<>(info.bulkAccessor(), info.fieldIndex(field));
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(method.getDeclaringClass());
		return new HibernateAccessorAsmMethodValueReader<>(info.bulkAccessor(), info.methodIndex(method));
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
		return new HibernateAccessorAsmFieldValueWriter(info.bulkAccessor(), info.fieldIndex(field));
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(setter.getDeclaringClass());
		return new HibernateAccessorAsmMethodValueWriter(info.bulkAccessor(), info.methodIndex(setter));
	}

	@Override
	public HibernateAccessorMultiValueReader multiValueReader(Member... members) {
		final boolean[] isField = new boolean[members.length];
		final int[] indices = new int[members.length];
		HibernateAccessorAsmBulkAccessor accessor = null;
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			final HibernateAccessorAsmClassAccessorInfo info = getOrCreate( member.getDeclaringClass() );
			if ( accessor == null ) {
				accessor = info.bulkAccessor();
			}
			if ( member instanceof Field field ) {
				isField[i] = true;
				indices[i] = info.fieldIndex( field );
			}
			else if ( member instanceof Method method ) {
				isField[i] = false;
				indices[i] = info.methodIndex( method );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return new HibernateAccessorAsmMultiValueReader( accessor, isField, indices );
	}

	@Override
	public HibernateAccessorMultiValueWriter multiValueWriter(Member... members) {
		final boolean[] isField = new boolean[members.length];
		final int[] indices = new int[members.length];
		HibernateAccessorAsmBulkAccessor accessor = null;
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			final HibernateAccessorAsmClassAccessorInfo info = getOrCreate( member.getDeclaringClass() );
			if ( accessor == null ) {
				accessor = info.bulkAccessor();
			}
			if ( member instanceof Field field ) {
				isField[i] = true;
				indices[i] = info.fieldIndex( field );
			}
			else if ( member instanceof Method method ) {
				isField[i] = false;
				indices[i] = info.methodIndex( method );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return new HibernateAccessorAsmMultiValueWriter( accessor, isField, indices );
	}

	private HibernateAccessorAsmClassAccessorInfo getOrCreate(Class<?> declaringClass) {
		return cache.computeIfAbsent(declaringClass, cls -> HibernateAccessorAsmClassAccessorInfo.create(cls, lookup));
	}
}
