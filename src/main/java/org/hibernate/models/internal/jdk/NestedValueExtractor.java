/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

/**
 * @author Steve Ebersole
 */
public class NestedValueExtractor<A extends Annotation> extends AbstractValueExtractor<AnnotationUsage<A>,A> {
	private final ValueWrapper<AnnotationUsage<A>,A> wrapper;

	public NestedValueExtractor(ValueWrapper<AnnotationUsage<A>, A> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected AnnotationUsage<A> wrap(
			A rawValue,
			AttributeDescriptor<AnnotationUsage<A>> attributeDescriptor,
			SourceModelBuildingContext buildingContext) {
		return wrapper.wrap( rawValue, buildingContext );
	}
}
