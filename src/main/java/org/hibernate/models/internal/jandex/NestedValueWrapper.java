/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as an annotation
 *
 * @author Steve Ebersole
 */
public class NestedValueWrapper<A extends Annotation> implements ValueWrapper<AnnotationUsage<A>,AnnotationValue> {
	private final AnnotationDescriptor<A> descriptor;

	public NestedValueWrapper(AnnotationDescriptor<A> descriptor) {
		assert descriptor != null : "AnnotationDescriptor was null";
		this.descriptor = descriptor;
	}

	@Override
	public AnnotationUsage<A> wrap(
			AnnotationValue rawValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		final AnnotationInstance nested = rawValue.asNested();
		return AnnotationUsageBuilder.makeUsage( nested, descriptor, target, buildingContext );
	}
}
