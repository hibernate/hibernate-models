/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.annotations;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.IndexView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * Tests for annotations that use themselves
 *
 * @author Steve Ebersole
 */
public class SelfReferenceTests {
	@Test
	void testWithJandex() {
		testSelfReferencingAnnotation( buildJandexIndex( SimpleClass.class ) );
	}

	@Test
	void testWithoutJandex() {
		testSelfReferencingAnnotation( null );
	}

	private void testSelfReferencingAnnotation(IndexView jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, SimpleClass.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleClass.class.getName() );
		assertThat( classDetails.hasAnnotationUsage( SelfReferencingAnnotation.class, buildingContext ) ).isTrue();

		final AnnotationDescriptor<SelfReferencingAnnotation> descriptor = descriptorRegistry.getDescriptor( SelfReferencingAnnotation.class );
		assertThat( descriptor.hasAnnotationUsage( SelfReferencingAnnotation.class, buildingContext ) ).isTrue();
	}

	@SelfReferencingAnnotation
	public static class SimpleClass {
	}
}
