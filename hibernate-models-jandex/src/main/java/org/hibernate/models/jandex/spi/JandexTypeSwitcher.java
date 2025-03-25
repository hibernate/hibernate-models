/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.Type;

/**
 * Coordinator for visiting Jandex {@linkplain Type types} and delegating
 * to a {@linkplain JandexTypeSwitch switch} based on the
 * {@linkplain Type#kind() type's kind}.
 *
 * @see JandexTypeSwitch
 *
 * @author Steve Ebersole
 */
public class JandexTypeSwitcher {
	/**
	 * Perform the visitation.
	 *
	 * @param type The Jandex type to visit.
	 * @param typeSwitch The switch, responsible for dealing with the specific cases.
	 * @param modelContext The context
	 */
	public static <T> T switchType(
			Type type,
			JandexTypeSwitch<T> typeSwitch,
			SourceModelBuildingContext modelContext) {
		switch( type.kind() ) {
			case CLASS -> {
				return typeSwitch.caseClass( type.asClassType(), modelContext );
			}
			case PRIMITIVE -> {
				return typeSwitch.casePrimitive( type.asPrimitiveType(), modelContext );
			}
			case VOID -> {
				return typeSwitch.caseVoid( type.asVoidType(), modelContext );
			}
			case ARRAY -> {
				return typeSwitch.caseArrayType( type.asArrayType(), modelContext );
			}
			case PARAMETERIZED_TYPE -> {
				return typeSwitch.caseParameterizedType( type.asParameterizedType(), modelContext );
			}
			case WILDCARD_TYPE -> {
				return typeSwitch.caseWildcardType( type.asWildcardType(), modelContext );
			}
			case TYPE_VARIABLE -> {
				return typeSwitch.caseTypeVariable( type.asTypeVariable(), modelContext );
			}
			case TYPE_VARIABLE_REFERENCE -> {
				return typeSwitch.caseTypeVariableReference( type.asTypeVariableReference(), modelContext );
			}
			case UNRESOLVED_TYPE_VARIABLE -> {
				throw new UnsupportedOperationException( "Not yet implemented" );
			}
			default -> {
				return typeSwitch.defaultCase( type, modelContext );
			}
		}
	}
}
