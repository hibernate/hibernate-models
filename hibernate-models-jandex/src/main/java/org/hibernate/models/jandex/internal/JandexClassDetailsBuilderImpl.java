/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.jandex.internal;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.IndexView;

/**
 * @author Steve Ebersole
 */
public class JandexClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final IndexView jandexIndex;

	public JandexClassDetailsBuilderImpl(IndexView jandexIndex, SourceModelBuildingContext buildingContext) {
		this.jandexIndex = jandexIndex;
	}

	@Override
	public ClassDetails buildClassDetails(String name, SourceModelBuildingContext buildingContext) {
		return JandexBuilders.buildDetailsFromIndex( name, jandexIndex, buildingContext );
	}
}
