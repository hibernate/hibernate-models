/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AnnotationUsage;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationTarget extends AnnotationTarget {
	void clearAnnotationUsages();

	<X extends Annotation> void removeAnnotationUsage(Class<X> annotationType);

	<X extends Annotation> void addAnnotationUsage(AnnotationUsage<X> annotationUsage);

	default <X extends Annotation> void addAnnotationUsages(List<AnnotationUsage<X>> annotationUsages) {
		annotationUsages.forEach( this::addAnnotationUsage );
	}
}
