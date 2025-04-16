/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import java.lang.annotation.Annotation;

import org.hibernate.models.jandex.internal.AnnotationUsageBuilder;
import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as an annotation
 *
 * @author Steve Ebersole
 */
public class JandexNestedValueConverter<A extends Annotation> implements JandexValueConverter<A> {
	private final AnnotationDescriptor<A> descriptor;

	public JandexNestedValueConverter(AnnotationDescriptor<A> descriptor) {
		assert descriptor != null : "AnnotationDescriptor was null";
		this.descriptor = descriptor;
	}

	@Override
	public A convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		final AnnotationInstance nested = jandexValue.asNested();
		assert nested.target() == null;
		return AnnotationUsageBuilder.makeUsage( nested, descriptor, modelContext );
	}
}
