/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptor<A extends Annotation, C extends A> extends AnnotationDescriptor<A> {
	/**
	 * The mutable contract for the given annotation
	 */
	Class<C> getMutableAnnotationType();
}
