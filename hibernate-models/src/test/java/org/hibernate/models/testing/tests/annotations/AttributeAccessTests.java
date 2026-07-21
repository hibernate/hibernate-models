/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.annotations.EverythingBagel;
import org.hibernate.models.testing.annotations.Nested;
import org.hibernate.models.testing.annotations.Status;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class AttributeAccessTests {
	@Test
	void testAttributeAccess() {
		final ModelsContext modelsContext = createModelContext( TheClass.class );
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().getClassDetails( TheClass.class.getName() );
		final EverythingBagel annotationUsage = classDetails.getAnnotationUsage( EverythingBagel.class, modelsContext );

		assertThat( annotationUsage.theString() ).isEqualTo( "hello" );
		assertThat( annotationUsage.theEnum() ).isEqualTo( Status.ACTIVE );
		assertThat( annotationUsage.theBoolean() ).isEqualTo( true );
		assertThat( annotationUsage.theByte() ).isEqualTo( (byte) 1 );
		assertThat( annotationUsage.theShort() ).isEqualTo( (short) 2 );
		assertThat( annotationUsage.theInteger() ).isEqualTo( 3 );
		assertThat( annotationUsage.theLong() ).isEqualTo( 4L );
		assertThat( annotationUsage.theFloat() ).isEqualTo( 5.1F );
		assertThat( annotationUsage.theDouble() ).isEqualTo( 6.2 );
		assertThat( annotationUsage.theBooleans() ).isExactlyInstanceOf( boolean[].class ).containsExactly( true, false );
		assertThat( annotationUsage.theBytes() ).isExactlyInstanceOf( byte[].class ).containsExactly( (byte) 1, (byte) 2 );
		assertThat( annotationUsage.theCharacters() ).isExactlyInstanceOf( char[].class ).containsExactly( 'a', 'b' );
		assertThat( annotationUsage.theShorts() ).isExactlyInstanceOf( short[].class ).containsExactly( (short) 2, (short) 3 );
		assertThat( annotationUsage.theIntegers() ).isExactlyInstanceOf( int[].class ).containsExactly( 3, 4 );
		assertThat( annotationUsage.theLongs() ).isExactlyInstanceOf( long[].class ).containsExactly( 4L, 5L );
		assertThat( annotationUsage.theFloats() ).isExactlyInstanceOf( float[].class ).containsExactly( 5.1F, 5.2F );
		assertThat( annotationUsage.theDoubles() ).isExactlyInstanceOf( double[].class ).containsExactly( 6.2, 6.3 );

		assertThat( modelsContext.getAnnotationDescriptorRegistry()
				.getDescriptor( EverythingBagel.class )
				.getAttribute( "theIntegers" )
				.getTypeDescriptor()
				.getValueType() ).isEqualTo( int[].class );

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
			theBooleans = {true, false},
			theBytes = {1, 2},
			theCharacters = {'a', 'b'},
			theShorts = {2, 3},
			theIntegers = {3, 4},
			theLongs = {4L, 5L},
			theFloats = {5.1F, 5.2F},
			theDoubles = {6.2, 6.3},
			theClass = TheClass.class,
			theNested = @Nested(),
			theNesteds = {@Nested(), @Nested()},
			theStrings = {"a", "b", "c"}
	)
	public static class TheClass {

	}

}
