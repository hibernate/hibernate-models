/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Contract used in interpreting generics details.
 * Case methods may return {@code null}. The context may be {@code null} only
 * when the supplied switch implementation does not require a models context.
 *
 * @author Steve Ebersole
 */
public interface TypeDetailsSwitch<T> {
	/**
	 * Main entry into the generics interpretation, with delegation to the defined case methods
	 */
	@Nullable
	static <T> T switchType(@Nonnull TypeDetails typeDetails, @Nonnull TypeDetailsSwitch<T> switcher, @Nullable ModelsContext modelsContext) {
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
	@Nullable
	T caseClass(@Nonnull ClassTypeDetails classType, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of a primitive
	 */
	@Nullable
	T casePrimitive(@Nonnull PrimitiveTypeDetails primitiveType, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of void (or Void)
	 */
	@Nullable
	T caseVoid(@Nonnull VoidTypeDetails voidType, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of a parameterized type
	 */
	@Nullable
	T caseParameterizedType(@Nonnull ParameterizedTypeDetails parameterizedType, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of a wildcard type
	 */
	@Nullable
	T caseWildcardType(@Nonnull WildcardTypeDetails wildcardType, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of a type variable
	 */
	@Nullable
	T caseTypeVariable(@Nonnull TypeVariableDetails typeVariable, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of a reference to a type variable
	 */
	@Nullable
	T caseTypeVariableReference(@Nonnull TypeVariableReferenceDetails typeVariableReference, @Nullable ModelsContext modelsContext);

	/**
	 * Handle the case of an array
	 */
	@Nullable
	T caseArrayType(@Nonnull ArrayTypeDetails arrayType, @Nullable ModelsContext modelsContext);

	/**
	 * Handle any other cases
	 */
	@Nullable
	T defaultCase(@Nonnull TypeDetails type, @Nullable ModelsContext modelsContext);
}
