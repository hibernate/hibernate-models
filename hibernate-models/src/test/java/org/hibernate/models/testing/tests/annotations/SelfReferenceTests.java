/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * Tests for annotations that use themselves
 *
 * @author Steve Ebersole
 */
public class SelfReferenceTests {
	@Test
	void testSelfReferencingAnnotation() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClass.class );
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
