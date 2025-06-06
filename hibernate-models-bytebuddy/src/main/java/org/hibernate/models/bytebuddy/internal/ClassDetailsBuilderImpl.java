/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final ByteBuddyModelsContext modelContext;

	public ClassDetailsBuilderImpl(ByteBuddyModelsContext modelContext) {
		this.modelContext = modelContext;
	}

	@Override
	public ClassDetails buildClassDetails(String name, ModelsContext modelsContext) {
		assert modelsContext == modelContext;
		return ByteBuddyBuilders.buildDetails( name, modelContext );
	}
}
