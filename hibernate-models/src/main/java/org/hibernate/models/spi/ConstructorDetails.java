/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Constructor;
import java.util.List;

import jakarta.annotation.Nonnull;

import static org.hibernate.models.spi.AnnotationTarget.Kind.CONSTRUCTOR;

/**
 * Models a {@linkplain Constructor constructor} in a {@linkplain ClassDetails class}.
 *
 * @since 1.3
 * @author Steve Ebersole
 */
public interface ConstructorDetails extends AnnotationTarget {
	@Nonnull
	@Override
	default Kind getKind() {
		return CONSTRUCTOR;
	}

	/**
	 * The class which declares this constructor
	 */
	@Nonnull
	ClassDetails getDeclaringType();

	/**
	 * The argument types for this constructor.
	 */
	@Nonnull
	List<ClassDetails> getArgumentTypes();

	@Nonnull
	@Override
	default ClassDetails getContainer(@Nonnull ModelsContext modelsContext) {
		return getDeclaringType();
	}

	/**
	 * Access to the underlying {@linkplain Constructor}.
	 */
	@Nonnull
	Constructor<?> toJavaConstructor();

	@Nonnull
	@Override
	default ConstructorDetails asConstructorDetails() {
		return this;
	}
}
