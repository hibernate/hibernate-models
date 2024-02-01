/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * Abstraction for what Hibernate understands about a "type", generally before it has access to
 * the actual {@link java.lang.reflect.Type} reference.
 *
 * @see org.jboss.jandex.Type
 *
 * @author Steve Ebersole
 */
public interface TypeDetails extends TypeVariableScope {
	String getName();

	Kind getTypeKind();

	/**
	 * Whether the described class is an implementor of the given {@code checkType}.
	 */
	boolean isImplementor(Class<?> checkType);

	/**
	 * Cast this TypeDetails as a ClassTypeDetails, throwing an exception
	 * if it cannot be.
	 */
	default ClassTypeDetails asClassType() {
		throw new IllegalArgumentException( "Not a class type - " + this );
	}

	/**
	 * Cast this TypeDetails as a PrimitiveTypeDetails, throwing an exception
	 * if it cannot be.
	 */
	default PrimitiveTypeDetails asPrimitiveType() {
		throw new IllegalArgumentException( "Not a primitive type - " + this );
	}

	/**
	 * Cast this TypeDetails as a VoidTypeDetails, throwing an exception
	 * if it cannot be.
	 */
	default VoidTypeDetails asVoidType() {
		throw new IllegalArgumentException( "Not a void type - " + this );
	}

	/**
	 * Cast this TypeDetails as a ArrayTypeDetails, throwing an exception
	 * if it cannot be.
	 */
	default ArrayTypeDetails asArrayType() {
		throw new IllegalArgumentException( "Not an array type - " + this );
	}

	/**
	 * Cast this TypeDetails as a TypeVariableDetails, throwing an exception
	 * if it cannot be.
	 */
	default TypeVariableDetails asTypeVariable() {
		throw new IllegalArgumentException( "Not a type variable - " + this );
	}

	/**
	 * Cast this TypeDetails as a ParameterizedTypeDetails, throwing an exception
	 * if it cannot be.
	 */
	default ParameterizedTypeDetails asParameterizedType() {
		throw new IllegalArgumentException( "Not a parameterized type - " + this );
	}

	/**
	 * Cast this TypeDetails as a TypeVariableReferenceDetails, throwing an exception
	 * if it cannot be.
	 */
	default TypeVariableReferenceDetails asTypeVariableReference() {
		throw new IllegalArgumentException( "Not a type variable reference - " + this );
	}

	/**
	 * Cast this TypeDetails as a WildcardTypeDetails, throwing an exception
	 * if it cannot be.
	 */
	default WildcardTypeDetails asWildcardType() {
		throw new IllegalArgumentException( "Not a wildcard type - " + this );
	}

	enum Kind {

		/**
		 * A Java class, interface, or annotation.
		 *
		 * @see ClassTypeDetails
		 * @see #asClassType()
		 */
		CLASS,

		/**
		 * A Java array
		 *
		 * @see ArrayTypeDetails
		 * @see #asArrayType()
		 */
		ARRAY,

		/**
		 * A Java primitive (boolean, byte, short, char, int, long, float, double)
		 *
		 * @see PrimitiveTypeDetails
		 * @see #asPrimitiveType()
		 */
		PRIMITIVE,

		/**
		 * Used to designate a Java method that returns nothing
		 *
		 * @see VoidTypeDetails
		 * @see #asVoidType()
		 */
		VOID,

		/**
		 * A resolved generic type parameter or type argument
		 *
		 * @see TypeVariableDetails
		 * @see #asTypeVariable()
		 */
		TYPE_VARIABLE,

		/**
		 * A generic wildcard type
		 *
		 * @see WildcardTypeDetails
		 * @see #asWildcardType()
		 */
		WILDCARD_TYPE,

		/**
		 * A generic parameterized type
		 *
		 * @see ParameterizedTypeDetails
		 * @see #asParameterizedType()
		 */
		PARAMETERIZED_TYPE,

		/**
		 * A reference to a resolved type variable occurring in the bound of a recursive type parameter
		 *
		 * @see TypeVariableReferenceDetails
		 * @see #asTypeVariableReference()
		 */
		TYPE_VARIABLE_REFERENCE,
	}
}
