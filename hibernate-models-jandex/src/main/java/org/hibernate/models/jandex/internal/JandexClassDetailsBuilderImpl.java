/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.IndexView;

/**
 * @author Steve Ebersole
 */
public class JandexClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final IndexView jandexIndex;

	@SuppressWarnings("unused")
	public JandexClassDetailsBuilderImpl(IndexView jandexIndex, ModelsContext modelsContext) {
		this.jandexIndex = jandexIndex;
	}

	@Override
	public ClassDetails buildClassDetails(String name, ModelsContext modelsContext) {
		return JandexBuilders.buildDetailsFromIndex( name, jandexIndex, modelsContext );
	}
}
