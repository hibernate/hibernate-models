/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.rendering.internal.RenderingTargetCollectingImpl;
import org.hibernate.models.rendering.internal.RenderingTargetStreamImpl;
import org.hibernate.models.rendering.internal.SimpleRenderer;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.annotations.EverythingBagel;
import org.hibernate.models.testing.annotations.Nested;
import org.hibernate.models.testing.annotations.Status;
import org.hibernate.models.testing.domain.SimpleEntity;

import org.junit.jupiter.api.Test;

import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class RenderingSmokeTest {
	@Test
	void testStreamRendering1() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleEntity.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );

		// simple stdout renderer with default (2) indentation
		final SimpleRenderer renderer = new SimpleRenderer( new RenderingTargetStreamImpl( System.out ) );
		renderer.renderClass( classDetails, buildingContext );
	}

	@Test
	void testStreamRendering2() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleEntity.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );

		// simple stdout renderer with specified indentation
		final SimpleRenderer renderer = new SimpleRenderer( new RenderingTargetStreamImpl( System.out, 4 ) );
		renderer.renderClass( classDetails, buildingContext );
	}

	@Test
	void testCollectingRendering1() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		final RenderingTargetCollectingImpl collectingTarget = new RenderingTargetCollectingImpl();
		final SimpleRenderer renderer = new SimpleRenderer( collectingTarget );
		renderer.renderClass( classDetails, buildingContext );

		System.out.println( collectingTarget.toString() );
	}

	@Test
	void testCollectingRendering2() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		final RenderingTargetCollectingImpl collectingTarget = new RenderingTargetCollectingImpl();
		final SimpleRenderer renderer = new SimpleRenderer( collectingTarget );
		renderer.renderClass( classDetails, buildingContext );

		collectingTarget.render( System.out );
	}

	@Test
	void testCollectingRendering3() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		final RenderingTargetCollectingImpl collectingTarget = new RenderingTargetCollectingImpl( 4 );
		final SimpleRenderer renderer = new SimpleRenderer( collectingTarget );
		renderer.renderClass( classDetails, buildingContext );

		collectingTarget.render( System.out );
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
			theClass = SimpleEntity.class,
			theNested = @Nested(),
			theNesteds = {@Nested(), @Nested()},
			theStrings = {"a", "b", "c"}
	)
	public static class SimpleClass {
	}
}
