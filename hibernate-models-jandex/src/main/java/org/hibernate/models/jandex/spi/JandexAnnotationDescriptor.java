/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.jandex.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;

import org.jboss.jandex.AnnotationInstance;

/**
 * @author Steve Ebersole
 */
public interface JandexAnnotationDescriptor<A extends Annotation> extends AnnotationDescriptor<A> {
	/**
	 * Create a usage from the Jandex representation.
	 */
	A createUsage(AnnotationInstance jandexAnnotation, JandexModelBuildingContext context);
}
