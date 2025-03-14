/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final SourceModelBuildingContextImpl modelContext;

	public ClassDetailsBuilderImpl(SourceModelBuildingContextImpl modelContext) {
		this.modelContext = modelContext;
	}

	@Override
	public ClassDetails buildClassDetails(String name, SourceModelBuildingContext buildingContext) {
		assert buildingContext == modelContext;
		return ModelBuilders.buildDetails( name, modelContext );
	}
}
