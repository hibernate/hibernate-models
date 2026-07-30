/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.lambda.impl;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.spi.HibernateAccessorConfiguration;
import org.hibernate.models.accessor.spi.MemberValidation;

import org.jboss.logging.Logger;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HibernateAccessorLambdaFactory implements HibernateAccessorFactory {

	private static final Logger LOG = Logger.getLogger( HibernateAccessorLambdaFactory.class );

	private final MethodHandles.Lookup lookup;
	private final HibernateAccessorFactory reflectionFallback = HibernateAccessorFactory.reflection();

	public HibernateAccessorLambdaFactory(MethodHandles.Lookup lookup) {
		this( new HibernateAccessorConfiguration( lookup ) );
	}

	public HibernateAccessorLambdaFactory(HibernateAccessorConfiguration configuration) {
		this.lookup = configuration.lookup();
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		try {
			return new LambdaInstantiator<>(
					MethodHandles.privateLookupIn( constructor.getDeclaringClass(), this.lookup ),
					constructor
			);
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create lambda instantiator for %s, falling back to reflection", constructor );
			return reflectionFallback.instantiator( constructor );
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		MemberValidation.validateInstanceMember( field );
		try {
			return new LambdaFieldValueReader<>( MethodHandles.privateLookupIn( field.getDeclaringClass(), this.lookup ).unreflectGetter( field ) );
		}
		catch (RuntimeException|IllegalAccessException e) {
			LOG.debugf( e, "Failed to create lambda field reader for %s, falling back to reflection", field );
			return reflectionFallback.valueReader( field );
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		MemberValidation.validateReaderMethod( method );
		try {
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn( method.getDeclaringClass(), this.lookup );
			MethodHandle target = lookup.unreflect( method );
			try {
				CallSite site = LambdaMetafactory.metafactory(
						lookup,
						"get",
						MethodType.methodType( HibernateAccessorValueReader.class ),
						MethodType.methodType( Object.class, Object.class ),
						target,
						MethodType.methodType( method.getReturnType(), method.getDeclaringClass() )
				);
				return (HibernateAccessorValueReader<?>) site.getTarget().invokeExact();
			}
			catch (LambdaConversionException e) {
				// LambdaMetafactory internally calls defineHiddenClass which requires
				// full privilege access (MODULE bit). Cross-classloader lookups lose
				// MODULE (JDK-8228624), so metafactory fails. Fall back to MethodHandle
				// which only needs PRIVATE access and works cross-CL.
				return new LambdaFieldValueReader<>( target );
			}
		}
		catch (Throwable t) {
			if ( t instanceof Error ) {
				throw (Error) t;
			}
			LOG.debugf( t, "Failed to create lambda method reader for %s, falling back to reflection", method );
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
			return new LambdaFieldValueWriter( MethodHandles.privateLookupIn( field.getDeclaringClass(), this.lookup ).unreflectSetter( field ) );
		}
		catch (IllegalAccessException t) {
			LOG.debugf( t, "Failed to create lambda field writer for %s, falling back to reflection", field );
			return reflectionFallback.valueWriter( field );
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		MemberValidation.validateWriterMethod( setter );
		try {
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn( setter.getDeclaringClass(), this.lookup );
			MethodHandle target = lookup.unreflect( setter );

			Class<?> paramType = setter.getParameterTypes()[0].isPrimitive()
					? MethodType.methodType( setter.getParameterTypes()[0] ).wrap().returnType()
					: setter.getParameterTypes()[0];

			try {
				CallSite site = LambdaMetafactory.metafactory(
						lookup,
						"set",
						MethodType.methodType( HibernateAccessorValueWriter.class ),
						MethodType.methodType( void.class, Object.class, Object.class ),
						target,
						MethodType.methodType( void.class, setter.getDeclaringClass(), paramType )
				);
				return (HibernateAccessorValueWriter) site.getTarget().invokeExact();
			}
			catch (LambdaConversionException e) {
				// See valueReader(Method) — same cross-CL MODULE bit issue (JDK-8228624)
				return new LambdaFieldValueWriter( target );
			}
		}
		catch (Throwable t) {
			if ( t instanceof Error ) {
				throw (Error) t;
			}
			LOG.debugf( t, "Failed to create lambda method writer for %s, falling back to reflection", setter );
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
			return new LambdaMultiValueReader( readers );
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create lambda multi-value reader for %s, falling back to reflection", declaringClass );
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
			return new LambdaMultiValueWriter( writers );
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create lambda multi-value writer for %s, falling back to reflection", declaringClass );
			return reflectionFallback.multiValueWriter( declaringClass, members );
		}
	}
}
