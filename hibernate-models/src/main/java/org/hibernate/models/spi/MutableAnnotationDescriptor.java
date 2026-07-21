/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptor<A extends Annotation, C extends A> extends AnnotationDescriptor<A> {
	/**
	 * The mutable contract for the given annotation
	 */
	Class<C> getMutableAnnotationType();

	@Override
	C createUsage(ModelsContext modelsContext);

	@Override
	C createUsage(A jdkAnnotation, ModelsContext modelsContext);

	@Override
	C createUsage(Map<String, Object> attributeValues, ModelsContext modelsContext);
}
