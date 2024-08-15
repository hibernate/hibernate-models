/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * @author Steve Ebersole
 */
public interface TypeDetailsSwitch<T> {
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

	T caseClass(ClassTypeDetails classType, SourceModelBuildingContext buildingContext);

	T casePrimitive(PrimitiveTypeDetails primitiveType, SourceModelBuildingContext buildingContext);

	T caseVoid(VoidTypeDetails voidType, SourceModelBuildingContext buildingContext);

	T caseParameterizedType(ParameterizedTypeDetails parameterizedType, SourceModelBuildingContext buildingContext);

	T caseWildcardType(WildcardTypeDetails wildcardType, SourceModelBuildingContext buildingContext);

	T caseTypeVariable(TypeVariableDetails typeVariable, SourceModelBuildingContext buildingContext);

	T caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference, SourceModelBuildingContext buildingContext);

	T caseArrayType(ArrayTypeDetails arrayType, SourceModelBuildingContext buildingContext);

	T defaultCase(TypeDetails type, SourceModelBuildingContext buildingContext);
}
