/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import org.hibernate.models.spi.ModelsContext;


import net.bytebuddy.description.type.TypeDefinition;

/**
 * @author Steve Ebersole
 */
public interface TypeSwitch<T> {
	T caseClass(TypeDefinition typeDescription, ModelsContext modelsContext);

	T casePrimitive(TypeDefinition typeDescription, ModelsContext modelsContext);

	T caseVoid(TypeDefinition typeDescription, ModelsContext modelsContext);

	T caseParameterizedType(TypeDefinition typeDescription, ModelsContext modelsContext);

	T caseWildcardType(TypeDefinition typeDescription, ModelsContext modelsContext);

	T caseTypeVariable(TypeDefinition typeDescription, ModelsContext modelsContext);

	T caseTypeVariableReference(TypeDefinition typeDescription, ModelsContext modelsContext);

	T caseArrayType(TypeDefinition typeDescription, ModelsContext modelsContext);

	T defaultCase(TypeDefinition typeDescription, ModelsContext modelsContext);
}
