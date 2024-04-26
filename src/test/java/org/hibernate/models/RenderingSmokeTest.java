/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

import org.hibernate.models.annotations.SimpleEntity;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class RenderingSmokeTest {
	@Test
	void testRendering() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( (Index) null, SimpleEntity.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry()
				.resolveClassDetails( SimpleEntity.class.getName() );
		classDetails.render();
	}
}
