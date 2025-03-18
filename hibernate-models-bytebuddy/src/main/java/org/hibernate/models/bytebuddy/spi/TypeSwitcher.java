/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;

/**
 * @author Steve Ebersole
 */
public class TypeSwitcher {
	private static final TypeDescription VOID = TypeDescription.ForLoadedType.of( void.class );

	public static <T> T switchType(TypeDefinition typeDescription, TypeSwitch<T> typeSwitch, SourceModelBuildingContext buildingContext) {
		if ( VOID.equals( typeDescription ) ) {
			return typeSwitch.caseVoid( typeDescription, buildingContext );
		}

		if ( typeDescription.isPrimitive() ) {
			return typeSwitch.casePrimitive( typeDescription, buildingContext );
		}

		if ( typeDescription.isArray() ) {
			return typeSwitch.caseArrayType( typeDescription, buildingContext );
		}

		switch( typeDescription.getSort() ) {
			case NON_GENERIC -> {
				return typeSwitch.caseClass( typeDescription, buildingContext );
			}
			case GENERIC_ARRAY -> {
				return typeSwitch.caseArrayType( typeDescription, buildingContext );
			}
			case PARAMETERIZED -> {
				return typeSwitch.caseParameterizedType( typeDescription, buildingContext );
			}
			case WILDCARD -> {
				return typeSwitch.caseWildcardType( typeDescription, buildingContext );
			}
			case VARIABLE -> {
				return typeSwitch.caseTypeVariable( typeDescription, buildingContext );
			}
			case VARIABLE_SYMBOLIC -> {
				return typeSwitch.caseTypeVariableReference( typeDescription, buildingContext );
			}
			default -> {
				return typeSwitch.defaultCase( typeDescription, buildingContext );
			}
		}
	}
}
