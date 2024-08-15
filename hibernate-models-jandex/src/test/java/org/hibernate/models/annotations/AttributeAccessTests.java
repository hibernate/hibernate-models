/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class AttributeAccessTests {
	@Test
	void testAttributeAccess() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (Index) null, TheClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( TheClass.class.getName() );
		final EverythingBagel annotationUsage = classDetails.getAnnotationUsage( EverythingBagel.class, buildingContext );

		assertThat( annotationUsage.theString() ).isEqualTo( "hello" );
		assertThat( annotationUsage.theEnum() ).isEqualTo( Status.ACTIVE );
		assertThat( annotationUsage.theBoolean() ).isEqualTo( true );
		assertThat( annotationUsage.theByte() ).isEqualTo( (byte) 1 );
		assertThat( annotationUsage.theShort() ).isEqualTo( (short) 2 );
		assertThat( annotationUsage.theInteger() ).isEqualTo( 3 );
		assertThat( annotationUsage.theLong() ).isEqualTo( 4L );
		assertThat( annotationUsage.theFloat() ).isEqualTo( 5.1F );
		assertThat( annotationUsage.theDouble() ).isEqualTo( 6.2 );

		assertThat( annotationUsage.theClass() ).isEqualTo( TheClass.class );
		assertThat( annotationUsage.theNested() ).isNotNull();
		assertThat( annotationUsage.theStrings() ).containsExactly( "a", "b", "c" );
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
			theClass = TheClass.class,
			theNested = @Nested(),
			theNesteds = {@Nested(), @Nested()},
			theStrings = {"a", "b", "c"}
	)
	public static class TheClass {

	}

}
