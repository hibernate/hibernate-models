/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import org.hibernate.models.ModelsException;
import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.jdk.AnnotationDescriptorImpl;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Steve Ebersole
 */
public class RegistryTests {
	@Test
	void testImmutableRegistry() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext();

		final AnnotationDescriptorRegistryStandard base = new AnnotationDescriptorRegistryStandard( buildingContext );
		base.register( new AnnotationDescriptorImpl<>( Annotation1.class, buildingContext ) );
		assertThat( base.getDescriptor( Annotation1.class ) ).isNotNull();

		final AnnotationDescriptorRegistry immutableCopy = base.makeImmutableCopy();
		assertThat( immutableCopy.getDescriptor( Annotation1.class ) ).isNotNull();

		// this should succeed
		base.resolveDescriptor( Annotation2.class, annotationType -> new AnnotationDescriptorImpl<>( annotationType, buildingContext ) );

		// this should fail
		try {
			immutableCopy.resolveDescriptor( Annotation2.class, annotationType -> new AnnotationDescriptorImpl<>( annotationType, buildingContext ) );
			fail( "Expecting an exception" );
		}
		catch (ModelsException expected) {
			assertThat( expected.getMessage() ).contains( "immutable" );
		}
	}

	public @interface Annotation1 {

	}

	public @interface Annotation2 {

	}
}
