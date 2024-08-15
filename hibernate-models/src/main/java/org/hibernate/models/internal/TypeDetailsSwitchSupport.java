/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.PrimitiveTypeDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableReferenceDetails;
import org.hibernate.models.spi.VoidTypeDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

/**
 * @author Steve Ebersole
 */
public class TypeDetailsSwitchSupport<T> implements TypeDetailsSwitch<T> {
	@Override
	public T caseVoid(VoidTypeDetails voidType) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T casePrimitive(PrimitiveTypeDetails primitiveType) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T caseClass(ClassTypeDetails classType) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T caseArrayType(ArrayTypeDetails arrayType) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T caseParameterizedType(ParameterizedTypeDetails parameterizedType) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T caseWildcardType(WildcardTypeDetails wildcardType) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T caseTypeVariable(TypeVariableDetails typeVariable) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}

	@Override
	public T defaultCase(TypeDetails type) {
		throw new UnsupportedOperationException( "Unexpected switch branch" );
	}
}
