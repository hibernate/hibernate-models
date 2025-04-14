/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexModelsContext;
import org.hibernate.models.jandex.spi.JandexValueExtractor;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractValueExtractor<W> implements JandexValueExtractor<W> {

	protected abstract W extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext);

	@Override
	public W extractValue(
			AnnotationInstance annotation,
			String attributeName,
			ModelsContext modelsContext) {
		final AnnotationValue jandexValue = resolveAnnotationValue( annotation, attributeName, modelsContext );
		assert jandexValue != null;
		return extractAndWrap( jandexValue, modelsContext );
	}

	protected AnnotationValue resolveAnnotationValue(
			AnnotationInstance annotation,
			String attributeName,
			ModelsContext modelsContext) {
		final AnnotationValue explicitValue = annotation.value( attributeName );
		if ( explicitValue != null ) {
			return explicitValue;
		}

		return annotation.valueWithDefault( modelsContext.as( JandexModelsContext.class ).getJandexIndex(), attributeName );
	}
}
