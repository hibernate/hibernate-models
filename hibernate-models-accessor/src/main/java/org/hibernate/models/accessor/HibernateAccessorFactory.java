/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

import org.hibernate.models.accessor.lambda.impl.HibernateAccessorLambdaFactory;
import org.hibernate.models.accessor.reflection.impl.HibernateAccessorReflectionFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Factory for creating accessors that read and write object state and instantiate objects.
 *
 * <p>Obtain an instance via the static factory methods {@link #reflection()} or {@link #lambda(MethodHandles.Lookup)},
 * then use it to create {@link HibernateAccessorInstantiator instantiators},
 * {@link HibernateAccessorValueReader readers}, and {@link HibernateAccessorValueWriter writers}.
 */
public interface HibernateAccessorFactory {

	/**
	 * Returns a reflection-based factory.
	 *
	 * <p>The returned factory uses {@link java.lang.reflect} to access fields, methods, and constructors.
	 *
	 * @return a shared, reflection-based factory instance
	 */
	static HibernateAccessorFactory reflection() {
		return HibernateAccessorReflectionFactory.INSTANCE;
	}

	/**
	 * Returns a lambda-based factory that uses the given lookup for access control.
	 *
	 * <p>The returned factory generates lambda-based accessors via {@link java.lang.invoke.LambdaMetafactory},
	 * which can offer better performance than reflection.
	 *
	 * @param lookup the lookup object that determines access rights
	 * @return a new lambda-based factory instance
	 */
	static HibernateAccessorFactory lambda(MethodHandles.Lookup lookup) {
		return new HibernateAccessorLambdaFactory(lookup);
	}

	/**
	 * Creates an instantiator for the given constructor.
	 *
	 * @param <T> the type to instantiate
	 * @param constructor the constructor to use for instantiation
	 * @return an instantiator that invokes the given constructor
	 * @throws HibernateAccessorException if the accessor cannot be created
	 */
	<T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor);

	/**
	 * Creates a value reader for the given field.
	 *
	 * @param field the field to read from
	 * @return a value reader that reads the field's value from an object instance
	 * @throws HibernateAccessorException if the accessor cannot be created
	 */
	HibernateAccessorValueReader<?> valueReader(Field field);

	/**
	 * Creates a value reader for the given getter method.
	 *
	 * @param method the getter method to invoke
	 * @return a value reader that reads a value by invoking the method on an object instance
	 * @throws HibernateAccessorException if the accessor cannot be created
	 */
	HibernateAccessorValueReader<?> valueReader(Method method);

	/**
	 * Creates a value writer for the given field.
	 *
	 * @param field the field to write to
	 * @return a value writer that sets the field's value on an object instance
	 * @throws HibernateAccessorException if the accessor cannot be created
	 */
	HibernateAccessorValueWriter valueWriter(Field field);

	/**
	 * Creates a value writer for the given setter method.
	 *
	 * @param setter the setter method to invoke
	 * @return a value writer that sets a value by invoking the method on an object instance
	 * @throws HibernateAccessorException if the accessor cannot be created
	 */
	HibernateAccessorValueWriter valueWriter(Method setter);

	/**
	 * Creates a multi-value reader for the given members.
	 *
	 * <p>Each member may be a {@link Field} or a getter {@link Method}.
	 * The returned reader reads all values in the order the members are specified.
	 *
	 * @param members the fields and/or getter methods to read from
	 * @return a multi-value reader that reads all specified members from an object instance
	 * @throws HibernateAccessorException if any accessor cannot be created
	 * @throws IllegalArgumentException if a member is not a Field or Method
	 */
	HibernateAccessorMultiValueReader multiValueReader(Member... members);

	/**
	 * Creates a multi-value writer for the given members.
	 *
	 * <p>Each member may be a {@link Field} or a setter {@link Method}.
	 * The returned writer writes all values in the order the members are specified.
	 *
	 * @param members the fields and/or setter methods to write to
	 * @return a multi-value writer that writes all specified members on an object instance
	 * @throws HibernateAccessorException if any accessor cannot be created
	 * @throws IllegalArgumentException if a member is not a Field or Method
	 */
	HibernateAccessorMultiValueWriter multiValueWriter(Member... members);
}
