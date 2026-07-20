/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.hibernate.models.internal.ArrayTypeDetailsImpl;
import org.hibernate.models.internal.ParameterizedTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveKind;
import org.hibernate.models.internal.util.CollectionHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.models.internal.util.CollectionHelper.arrayList;
import static org.hibernate.models.spi.StandardTypeDetails.OBJECT_TYPE_DETAILS;

/**
 * Helper utilities for dealing with {@linkplain TypeDetails}
 *
 * @author Steve Ebersole
 */
public class TypeDetailsHelper {
	/**
	 * Resolve {@code type} to the corresponding occurrence of {@code superType} in its
	 * type hierarchy, substituting type variables at each hierarchy edge.
	 *
	 * @return the resolved super type, or {@code null} if {@code type} does not implement it
	 */
	public static TypeDetails resolveSuperType(TypeDetails type, Class<?> superType) {
		if ( type == null || !type.isImplementor( superType ) ) {
			return null;
		}
		return resolveSuperType( type, superType, new HashSet<>() );
	}

	private static TypeDetails resolveSuperType(
			TypeDetails type,
			Class<?> superType,
			Set<ClassDetails> visitedTypes) {
		if ( type.getTypeKind() == TypeDetails.Kind.WILDCARD_TYPE ) {
			final TypeDetails bound = type.asWildcardType().getBound();
			return bound == null ? null : resolveSuperType( bound, superType, visitedTypes );
		}
		if ( type.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
			for ( TypeDetails bound : type.asTypeVariable().getBounds() ) {
				final TypeDetails resolved = resolveSuperType( bound, superType, visitedTypes );
				if ( resolved != null ) {
					return resolved;
				}
			}
			return null;
		}

		final ClassDetails rawType = type.determineRawClass();
		if ( rawType.getName().equals( superType.getName() ) ) {
			return type;
		}
		if ( !visitedTypes.add( rawType ) ) {
			return null;
		}

		final TypeDetails genericSuperType = rawType.getGenericSuperType();
		if ( genericSuperType != null ) {
			final TypeDetails resolved = resolveSuperType(
					genericSuperType.determineRelativeType( type ),
					superType,
					visitedTypes
			);
			if ( resolved != null ) {
				return resolved;
			}
		}

		for ( TypeDetails implementedInterface : rawType.getImplementedInterfaces() ) {
			final TypeDetails resolved = resolveSuperType(
					implementedInterface.determineRelativeType( type ),
					superType,
					visitedTypes
			);
			if ( resolved != null ) {
				return resolved;
			}
		}

		return null;
	}

	/**
	 * Extract the collection element or map value type.
	 */
	public static TypeDetails extractElementType(TypeDetails type) {
		if ( type == null ) {
			return null;
		}
		if ( type.getTypeKind() == TypeDetails.Kind.ARRAY ) {
			return type.asArrayType().getConstituentType();
		}
		if ( type.isImplementor( Collection.class ) ) {
			return extractCollectionElementType( type );
		}
		if ( type.isImplementor( Map.class ) ) {
			return extractMapValueType( type );
		}
		return null;
	}

	public static TypeDetails extractCollectionElementType(TypeDetails type) {
		return extractTypeArgument( resolveSuperType( type, Collection.class ), 0 );
	}

	public static TypeDetails extractMapKeyType(TypeDetails type) {
		return extractTypeArgument( resolveSuperType( type, Map.class ), 0 );
	}

	public static TypeDetails extractMapValueType(TypeDetails type) {
		return extractTypeArgument( resolveSuperType( type, Map.class ), 1 );
	}

	private static TypeDetails extractTypeArgument(TypeDetails resolvedType, int argumentIndex) {
		if ( resolvedType == null || resolvedType.getTypeKind() != TypeDetails.Kind.PARAMETERIZED_TYPE ) {
			return OBJECT_TYPE_DETAILS;
		}
		final List<TypeDetails> arguments = resolvedType.asParameterizedType().getArguments();
		return argumentIndex < arguments.size() ? arguments.get( argumentIndex ) : OBJECT_TYPE_DETAILS;
	}

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
	 * @return the type variable's relative type
	 */
	public static TypeDetails resolveTypeVariableFromParameterizedType(
			ParameterizedTypeDetails parameterizedType,
			TypeVariableDetails typeVariable) {
		final ClassDetails classDetails = parameterizedType.getRawClassDetails();
		if ( classDetails == typeVariable.getDeclaringType() ) {
			// If the type variable is defined by the parameterized class, try to find the matching type argument
			return findMatchingTypeArgument(
					classDetails.getTypeParameters(),
					parameterizedType.getArguments(),
					typeVariable.getIdentifier()
			);
		}
		else {
			// Try resolving the type variable in the generic super type
			final TypeDetails genericSuper = classDetails.getGenericSuperType();
			final TypeDetails resolvedType = genericSuper != null ?
					genericSuper.resolveTypeVariable( typeVariable ) :
					null;
			if ( resolvedType != null ) {
				return resolvedType.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ?
						parameterizedType.resolveTypeVariable( resolvedType.asTypeVariable() ) :
						resolvedType;
			}
		}

		throw new IllegalArgumentException(
				"Unable to resolve type variable [" + typeVariable.getIdentifier() + "] from parameterized type ["
						+ parameterizedType.getName() + "]"
		);
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
	public static ArrayTypeDetails arrayOf(TypeDetails constituentType, ModelsContext modelsContext) {
		final ClassDetails arrayClassDetails;
		if ( constituentType.getTypeKind() == TypeDetails.Kind.PRIMITIVE ) {
			final PrimitiveTypeDetails primitiveType = constituentType.asPrimitiveType();
			final PrimitiveKind primitiveKind = primitiveType.getPrimitiveKind();
			arrayClassDetails = modelsContext
					.getClassDetailsRegistry()
					.resolveClassDetails( "[" + primitiveKind.getJavaTypeChar() );
		}
		else {
			final ClassDetails rawComponentType = constituentType.determineRawClass();
			final String arrayClassName = "[L" + rawComponentType.getName().replace( '.', '/' ) + ";";
			arrayClassDetails = modelsContext
					.getClassDetailsRegistry()
					.resolveClassDetails( arrayClassName );
		}
		return new ArrayTypeDetailsImpl( arrayClassDetails, constituentType );
	}

}
