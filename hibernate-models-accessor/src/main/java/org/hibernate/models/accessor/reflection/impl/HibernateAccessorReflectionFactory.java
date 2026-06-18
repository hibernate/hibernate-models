/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.reflection.impl;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.spi.MemberValidation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
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
		MemberValidation.validateReaderMethod( method );
		return new HibernateAccessorReflectionMethodValueReader<>(method);
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		return new HibernateAccessorReflectionFieldValueWriter(field);
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		MemberValidation.validateWriterMethod( setter );
		return new HibernateAccessorReflectionMethodValueWriter(setter);
	}

	@Override
	public HibernateAccessorMultiValueReader multiValueReader(Member... members) {
		final HibernateAccessorValueReader<?>[] readers = new HibernateAccessorValueReader<?>[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			MemberValidation.validateReaderMember( member );
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
		return new HibernateAccessorReflectionMultiValueReader( readers );
	}

	@Override
	public HibernateAccessorMultiValueWriter multiValueWriter(Member... members) {
		final HibernateAccessorValueWriter[] writers = new HibernateAccessorValueWriter[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			MemberValidation.validateWriterMember( member );
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
		return new HibernateAccessorReflectionMultiValueWriter( writers );
	}

}
