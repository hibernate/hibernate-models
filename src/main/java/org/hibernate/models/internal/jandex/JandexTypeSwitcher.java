/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.Type;

/**
 * @author Steve Ebersole
 */
public class JandexTypeSwitcher {
	public static <T> T switchType(Type type, JandexTypeSwitch<T> typeSwitch, SourceModelBuildingContext buildingContext) {
		switch( type.kind() ) {
			case CLASS -> {
				return typeSwitch.caseClass( type.asClassType(), buildingContext );
			}
			case PRIMITIVE -> {
				return typeSwitch.casePrimitive( type.asPrimitiveType(), buildingContext );
			}
			case VOID -> {
				return typeSwitch.caseVoid( type.asVoidType(), buildingContext );
			}
			case ARRAY -> {
				return typeSwitch.caseArrayType( type.asArrayType(), buildingContext );
			}
			case PARAMETERIZED_TYPE -> {
				return typeSwitch.caseParameterizedType( type.asParameterizedType(), buildingContext );
			}
			case WILDCARD_TYPE -> {
				return typeSwitch.caseWildcardType( type.asWildcardType(), buildingContext );
			}
			case TYPE_VARIABLE -> {
				return typeSwitch.caseTypeVariable( type.asTypeVariable(), buildingContext );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				return typeSwitch.caseTypeVariableReference( type.asTypeVariableReference(), buildingContext );
			}
			case UNRESOLVED_TYPE_VARIABLE -> {
				throw new UnsupportedOperationException( "Not yet implemented" );
			}
			default -> {
				return typeSwitch.defaultCase( type, buildingContext );
			}
		}
	}
}
