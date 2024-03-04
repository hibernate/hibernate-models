/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * Support for switch-style handling of {@linkplain org.hibernate.models.spi.TypeDetails}
 *
 * @author Steve Ebersole
 */
public class TypeDetailsSwitcher {
	public static <T> T switchType(TypeDetails type, TypeDetailsSwitch<T> typeSwitch) {

		switch( type.getTypeKind() ) {
			case CLASS -> {
				return typeSwitch.caseClass( type.asClassType() );
			}
			case PRIMITIVE -> {
				return typeSwitch.casePrimitive( type.asPrimitiveType() );
			}
			case VOID -> {
				return typeSwitch.caseVoid( type.asVoidType() );
			}
			case ARRAY -> {
				return typeSwitch.caseArrayType( type.asArrayType() );
			}
			case PARAMETERIZED_TYPE -> {
				return typeSwitch.caseParameterizedType( type.asParameterizedType() );
			}
			case WILDCARD_TYPE -> {
				return typeSwitch.caseWildcardType( type.asWildcardType() );
			}
			case TYPE_VARIABLE -> {
				return typeSwitch.caseTypeVariable( type.asTypeVariable() );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				return typeSwitch.caseTypeVariableReference( type.asTypeVariableReference() );
			}
			default -> {
				return typeSwitch.defaultCase( type );
			}
		}
	}
}
