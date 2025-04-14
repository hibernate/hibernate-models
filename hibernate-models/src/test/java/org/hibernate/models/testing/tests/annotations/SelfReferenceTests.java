/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;

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
		final ModelsContext modelsContext = createModelContext( SimpleClass.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final AnnotationDescriptorRegistry descriptorRegistry = modelsContext.getAnnotationDescriptorRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleClass.class.getName() );
		assertThat( classDetails.hasAnnotationUsage( SelfReferencingAnnotation.class, modelsContext ) ).isTrue();

		final AnnotationDescriptor<SelfReferencingAnnotation> descriptor = descriptorRegistry.getDescriptor( SelfReferencingAnnotation.class );
		assertThat( descriptor.hasAnnotationUsage( SelfReferencingAnnotation.class, modelsContext ) ).isTrue();
	}

	@SelfReferencingAnnotation
	public static class SimpleClass {
	}
}
