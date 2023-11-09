/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

/**
 * @author Steve Ebersole
 */
public class NestedValueWrapper<A extends Annotation> implements ValueWrapper<AnnotationUsage<A>,A> {
	private final AnnotationDescriptor<A> descriptor;

	public NestedValueWrapper(AnnotationDescriptor<A> descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public AnnotationUsage<A> wrap(A rawValue, AnnotationTarget target, SourceModelBuildingContext buildingContext) {
		return AnnotationUsageBuilder.makeUsage( rawValue, descriptor, target, buildingContext );

	}
}
