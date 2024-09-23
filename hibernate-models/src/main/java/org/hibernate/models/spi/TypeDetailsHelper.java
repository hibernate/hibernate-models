/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.ArrayTypeDetailsImpl;
import org.hibernate.models.internal.ParameterizedTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveKind;
import org.hibernate.models.internal.util.CollectionHelper;

import static org.hibernate.models.internal.util.CollectionHelper.arrayList;
import static org.hibernate.models.spi.ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;

/**
 * Helper utilities for dealing with {@linkplain TypeDetails}
 *
 * @author Steve Ebersole
 */
public class TypeDetailsHelper {
	/**
	 * Given an attribute member type and a concrete container type, resolve the type of
	 * the attribute relative to that container.
	 * <p/>
	 * For example, consider
	 * <pre class="brush:java">
	 * class {@code Item<T>} {
	 *     T id;
	 * }
	 * class Hat extends {@code Item<Integer>} {
	 *     ...
	 * }
	 * </pre>
	 * Given this model, a call to resolve the type of {@code id} relative to {@code Hat}
	 * will return {@code ClassTypeDetails(Integer)}.  A call to resolve the type of {@code id}
	 * relative to {@code Item} returns {@code ParameterizedTypeDetails(T)} (roughly Object)
	 */
	public static TypeDetails resolveRelativeType(TypeDetails type, TypeVariableScope container) {
		switch ( type.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, WILDCARD_TYPE -> {
				return type;
			}
			case ARRAY -> {
				final ArrayTypeDetails arrayType = type.asArrayType();
				return new ArrayTypeDetailsImpl(
						arrayType.getArrayClassDetails(),
						arrayType.getConstituentType().determineRelativeType( container )
				);
			}
			case PARAMETERIZED_TYPE -> {
				final ParameterizedTypeDetails parameterizedType = type.asParameterizedType();
				final List<TypeDetails> resolvedArguments;
				if ( parameterizedType.getArguments().isEmpty() ) {
					resolvedArguments = Collections.emptyList();
				}
				else {
					resolvedArguments = arrayList( parameterizedType.getArguments().size() );
					for ( TypeDetails argument : parameterizedType.getArguments() ) {
						resolvedArguments.add( argument.determineRelativeType( container ) );
					}
				}
				return new ParameterizedTypeDetailsImpl(
						parameterizedType.getRawClassDetails(),
						resolvedArguments,
						container
				);
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails typeVariable = type.asTypeVariable();
				return container.resolveTypeVariable( typeVariable );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				throw new UnsupportedOperationException( "TypeVariableReferenceDetails not supported for concrete type resolution" );
			}
			default -> {
				throw new UnsupportedOperationException( "Unknown TypeDetails kind - " + type.getTypeKind() );
			}
		}
	}

	public static TypeVariableDetails findTypeVariableDetails(String identifier, List<TypeVariableDetails> typeParameters) {
		if ( CollectionHelper.isNotEmpty( typeParameters ) ) {
			for ( TypeVariableDetails typeParameter : typeParameters ) {
				if ( typeParameter.getIdentifier().equals( identifier ) ) {
					return typeParameter;
				}
			}
		}

		return null;
	}

