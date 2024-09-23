/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import org.hibernate.models.annotations.EverythingBagel;
import org.hibernate.models.annotations.Nested;
import org.hibernate.models.annotations.Status;
import org.hibernate.models.rendering.internal.RenderingTargetCollectingImpl;
import org.hibernate.models.rendering.internal.RenderingTargetStreamImpl;
import org.hibernate.models.rendering.internal.SimpleRenderer;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class RenderingSmokeTest {
	@Test
	void testStreamRendering1() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (Index) null, SimpleEntity.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );

		// simple stdout renderer with default (2) indentation
		final SimpleRenderer renderer = new SimpleRenderer( new RenderingTargetStreamImpl( System.out ) );
		renderer.renderClass( classDetails, buildingContext );
	}

	@Test
	void testStreamRendering2() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (Index) null, SimpleEntity.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );

		// simple stdout renderer with specified indentation
		final SimpleRenderer renderer = new SimpleRenderer( new RenderingTargetStreamImpl( System.out, 4 ) );
		renderer.renderClass( classDetails, buildingContext );
	}

	@Test
	void testCollectingRendering1() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (org.jboss.jandex.Index) null, SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		final RenderingTargetCollectingImpl collectingTarget = new RenderingTargetCollectingImpl();
		final SimpleRenderer renderer = new SimpleRenderer( collectingTarget );
		renderer.renderClass( classDetails, buildingContext );

		System.out.println( collectingTarget.toString() );
	}

	@Test
	void testCollectingRendering2() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (org.jboss.jandex.Index) null, SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		final RenderingTargetCollectingImpl collectingTarget = new RenderingTargetCollectingImpl();
		final SimpleRenderer renderer = new SimpleRenderer( collectingTarget );
		renderer.renderClass( classDetails, buildingContext );

		collectingTarget.render( System.out );
	}

	@Test
	void testCollectingRendering3() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (org.jboss.jandex.Index) null, SimpleClass.class );
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
