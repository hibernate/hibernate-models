/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.StandardAnnotationDescriptor;
import org.hibernate.models.serial.spi.SerialAnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class SerialAnnotationDescriptorImpl<A extends Annotation> implements SerialAnnotationDescriptor<A> {
	private final Class<A> annotationType;

	public SerialAnnotationDescriptorImpl(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public AnnotationDescriptor<A> fromStorableForm(SourceModelBuildingContext context) {
		return new StandardAnnotationDescriptor<>( annotationType, context );
	}
}
