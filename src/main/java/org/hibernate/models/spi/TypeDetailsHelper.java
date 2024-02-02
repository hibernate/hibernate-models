/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.util.List;

import org.hibernate.models.internal.ArrayTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveKind;
import org.hibernate.models.internal.util.CollectionHelper;

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
	 * class Hat extends {@code Item<Integer} {
	 *     ...
	 * }
	 * </pre>
	 * Given this model, a call to resolve the type of {@code id} relative to {@code Hat}
	 * will return {@code ClassTypeDetails(Integer)}.  A call to resolve the type of {@code id}
	 * relative to {@code Item} returns {@code ParameterizedTypeDetails(T)} (roughly Object)
	 */
	@SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
	public static TypeDetails resolveRelativeType(TypeDetails type, TypeDetails container) {
		switch ( type.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return type;
			}
			case PARAMETERIZED_TYPE -> {
				throw new UnsupportedOperationException( "Not implemented" );
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails typeVariable = type.asTypeVariable();
				return container.resolveTypeVariable( typeVariable.getIdentifier() );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				throw new UnsupportedOperationException( "TypeVariableReferenceDetails not supported for concrete type resolution" );
			}
			case WILDCARD_TYPE -> {
				throw new UnsupportedOperationException( "WildcardTypeDetails not supported for concrete type resolution" );
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

	public static TypeVariableDetails findTypeVariableDetails2(String identifier, List<TypeDetails> typeParameters) {
		if ( CollectionHelper.isNotEmpty( typeParameters ) ) {
			for ( TypeDetails typeParameter : typeParameters ) {
				if ( typeParameter instanceof TypeVariableDetails typeVariableDetails ) {
					if ( typeVariableDetails.getIdentifier().equals( identifier ) ) {
						return typeVariableDetails;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Overload of {@linkplain #resolveRelativeType(TypeDetails, TypeDetails)}
	 */
	@SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
	public static TypeDetails resolveRelativeType(TypeDetails memberType, ClassDetails containerType) {
		switch ( memberType.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return memberType;
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				return containerType.resolveTypeVariable( typeVariable.getIdentifier() );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				throw new UnsupportedOperationException( "TypeVariableReferenceDetails not supported for relative type resolution" );
			}
			case PARAMETERIZED_TYPE -> {
				throw new UnsupportedOperationException( "ParameterizedTypeDetails not supported for relative type resolution" );
			}
			case WILDCARD_TYPE -> {
				throw new UnsupportedOperationException( "WildcardTypeDetails not supported for relative type resolution" );
			}
			default -> {
				throw new UnsupportedOperationException( "Unknown TypeDetails kind - " + memberType.getTypeKind() );
			}
		}
	}

	/**
	 * Very much the same as {@linkplain #resolveRelativeType(TypeDetails, TypeDetails)}, except that
	 * here we resolve the relative type to the corresponding {@link ClassBasedTypeDetails} which
	 * gives easy access to the type's {@linkplain ClassBasedTypeDetails#getClassDetails() ClassDetails}
	 */
	@SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
	public static ClassBasedTypeDetails resolveRelativeClassType(TypeDetails memberType, TypeDetails containerType) {
		switch ( memberType.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return (ClassBasedTypeDetails) memberType;
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				final TypeDetails typeDetails = containerType.resolveTypeVariable( typeVariable.getIdentifier() );
				if ( typeDetails.getTypeKind() == TypeDetails.Kind.CLASS ) {
					return typeDetails.asClassType();
				}
				else if ( typeDetails.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
					final TypeVariableDetails resolvedTypeVariable = typeDetails.asTypeVariable();
					if ( CollectionHelper.size( resolvedTypeVariable.getBounds() ) == 1 ) {
						// and assume the bound is a class
						return resolvedTypeVariable.getBounds().get( 0 ).asClassType();
					}
					return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
				}
				else {
					// assume parameterized
					final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
					if ( CollectionHelper.size( parameterizedType.getArguments() ) == 1 ) {
						// and assume the bound is a class
						return parameterizedType.getArguments().get( 0 ).asClassType();
					}
					return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
				}
			}
			case TYPE_VARIABLE_REFERENCE -> {
				throw new UnsupportedOperationException( "TypeVariableReferenceDetails not supported for relative class resolution" );
			}
			case PARAMETERIZED_TYPE -> {
				throw new UnsupportedOperationException( "ParameterizedTypeDetails not supported for relative class resolution" );
			}
			case WILDCARD_TYPE -> {
				throw new UnsupportedOperationException( "WildcardTypeDetails not supported for relative class resolution" );
			}
			default -> {
				throw new UnsupportedOperationException( "Unknown TypeDetails kind - " + memberType.getTypeKind() );
			}
		}
	}

	/**
	 * Overload form of {@linkplain #resolveRelativeClassType(TypeDetails, TypeDetails)}
	 */
	@SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
	public static ClassBasedTypeDetails resolveRelativeClassType(TypeDetails memberType, ClassDetails containerType) {
		switch ( memberType.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return (ClassBasedTypeDetails) memberType;
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				final TypeDetails typeDetails = containerType.resolveTypeVariable( typeVariable.getIdentifier() );
				if ( typeDetails.getTypeKind() == TypeDetails.Kind.CLASS ) {
					return typeDetails.asClassType();
				}
				else if ( typeDetails.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
					final TypeVariableDetails resolvedTypeVariable = typeDetails.asTypeVariable();
					if ( CollectionHelper.size( resolvedTypeVariable.getBounds() ) == 1 ) {
						// and assume the bound is a class
						return resolvedTypeVariable.getBounds().get( 0 ).asClassType();
					}
					return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
				}
				else {
					// assume parameterized
					final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
					if ( CollectionHelper.size( parameterizedType.getArguments() ) == 1 ) {
						// and assume the bound is a class
						return parameterizedType.getArguments().get( 0 ).asClassType();
					}
					return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
				}
			}
			case TYPE_VARIABLE_REFERENCE -> {
				throw new UnsupportedOperationException( "TypeVariableReferenceDetails not supported for relative type resolution" );
			}
			case PARAMETERIZED_TYPE -> {
				throw new UnsupportedOperationException( "ParameterizedTypeDetails not supported for relative type resolution" );
			}
			case WILDCARD_TYPE -> {
				throw new UnsupportedOperationException( "WildcardTypeDetails not supported for relative type resolution" );
			}
			default -> {
				throw new UnsupportedOperationException( "Unknown TypeDetails kind - " + memberType.getTypeKind() );
			}
		}
	}

	/**
	 * Given a type, resolve the underlying ClassDetails
	 */
	public static ClassDetails resolveRawClass(
			TypeDetails typeDetails,
			@SuppressWarnings("unused") SourceModelBuildingContext buildingContext) {
		switch ( typeDetails.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID, ARRAY -> {
				return ( (ClassBasedTypeDetails) typeDetails ).getClassDetails();
			}
			case TYPE_VARIABLE -> {
				final TypeVariableDetails resolvedTypeVariable = typeDetails.asTypeVariable();
				if ( CollectionHelper.size( resolvedTypeVariable.getBounds() ) == 1 ) {
					// and assume the bound is a class
					return resolvedTypeVariable.getBounds().get( 0 ).asClassType().getClassDetails();
				}
				return ClassDetails.OBJECT_CLASS_DETAILS;
			}
			case PARAMETERIZED_TYPE -> {
				final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
				if ( CollectionHelper.size( parameterizedType.getArguments() ) == 1 ) {
					// and assume the bound is a class
					return parameterizedType.getArguments().get( 0 ).asClassType().getClassDetails();
				}
				return ClassDetails.OBJECT_CLASS_DETAILS;
			}
		}
		return ClassDetails.OBJECT_CLASS_DETAILS;
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
			final ClassDetails rawComponentType = TypeDetailsHelper.resolveRawClass( constituentType, buildingContext );
			final String arrayClassName = "[L" + rawComponentType.getName().replace( '.', '/' ) + ";";
			arrayClassDetails = buildingContext
					.getClassDetailsRegistry()
					.resolveClassDetails( arrayClassName );
		}
		return new ArrayTypeDetailsImpl( arrayClassDetails, constituentType );
	}
}
