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
import org.hibernate.models.accessor.spi.MemberValidation;

import org.jboss.logging.Logger;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HibernateAccessorMethodHandleFactory implements HibernateAccessorFactory {

	private static final Logger LOG = Logger.getLogger( HibernateAccessorMethodHandleFactory.class );

	private final MethodHandles.Lookup lookup;
	private final HibernateAccessorFactory reflectionFallback = HibernateAccessorFactory.reflection();

	public HibernateAccessorMethodHandleFactory(MethodHandles.Lookup lookup) {
		this.lookup = lookup;
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		try {
			return new HibernateAccessorMethodHandleInstantiator<>(
					privateLookup(constructor.getDeclaringClass()).unreflectConstructor(constructor)
							.asSpreader(Object[].class, constructor.getParameterCount()));
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create method-handle instantiator for %s, falling back to reflection", constructor );
			return reflectionFallback.instantiator( constructor );
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		MemberValidation.validateInstanceMember( field );
		try {
			return new HibernateAccessorMethodHandleFieldValueReader<>(
					privateLookup(field.getDeclaringClass()).unreflectGetter(field));
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create method-handle field reader for %s, falling back to reflection", field );
			return reflectionFallback.valueReader( field );
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		MemberValidation.validateReaderMethod( method );
		try {
			return new HibernateAccessorMethodHandleMethodValueReader<>(
					privateLookup(method.getDeclaringClass()).unreflect(method));
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create method-handle method reader for %s, falling back to reflection", method );
			return reflectionFallback.valueReader( method );
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		MemberValidation.validateInstanceMember( field );
		if ( Modifier.isFinal( field.getModifiers() ) ) {
			return reflectionFallback.valueWriter( field );
		}
		try {
			return new HibernateAccessorMethodHandleFieldValueWriter(
					privateLookup(field.getDeclaringClass()).unreflectSetter(field));
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create method-handle field writer for %s, falling back to reflection", field );
			return reflectionFallback.valueWriter( field );
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		MemberValidation.validateWriterMethod( setter );
		try {
			return new HibernateAccessorMethodHandleMethodValueWriter(
					privateLookup(setter.getDeclaringClass()).unreflect(setter));
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create method-handle method writer for %s, falling back to reflection", setter );
			return reflectionFallback.valueWriter( setter );
		}
	}

	@Override
	public HibernateAccessorMultiValueReader multiValueReader(Class<?> declaringClass, Member... members) {
		if ( members.length == 0 ) {
			throw new IllegalArgumentException( "At least one member is required" );
		}
		try {
			final HibernateAccessorValueReader<?>[] readers = new HibernateAccessorValueReader<?>[members.length];
			for ( int i = 0; i < members.length; i++ ) {
				final Member member = members[i];
				MemberValidation.validateMemberDeclaringType( declaringClass, member );
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
			return new HibernateAccessorMethodHandleMultiValueReader( readers );
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create method-handle multi-value reader for %s, falling back to reflection", declaringClass );
			return reflectionFallback.multiValueReader( declaringClass, members );
		}
	}

	@Override
	public HibernateAccessorMultiValueWriter multiValueWriter(Class<?> declaringClass, Member... members) {
		if ( members.length == 0 ) {
			throw new IllegalArgumentException( "At least one member is required" );
		}
		try {
			final HibernateAccessorValueWriter[] writers = new HibernateAccessorValueWriter[members.length];
			for ( int i = 0; i < members.length; i++ ) {
				final Member member = members[i];
				MemberValidation.validateMemberDeclaringType( declaringClass, member );
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
			return new HibernateAccessorMethodHandleMultiValueWriter( writers );
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create method-handle multi-value writer for %s, falling back to reflection", declaringClass );
			return reflectionFallback.multiValueWriter( declaringClass, members );
		}
	}

	private MethodHandles.Lookup privateLookup(Class<?> targetClass) throws IllegalAccessException {
		return MethodHandles.privateLookupIn(targetClass, this.lookup);
	}
}
