/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.UnknownClassException;

/**
 * Contract for creating the ClassDetails for a Java type we have not yet seen
 * as part of {@link ClassDetailsRegistry#resolveClassDetails}
 *
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface ClassDetailsBuilder {
	/**
	 * Build a ClassDetails descriptor for a class with the given name
	 *
	 * @throws UnknownClassException To indicate that the given class name is not valid
	 */
	@Nullable
	ClassDetails buildClassDetails(@Nonnull String name, @Nonnull ModelsContext modelsContext);
}
