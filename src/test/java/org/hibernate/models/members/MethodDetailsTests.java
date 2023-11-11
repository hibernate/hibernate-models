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
import org.hibernate.models.spi.MethodDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for modeling methods
 *
 * @author Steve Ebersole
 */
public class MethodDetailsTests {
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
		assertThat( classDetails.getMethods() ).hasSize( 4 );

		classDetails.forEachMethod( (pos, method) -> {
			if ( method.getName().equals( "getProperty" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType() ).isSameAs( method.getReturnType() );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "property" );
			}
			else if ( method.getName().equals( "setProperty" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.SETTER );
				assertThat( method.getReturnType() ).isNull();
				assertThat( method.getArgumentTypes() ).hasSize( 1 );
				assertThat( method.getType() ).isSameAs( method.getArgumentTypes().get(0) );
			}
			else if ( method.getName().equals( "nothing" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.OTHER );
				assertThat( method.getReturnType() ).isNull();
				assertThat( method.getType() ).isNull();
				assertThat( method.getArgumentTypes() ).isEmpty();
			}
			else if ( method.getName().equals( "something" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.OTHER );
				assertThat( method.getReturnType() ).isNull();
				assertThat( method.getType() ).isNull();
				assertThat( method.getArgumentTypes() ).hasSize( 1 );
			}
			else {
				fail();
			}
		} );
	}


	public static class RandomClass {
		public Integer getProperty() { return null; }
		public void setProperty(Integer id) {}

		public void nothing() {}
		public void something(Object stuff) {}
	}
}
