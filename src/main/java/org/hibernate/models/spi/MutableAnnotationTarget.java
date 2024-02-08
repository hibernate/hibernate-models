/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Extension of AnnotationTarget which allows manipulation of the annotations
 *
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
