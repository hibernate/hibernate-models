/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.ModelsContext;

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
 * Specific handling for the various Jandex {@linkplain Type#kind() type kinds}.
 *
 * @see JandexTypeSwitcher
 *
 * @author Steve Ebersole
 */
public interface JandexTypeSwitch<T> {
	/**
	 * Handling for {@linkplain Type.Kind#CLASS class types}.
	 */
	T caseClass(ClassType classType, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#PRIMITIVE primitive types}.
	 */
	T casePrimitive(PrimitiveType primitiveType, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#VOID void types}.
	 */
	T caseVoid(VoidType voidType, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#PARAMETERIZED_TYPE parameterized types}.
	 */
	T caseParameterizedType(ParameterizedType parameterizedType, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#WILDCARD_TYPE wildcard types}.
	 */
	T caseWildcardType(WildcardType wildcardType, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#TYPE_VARIABLE type variable types}.
	 */
	T caseTypeVariable(TypeVariable typeVariable, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#TYPE_VARIABLE_REFERENCE type variable reference types}.
	 */
	T caseTypeVariableReference(TypeVariableReference typeVariableReference, ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#ARRAY array types}.
	 */
	T caseArrayType(ArrayType genericArrayType, ModelsContext modelsContext);

	/**
	 * Fallback handling.
	 */
	T defaultCase(Type t, ModelsContext modelsContext);
}
