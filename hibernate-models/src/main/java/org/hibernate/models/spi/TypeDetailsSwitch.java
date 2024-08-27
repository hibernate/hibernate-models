/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * Contract used in interpreting generics details.
 *
 * @author Steve Ebersole
 */
public interface TypeDetailsSwitch<T> {
	/**
	 * Main entry into the generics interpretation, with delegation to the defined case methods
	 */
	static <T> T switchType(TypeDetails typeDetails, TypeDetailsSwitch<T> switcher, SourceModelBuildingContext buildingContext) {
		switch( typeDetails.getTypeKind() ) {
			case CLASS -> {
				return switcher.caseClass( typeDetails.asClassType(), buildingContext );
			}
			case PRIMITIVE -> {
				return switcher.casePrimitive( typeDetails.asPrimitiveType(), buildingContext );
			}
			case VOID -> {
				return switcher.caseVoid( typeDetails.asVoidType(), buildingContext );
			}
			case ARRAY -> {
				return switcher.caseArrayType( typeDetails.asArrayType(), buildingContext );
			}
			case PARAMETERIZED_TYPE -> {
				return switcher.caseParameterizedType( typeDetails.asParameterizedType(), buildingContext );
			}
			case WILDCARD_TYPE -> {
				return switcher.caseWildcardType( typeDetails.asWildcardType(), buildingContext );
			}
			case TYPE_VARIABLE -> {
				return switcher.caseTypeVariable( typeDetails.asTypeVariable(), buildingContext );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				return switcher.caseTypeVariableReference( typeDetails.asTypeVariableReference(), buildingContext );
			}
			default -> {
				return switcher.defaultCase( typeDetails, buildingContext );
			}
		}
	}

	/**
	 * Handle the case of a raw class
	 */
	T caseClass(ClassTypeDetails classType, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of a primitive
	 */
	T casePrimitive(PrimitiveTypeDetails primitiveType, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of void (or Void)
	 */
	T caseVoid(VoidTypeDetails voidType, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of a parameterized type
	 */
	T caseParameterizedType(ParameterizedTypeDetails parameterizedType, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of a wildcard type
	 */
	T caseWildcardType(WildcardTypeDetails wildcardType, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of a type variable
	 */
	T caseTypeVariable(TypeVariableDetails typeVariable, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of a reference to a type variable
	 */
	T caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference, SourceModelBuildingContext buildingContext);

	/**
	 * Handle the case of an array
	 */
	T caseArrayType(ArrayTypeDetails arrayType, SourceModelBuildingContext buildingContext);

	/**
	 * Handle any other cases
	 */
	T defaultCase(TypeDetails type, SourceModelBuildingContext buildingContext);
}
