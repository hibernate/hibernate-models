/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;


import net.bytebuddy.description.type.TypeDefinition;

/**
 * @author Steve Ebersole
 */
public interface TypeSwitch<T> {
	T caseClass(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T casePrimitive(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T caseVoid(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T caseParameterizedType(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T caseWildcardType(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T caseTypeVariable(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T caseTypeVariableReference(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T caseArrayType(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);

	T defaultCase(TypeDefinition typeDescription, SourceModelBuildingContext buildingContext);
}
