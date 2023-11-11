/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.members;

import org.hibernate.models.MixedSourcesTests;
import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for modeling methods
 *
 * @author Steve Ebersole
 */
public class FieldDetailsTests {
	@Test
	void testMethodDetails() {
		final Index index = SourceModelTestHelper.buildJandexIndex( RandomClass.class );
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				MixedSourcesTests.Entity1.class,
				MixedSourcesTests.Embeddable1.class
		);

		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.findClassDetails( RandomClass.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getFields() ).hasSize( 2 );

		classDetails.forEachField( (pos, field) -> {
			if ( field.getName().equals( "property" ) ) {
				assertThat( field.resolveAttributeName() ).isEqualTo( "property" );
				assertThat( field.getType().toJavaClass() ).isEqualTo( Integer.class );
			}
			else {
				assertThat( field.resolveAttributeName() ).isEqualTo( "something" );
				assertThat( field.getType().toJavaClass() ).isEqualTo( Object.class );
			}
		} );
	}


	public static class RandomClass {
		public Integer property;
		public Object something;
	}
}
