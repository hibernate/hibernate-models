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

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class NestedValueExtractor<A extends Annotation> extends AbstractValueExtractor<AnnotationUsage<A>> {
	private final NestedValueWrapper<A> wrapper;

	public NestedValueExtractor(NestedValueWrapper<A> wrapper) {
		this.wrapper = wrapper;
	}

	public NestedValueExtractor(AnnotationDescriptor<A> descriptor) {
		this( new NestedValueWrapper<>( descriptor ) );
	}

	@Override
	protected AnnotationUsage<A> extractAndWrap(
			AnnotationValue jandexValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		return wrapper.wrap( jandexValue, target, buildingContext );
	}
}
