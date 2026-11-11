/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;

/**
 * @author Steve Ebersole
 */
public class TypeSwitcher {
	private static final TypeDescription VOID = TypeDescription.ForLoadedType.of( void.class );

	@Nullable
	public static <T> T switchType(@Nonnull TypeDefinition typeDescription, @Nonnull TypeSwitch<T> typeSwitch, @Nonnull ModelsContext modelsContext) {
		if ( VOID.equals( typeDescription ) ) {
			return typeSwitch.caseVoid( typeDescription, modelsContext );
		}

		if ( typeDescription.isPrimitive() ) {
			return typeSwitch.casePrimitive( typeDescription, modelsContext );
		}

		if ( typeDescription.isArray() ) {
			return typeSwitch.caseArrayType( typeDescription, modelsContext );
		}

		switch( typeDescription.getSort() ) {
			case NON_GENERIC -> {
				return typeSwitch.caseClass( typeDescription, modelsContext );
			}
			case GENERIC_ARRAY -> {
				return typeSwitch.caseArrayType( typeDescription, modelsContext );
			}
			case PARAMETERIZED -> {
				return typeSwitch.caseParameterizedType( typeDescription, modelsContext );
			}
			case WILDCARD -> {
				return typeSwitch.caseWildcardType( typeDescription, modelsContext );
			}
			case VARIABLE -> {
				return typeSwitch.caseTypeVariable( typeDescription, modelsContext );
			}
			case VARIABLE_SYMBOLIC -> {
				return typeSwitch.caseTypeVariableReference( typeDescription, modelsContext );
			}
			default -> {
				return typeSwitch.defaultCase( typeDescription, modelsContext );
			}
		}
	}
}
