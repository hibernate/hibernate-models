/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Steve Ebersole
 */
public interface JdkTypeSwitch<T> {
	@Nullable
	T caseClass(@Nonnull Class<?> classType);

	@Nullable
	T caseParameterizedType(@Nonnull ParameterizedType parameterizedType);

	@Nullable
	T caseWildcardType(@Nonnull WildcardType wildcardType);

	@Nullable
	T caseTypeVariable(@Nonnull TypeVariable<?> typeVariable);

	@Nullable
	T caseGenericArrayType(@Nonnull GenericArrayType genericArrayType);

	@Nullable
	T defaultCase(@Nonnull Type t);
}
