/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.spi;

import org.hibernate.models.accessor.HibernateAccessorException;
import org.hibernate.models.accessor.HibernateAccessorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Shared validation for {@link Member} arguments passed to
 * {@link HibernateAccessorFactory} methods.
 */
public final class MemberValidation {

	private MemberValidation() {
	}

	/**
	 * Validates that the given method is suitable for reading (i.e. is a getter).
	 *
	 * @throws HibernateAccessorException if the method has parameters or returns void
	 */
	public static void validateReaderMethod(Method method) {
		if ( method.getParameterCount() != 0 ) {
			throw new HibernateAccessorException(
					"Method '" + method.getName() + "' on " + method.getDeclaringClass().getName()
							+ " cannot be used as a reader: expected 0 parameters, found " + method.getParameterCount()
			);
		}
		if ( method.getReturnType() == void.class ) {
			throw new HibernateAccessorException(
					"Method '" + method.getName() + "' on " + method.getDeclaringClass().getName()
							+ " cannot be used as a reader: returns void"
			);
		}
	}

	/**
	 * Validates that the given method is suitable for writing (i.e. is a setter).
	 *
	 * @throws HibernateAccessorException if the method does not have exactly one parameter
	 */
	public static void validateWriterMethod(Method method) {
		if ( method.getParameterCount() != 1 ) {
			throw new HibernateAccessorException(
					"Method '" + method.getName() + "' on " + method.getDeclaringClass().getName()
							+ " cannot be used as a writer: expected 1 parameter, found " + method.getParameterCount()
			);
		}
	}

	/**
	 * Validates that the given member is a {@link Field} or a getter {@link Method}.
	 *
	 * @throws IllegalArgumentException if the member is not a Field or Method
	 * @throws HibernateAccessorException if the member is a Method that is not a valid getter
	 */
	public static void validateReaderMember(Member member) {
		if ( member instanceof Field ) {
			return;
		}
		if ( member instanceof Method method ) {
			validateReaderMethod( method );
			return;
		}
		throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
	}

	/**
	 * Validates that the given member is a {@link Field} or a setter {@link Method}.
	 *
	 * @throws IllegalArgumentException if the member is not a Field or Method
	 * @throws HibernateAccessorException if the member is a Method that is not a valid setter
	 */
	public static void validateWriterMember(Member member) {
		if ( member instanceof Field ) {
			return;
		}
		if ( member instanceof Method method ) {
			validateWriterMethod( method );
			return;
		}
		throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
	}

	/**
	 * Validates that the given member is declared on the concrete class or one of its supertypes.
	 *
	 * @param declaringClass the concrete class whose instances will be accessed
	 * @param member the member to validate
	 * @throws IllegalArgumentException if the member is not declared on the class or any of its supertypes
	 */
	public static void validateMemberDeclaringType(Class<?> declaringClass, Member member) {
		if ( !member.getDeclaringClass().isAssignableFrom( declaringClass ) ) {
			throw new IllegalArgumentException(
					"Member '" + member.getName() + "' is declared on " + member.getDeclaringClass().getName()
							+ " which is not a supertype of " + declaringClass.getName()
			);
		}
	}
}
