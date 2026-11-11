/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.type.TypeDefinition;

/**
 * @author Steve Ebersole
 */
public interface TypeSwitch<T> {
	@Nullable
	T caseClass(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T casePrimitive(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T caseVoid(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T caseParameterizedType(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T caseWildcardType(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T caseTypeVariable(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T caseTypeVariableReference(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T caseArrayType(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);

	@Nullable
	T defaultCase(@Nonnull TypeDefinition typeDescription, @Nonnull ModelsContext modelsContext);
}
