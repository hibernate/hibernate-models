/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Serial form of a AnnotationDescriptor
 *
 * @author Steve Ebersole
 */
public class SerialAnnotationDescriptor<A extends Annotation> implements SerialForm<AnnotationDescriptor<A>> {
	private final Class<A> annotationType;

	public SerialAnnotationDescriptor(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public AnnotationDescriptor<A> fromSerialForm(SourceModelBuildingContext context) {
		return new StandardAnnotationDescriptor<>( annotationType, context );
	}
}