	/**
	 * Very much the same as {@linkplain #resolveRelativeType(TypeDetails, TypeVariableScope)}, except that
	 * here we resolve the relative type to the corresponding {@link ClassBasedTypeDetails} which
	 * gives easy access to the type's {@linkplain ClassBasedTypeDetails#getClassDetails() ClassDetails}
	 */
	public static ClassBasedTypeDetails resolveRelativeClassType(
			TypeDetails memberType,
			TypeVariableScope containerType) {
		switch ( memberType.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return (ClassBasedTypeDetails) memberType;
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				final TypeDetails typeDetails = containerType.resolveTypeVariable( typeVariable );
				if ( typeDetails.getTypeKind() == TypeDetails.Kind.CLASS ) {
					return typeDetails.asClassType();
				}
				else if ( typeDetails.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
					final TypeVariableDetails resolvedTypeVariable = typeDetails.asTypeVariable();
					if ( CollectionHelper.size( resolvedTypeVariable.getBounds() ) == 1 ) {
						// and assume the bound is a class
						return resolvedTypeVariable.getBounds().get( 0 ).asClassType();
					}
					return OBJECT_TYPE_DETAILS;
				}
				else {
					// assume parameterized
					final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
					if ( CollectionHelper.size( parameterizedType.getArguments() ) == 1 ) {
						// and assume the bound is a class
						return parameterizedType.getArguments().get( 0 ).asClassType();
					}
					return OBJECT_TYPE_DETAILS;
				}
			}
			case TYPE_VARIABLE_REFERENCE -> {
				throw new UnsupportedOperationException( "TypeVariableReferenceDetails not supported for relative class resolution" );
			}
			case PARAMETERIZED_TYPE, WILDCARD_TYPE -> {
				return resolveRelativeType( memberType, containerType ).asClassType();
			}
			default -> {
				throw new UnsupportedOperationException( "Unknown TypeDetails kind - " + memberType.getTypeKind() );
			}
		}
	}

	/**
	 * Given a type, resolve the underlying ClassDetails
	 *
	 * @see TypeDetails#determineRawClass()
	 */
	public static ClassDetails resolveRawClass(TypeDetails typeDetails) {
		switch ( typeDetails.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return ( (ClassBasedTypeDetails) typeDetails ).getClassDetails();
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails resolvedTypeVariable = typeDetails.asTypeVariable();
				if ( CollectionHelper.size( resolvedTypeVariable.getBounds() ) == 1 ) {
					return resolvedTypeVariable.getBounds().get( 0 ).determineRawClass();
				}
				return ClassDetails.OBJECT_CLASS_DETAILS;
			}
			case PARAMETERIZED_TYPE -> {
				final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
				return parameterizedType.getRawClassDetails();
			}
			case WILDCARD_TYPE -> {
				final WildcardTypeDetails wildcardType = typeDetails.asWildcardType();
				if ( wildcardType.getBound() != null ) {
					return wildcardType.getBound().determineRawClass();
				}
				return ClassDetails.OBJECT_CLASS_DETAILS;
			}
			case TYPE_VARIABLE_REFERENCE -> {
				final TypeVariableReferenceDetails typeVariableReference = typeDetails.asTypeVariableReference();
				final TypeDetails identifiedTypeDetails = typeDetails.resolveTypeVariable( typeVariableReference.getTarget() );
				return identifiedTypeDetails.determineRawClass();
			}
		}
		return ClassDetails.OBJECT_CLASS_DETAILS;
	}

	/**
	 * Resolve a {@linkplain TypeVariableDetails type variable}'s type relative to the
	 * provided {@linkplain ParameterizedTypeDetails parameterized type}.
	 *
	 * @param parameterizedType the parameterized type used to resolve the type variable's relative type
	 * @param typeVariable the type variable to resolve
	 *
	 * @return the type variable's relative type, or {@code null} if not resolved
	 */
	public static TypeDetails resolveTypeVariableFromParameterizedType(
			ParameterizedTypeDetails parameterizedType,
			TypeVariableDetails typeVariable) {
		final ClassDetails classDetails = parameterizedType.getRawClassDetails();
		final TypeDetails typeArgument = findMatchingTypeArgument(
				classDetails.getTypeParameters(),
				parameterizedType.getArguments(),
				typeVariable.getIdentifier()
		);

		// If no type argument is found, or the type variable is defined by another
		// class, try resolving it in the generic super type if present
		if ( typeArgument == null || classDetails != typeVariable.getDeclaringType() ) {
			final TypeDetails genericSuper = classDetails.getGenericSuperType();
			final TypeDetails resolvedType = genericSuper != null ?
					genericSuper.resolveTypeVariable( typeVariable ) :
					null;
			if ( typeArgument == null || resolvedType != null
					&& resolvedType.getTypeKind() != TypeDetails.Kind.TYPE_VARIABLE ) {
				return resolvedType;
			}
		}

		// Either we found the exact type variable's argument, or the parameterized class redefines
		// a type variable with the same identifier as a supertype, and it should be ignored.
		// Return the matching generic type argument
		return typeArgument;
	}

	private static TypeDetails findMatchingTypeArgument(
			List<TypeVariableDetails> typeParameters,
			List<TypeDetails> typeArguments,
			String identifier) {
		assert typeParameters.size() == typeArguments.size();
		for ( int i = 0; i < typeParameters.size(); i++ ) {
			final TypeVariableDetails typeParameter = typeParameters.get( i );
			if ( typeParameter.getIdentifier().equals( identifier ) ) {
				return typeArguments.get( i );
			}
		}
		return null;
	}

	/**
	 * Make an array type of the given component type
	 */
	public static ArrayTypeDetails arrayOf(TypeDetails constituentType, SourceModelBuildingContext buildingContext) {
		final ClassDetails arrayClassDetails;
		if ( constituentType.getTypeKind() == TypeDetails.Kind.PRIMITIVE ) {
			final PrimitiveTypeDetails primitiveType = constituentType.asPrimitiveType();
			final PrimitiveKind primitiveKind = primitiveType.getPrimitiveKind();
			arrayClassDetails = buildingContext
					.getClassDetailsRegistry()
					.resolveClassDetails( "[" + primitiveKind.getJavaTypeChar() );
		}
		else {
			final ClassDetails rawComponentType = constituentType.determineRawClass();
			final String arrayClassName = "[L" + rawComponentType.getName().replace( '.', '/' ) + ";";
			arrayClassDetails = buildingContext
					.getClassDetailsRegistry()
					.resolveClassDetails( arrayClassName );
		}
		return new ArrayTypeDetailsImpl( arrayClassDetails, constituentType );
	}

}
