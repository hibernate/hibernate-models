/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

/**
 * A scope for annotation usages.  Differs from {@linkplain AnnotationTarget} in that this
 * is a "registry" of usages that are shareable.
 * <p/>
 * Generally speaking, this equates to global and package scope.
 *
 * @author Steve Ebersole
 */
public interface SharedAnnotationScope {
	<A extends Annotation> List<AnnotationUsage<A>> getAllAnnotationUsages(Class<A> annotationType);

	default <A extends Annotation> List<AnnotationUsage<A>> getAllAnnotationUsages(AnnotationDescriptor<A> annotationDescriptor) {
		return getAllAnnotationUsages( annotationDescriptor.getAnnotationType() );
	}

	<A extends Annotation> void forEachAnnotationUsage(Class<A> annotationType, Consumer<AnnotationUsage<A>> consumer);

	default <A extends Annotation> void forEachAnnotationUsage(
			AnnotationDescriptor<A> annotationDescriptor,
			Consumer<AnnotationUsage<A>> consumer) {
		forEachAnnotationUsage( annotationDescriptor.getAnnotationType(), consumer );
	}
}
