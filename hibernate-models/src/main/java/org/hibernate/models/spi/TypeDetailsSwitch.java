/*
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
	static <T> T switchType(TypeDetails typeDetails, TypeDetailsSwitch<T> switcher, ModelsContext modelsContext) {
		switch( typeDetails.getTypeKind() ) {
			case CLASS -> {
				return switcher.caseClass( typeDetails.asClassType(), modelsContext );
			}
			case PRIMITIVE -> {
				return switcher.casePrimitive( typeDetails.asPrimitiveType(), modelsContext );
			}
			case VOID -> {
				return switcher.caseVoid( typeDetails.asVoidType(), modelsContext );
			}
			case ARRAY -> {
				return switcher.caseArrayType( typeDetails.asArrayType(), modelsContext );
			}
			case PARAMETERIZED_TYPE -> {
				return switcher.caseParameterizedType( typeDetails.asParameterizedType(), modelsContext );
			}
			case WILDCARD_TYPE -> {
				return switcher.caseWildcardType( typeDetails.asWildcardType(), modelsContext );
			}
			case TYPE_VARIABLE -> {
				return switcher.caseTypeVariable( typeDetails.asTypeVariable(), modelsContext );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				return switcher.caseTypeVariableReference( typeDetails.asTypeVariableReference(), modelsContext );
			}
			default -> {
				return switcher.defaultCase( typeDetails, modelsContext );
			}
		}
	}

	/**
	 * Handle the case of a raw class
	 */
	T caseClass(ClassTypeDetails classType, ModelsContext modelsContext);

	/**
	 * Handle the case of a primitive
	 */
	T casePrimitive(PrimitiveTypeDetails primitiveType, ModelsContext modelsContext);

	/**
	 * Handle the case of void (or Void)
	 */
	T caseVoid(VoidTypeDetails voidType, ModelsContext modelsContext);

	/**
	 * Handle the case of a parameterized type
	 */
	T caseParameterizedType(ParameterizedTypeDetails parameterizedType, ModelsContext modelsContext);

	/**
	 * Handle the case of a wildcard type
	 */
	T caseWildcardType(WildcardTypeDetails wildcardType, ModelsContext modelsContext);

	/**
	 * Handle the case of a type variable
	 */
	T caseTypeVariable(TypeVariableDetails typeVariable, ModelsContext modelsContext);

	/**
	 * Handle the case of a reference to a type variable
	 */
	T caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference, ModelsContext modelsContext);

	/**
	 * Handle the case of an array
	 */
	T caseArrayType(ArrayTypeDetails arrayType, ModelsContext modelsContext);

	/**
	 * Handle any other cases
	 */
	T defaultCase(TypeDetails type, ModelsContext modelsContext);
}
