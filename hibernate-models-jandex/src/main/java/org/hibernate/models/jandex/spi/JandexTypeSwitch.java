/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
	@Nullable
	T caseClass(@Nonnull ClassType classType, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#PRIMITIVE primitive types}.
	 */
	@Nullable
	T casePrimitive(@Nonnull PrimitiveType primitiveType, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#VOID void types}.
	 */
	@Nullable
	T caseVoid(@Nonnull VoidType voidType, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#PARAMETERIZED_TYPE parameterized types}.
	 */
	@Nullable
	T caseParameterizedType(@Nonnull ParameterizedType parameterizedType, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#WILDCARD_TYPE wildcard types}.
	 */
	@Nullable
	T caseWildcardType(@Nonnull WildcardType wildcardType, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#TYPE_VARIABLE type variable types}.
	 */
	@Nullable
	T caseTypeVariable(@Nonnull TypeVariable typeVariable, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#TYPE_VARIABLE_REFERENCE type variable reference types}.
	 */
	@Nullable
	T caseTypeVariableReference(@Nonnull TypeVariableReference typeVariableReference, @Nonnull ModelsContext modelsContext);

	/**
	 * Handling for {@linkplain Type.Kind#ARRAY array types}.
	 */
	@Nullable
	T caseArrayType(@Nonnull ArrayType genericArrayType, @Nonnull ModelsContext modelsContext);

	/**
	 * Fallback handling.
	 */
	@Nullable
	T defaultCase(@Nonnull Type t, @Nonnull ModelsContext modelsContext);
}
