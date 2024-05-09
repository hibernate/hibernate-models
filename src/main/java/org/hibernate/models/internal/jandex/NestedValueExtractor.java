/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class NestedValueExtractor<A extends Annotation> extends AbstractValueExtractor<A> {
	private final NestedValueConverter<A> wrapper;

	public NestedValueExtractor(NestedValueConverter<A> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected A extractAndWrap(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		return wrapper.convert( jandexValue, buildingContext );
	}
}
