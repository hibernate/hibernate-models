/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import static org.hibernate.models.internal.IsResolvedTypeSwitch.IS_RESOLVED_SWITCH;
import static org.hibernate.models.spi.TypeDetailsSwitch.switchType;

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

	/**
	 * Determine whether all the bounds (if any) of the type are concretely known
	 * <p/>
	 * For example, given:
	 * <pre class="brush:java">
	 * class {@code Thing<T>} {
	 *     T id;
	 * }
	 * class {@code AnotherThing extends Thing<Integer>} {
	 * }
	 * </pre>
	 * The type for {@code id} {@linkplain TypeDetails#determineRelativeType relative} to {@code Thing} is NOT resolved
	 * whereas the type for {@code id} relative to {@code AnotherThing} is.
	 */
	default boolean isResolved() {
		// IMPORTANT : Relies on the fact that `IsResolvedTypeSwitch` never uses the
		// `SourceModelBuildingContext` passed to it as a `TypeDetailsSwitch` implementation.
		// Hence, the passing `null` here
		return switchType( this, IS_RESOLVED_SWITCH, null );
	}

	/**
	 * Determine the type relative to the passed {@code container}.
	 * <p/>
	 * For example, given the classes defined in {@linkplain #isResolved()}, calling
	 * this method has the following outcomes based on the passed {@code container} - <ul>
	 *     <li>Passing {@code Thing}, the result would be the {@code ParameterizedTypeDetails(T)}</li>
	 *     <li>Passing {@code AnotherThing}, the result would be {@code ClassTypeDetails(Integer)}</li>
	 * </ul>
	 */
	default TypeDetails determineRelativeType(TypeDetails container) {
		return TypeDetailsHelper.resolveRelativeType( this, container );
	}

	/**
	 * Determine the type relative to the passed {@code container}.
	 * <p/>
	 * Overload of {@linkplain #determineRelativeType(TypeDetails)}
	 */
	default TypeDetails determineRelativeType(ClassDetails container) {
		return TypeDetailsHelper.resolveRelativeType( this, container );
	}

	/**
	 * Determine the raw {@linkplain ClassDetails class} for the given type.  Never returns {@code null}, opting
	 * to return {@linkplain ClassDetails#OBJECT_CLASS_DETAILS Object} instead if the raw class is not known
	 *
	 * @return The raw class details, or {@linkplain ClassDetails#OBJECT_CLASS_DETAILS Object} if "not known".
	 */
	default ClassDetails determineRawClass() {
		return TypeDetailsHelper.resolveRawClass( this );
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
