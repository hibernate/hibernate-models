/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.Map;

import jakarta.annotation.Nonnull;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptor<A extends Annotation, C extends A> extends AnnotationDescriptor<A> {
	/**
	 * The mutable contract for the given annotation
	 */
	@Nonnull
	Class<C> getMutableAnnotationType();

	@Nonnull
	@Override
	C createUsage(@Nonnull ModelsContext modelsContext);

	@Nonnull
	@Override
	C createUsage(@Nonnull A jdkAnnotation, @Nonnull ModelsContext modelsContext);

	@Nonnull
	@Override
	C createUsage(@Nonnull Map<String, Object> attributeValues, @Nonnull ModelsContext modelsContext);
}
