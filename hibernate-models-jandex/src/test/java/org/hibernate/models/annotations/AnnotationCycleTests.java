/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationCycleTests {
	@Test
	void testWithJandex() {
		testAnnotationCycle( buildJandexIndex( SelfReferenceTests.SimpleClass.class ) );
	}

	@Test
	void testWithoutJandex() {
		testAnnotationCycle( null );
	}

	private void testAnnotationCycle(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, SimpleClass.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleClass.class.getName() );
		assertThat( classDetails.hasDirectAnnotationUsage( A.class ) ).isTrue();
		assertThat( classDetails.hasDirectAnnotationUsage( B.class ) ).isTrue();

		final AnnotationDescriptor<A> aDescriptor = descriptorRegistry.getDescriptor( A.class );
		final AnnotationDescriptor<B> bDescriptor = descriptorRegistry.getDescriptor( B.class );
		assertThat( aDescriptor.hasDirectAnnotationUsage( B.class ) ).isTrue();
		assertThat( bDescriptor.hasDirectAnnotationUsage( A.class ) ).isTrue();
	}

	@B
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface A {
	}

	@A
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface B {
	}

	@A @B
	public static class SimpleClass {
	}
}
