/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.TypeVariableReference;
import org.jboss.jandex.VoidType;
import org.jboss.jandex.WildcardType;

/**
 * @author Steve Ebersole
 */
public interface JandexTypeSwitch<T> {
	T caseClass(ClassType classType, SourceModelBuildingContext buildingContext);

	T casePrimitive(PrimitiveType primitiveType, SourceModelBuildingContext buildingContext);

	T caseVoid(VoidType voidType, SourceModelBuildingContext buildingContext);

	T caseParameterizedType(ParameterizedType parameterizedType, SourceModelBuildingContext buildingContext);

	T caseWildcardType(WildcardType wildcardType, SourceModelBuildingContext buildingContext);

	T caseTypeVariable(TypeVariable typeVariable, SourceModelBuildingContext buildingContext);

	T caseTypeVariableReference(TypeVariableReference typeVariableReference, SourceModelBuildingContext buildingContext);

	T caseArrayType(ArrayType genericArrayType, SourceModelBuildingContext buildingContext);

	T defaultCase(Type t, SourceModelBuildingContext buildingContext);
}
