/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class NestedValueConverter<A extends Annotation> implements JdkValueConverter<A> {
	private final AnnotationDescriptor<A> descriptor;

	public NestedValueConverter(AnnotationDescriptor<A> descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public A convert(A rawValue, SourceModelBuildingContext modelContext) {
		return descriptor.createUsage( rawValue, modelContext );
	}
}
