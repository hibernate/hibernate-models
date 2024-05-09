/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

import org.hibernate.models.annotations.AttributeAccessTests;
import org.hibernate.models.annotations.EverythingBagel;
import org.hibernate.models.annotations.Nested;
import org.hibernate.models.annotations.SimpleEntity;
import org.hibernate.models.annotations.Status;
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

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );
		classDetails.render( buildingContext );
	}

	@Test
	void testRendering2() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( (org.jboss.jandex.Index) null, SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		System.out.println( classDetails.renderToString( buildingContext ) );
	}


	@EverythingBagel(
			theString = "hello",
			theEnum = Status.ACTIVE,
			theBoolean = true,
			theByte = 1,
			theShort = 2,
			theInteger = 3,
			theLong = 4L,
			theFloat = 5.1F,
			theDouble = 6.2,
			theClass = AttributeAccessTests.TheClass.class,
			theNested = @Nested(),
			theNesteds = {@Nested(), @Nested()},
			theStrings = {"a", "b", "c"}
	)
	public static class SimpleClass {
	}
}
