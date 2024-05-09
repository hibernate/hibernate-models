/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueExtractor;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractValueExtractor<W> implements JandexValueExtractor<W> {

	protected abstract W extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext);

	@Override
	public W extractValue(
			AnnotationInstance annotation,
			String attributeName,
			SourceModelBuildingContext buildingContext) {
		final AnnotationValue jandexValue = resolveAnnotationValue( annotation, attributeName, buildingContext );
		assert jandexValue != null;
		return extractAndWrap( jandexValue, buildingContext );
	}

	protected AnnotationValue resolveAnnotationValue(
			AnnotationInstance annotation,
			String attributeName,
			SourceModelBuildingContext buildingContext) {
		final AnnotationValue explicitValue = annotation.value( attributeName );
		if ( explicitValue != null ) {
			return explicitValue;
		}

		return annotation.valueWithDefault( buildingContext.getJandexIndex(), attributeName );
	}
}
