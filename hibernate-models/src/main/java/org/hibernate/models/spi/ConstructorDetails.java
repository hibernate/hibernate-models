/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.hibernate.models.spi.AnnotationTarget.Kind.CONSTRUCTOR;

/**
 * Models a {@linkplain Constructor constructor} in a {@linkplain ClassDetails class}.
 *
 * @author Steve Ebersole
 */
public interface ConstructorDetails extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return CONSTRUCTOR;
	}

	/**
	 * The class which declares this constructor
	 */
	ClassDetails getDeclaringType();

	/**
	 * The argument types for this constructor.
	 */
	List<ClassDetails> getArgumentTypes();

	@Override
	default ClassDetails getContainer(ModelsContext modelsContext) {
		return getDeclaringType();
	}

	/**
	 * Access to the underlying {@linkplain Constructor}.
	 */
	Constructor<?> toJavaConstructor();

	@Override
	default ConstructorDetails asConstructorDetails() {
		return this;
	}
}
