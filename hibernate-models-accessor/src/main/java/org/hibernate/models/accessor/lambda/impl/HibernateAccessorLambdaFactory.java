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
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class HibernateAccessorLambdaFactory implements HibernateAccessorFactory {

	private final MethodHandles.Lookup lookup;

	public HibernateAccessorLambdaFactory(MethodHandles.Lookup lookup) {
		this.lookup = lookup;
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		return new LambdaInstantiator<>(lookup, constructor);
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		try {
			return new LambdaFieldValueReader<>(MethodHandles.privateLookupIn(field.getDeclaringClass(), this.lookup).unreflectGetter(field));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(field, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		try {
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(method.getDeclaringClass(), this.lookup);
			MethodHandle target = lookup.unreflect(method);
			CallSite site = LambdaMetafactory.metafactory(
					lookup,
					"get",
					MethodType.methodType(HibernateAccessorValueReader.class),
					MethodType.methodType(Object.class, Object.class),
					target,
					MethodType.methodType(method.getReturnType(), method.getDeclaringClass())
			);
			return (HibernateAccessorValueReader<?>) site.getTarget().invokeExact();
		}
		catch (Throwable t) {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw CoreLog.INSTANCE.errorCreatingHandle(method, t, t.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		try {
			return new LambdaFieldValueWriter(MethodHandles.privateLookupIn(field.getDeclaringClass(), this.lookup).unreflectSetter(field));
		}
		catch (IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorCreatingHandle(field, e, e.getMessage());
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		try {
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(setter.getDeclaringClass(), this.lookup);
			MethodHandle target = lookup.unreflect(setter);

			Class<?> paramType = setter.getParameterTypes()[0].isPrimitive()
					? MethodType.methodType(setter.getParameterTypes()[0]).wrap().returnType()
					: setter.getParameterTypes()[0];

			CallSite site = LambdaMetafactory.metafactory(
					lookup,
					"set",
					MethodType.methodType(HibernateAccessorValueWriter.class),
					MethodType.methodType(void.class, Object.class, Object.class),
					target,
					MethodType.methodType(void.class, setter.getDeclaringClass(), paramType)
			);
			return (HibernateAccessorValueWriter) site.getTarget().invokeExact();
		}
		catch (Throwable t) {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw CoreLog.INSTANCE.errorCreatingHandle(setter, t, t.getMessage());
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
		return new LambdaMultiValueReader( readers );
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
		return new LambdaMultiValueWriter( writers );
	}
}
