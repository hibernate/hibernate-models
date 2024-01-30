/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.util.List;

import org.hibernate.models.internal.util.CollectionHelper;

/**
 * @author Steve Ebersole
 */
public class TypeDetailsHelper {
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
					if ( CollectionHelper.isNotEmpty( resolvedTypeVariable.getBounds() ) ) {
						// and assume the bound is a class
						return resolvedTypeVariable.getBounds().get( 0 ).asClassType();
					}
					return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
				}
				else {
					// assume parameterized
					final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
					if ( CollectionHelper.isNotEmpty( parameterizedType.getArguments() ) ) {
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
					if ( CollectionHelper.isNotEmpty( resolvedTypeVariable.getBounds() ) ) {
						// and assume the bound is a class
						return resolvedTypeVariable.getBounds().get( 0 ).asClassType();
					}
					return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
				}
				else {
					// assume parameterized
					final ParameterizedTypeDetails parameterizedType = typeDetails.asParameterizedType();
					if ( CollectionHelper.isNotEmpty( parameterizedType.getArguments() ) ) {
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
}
