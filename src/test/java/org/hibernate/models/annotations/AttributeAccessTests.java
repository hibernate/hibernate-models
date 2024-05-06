/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;

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
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( (Index) null, TheClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( TheClass.class.getName() );
		final AnnotationUsage<EverythingBagel> annotationUsage = classDetails.getAnnotationUsage( EverythingBagel.class );

		assertThat( annotationUsage.<String>getAttributeValue( "theString" ) ).isEqualTo( "hello" );
		assertThat( annotationUsage.<Status>getAttributeValue( "theEnum" ) ).isEqualTo( Status.ACTIVE );
		assertThat( annotationUsage.<Boolean>getAttributeValue( "theBoolean" ) ).isEqualTo( true );
		assertThat( annotationUsage.<Byte>getAttributeValue( "theByte" ) ).isEqualTo( (byte) 1 );
		assertThat( annotationUsage.<Short>getAttributeValue( "theShort" ) ).isEqualTo( (short) 2 );
		assertThat( annotationUsage.<Integer>getAttributeValue( "theInteger" ) ).isEqualTo( 3 );
		assertThat( annotationUsage.<Long>getAttributeValue( "theLong" ) ).isEqualTo( 4L );
		assertThat( annotationUsage.<Float>getAttributeValue( "theFloat" ) ).isEqualTo( 5.1F );
		assertThat( annotationUsage.<Double>getAttributeValue( "theDouble" ) ).isEqualTo( 6.2 );

		assertThat( annotationUsage.getString( "theString" ) ).isEqualTo( "hello" );
		assertThat( annotationUsage.getEnum( "theEnum" ).name() ).isEqualTo( Status.ACTIVE.name() );
		assertThat( annotationUsage.getEnum( "theEnum", Status.class ) ).isEqualTo( Status.ACTIVE );
		assertThat( annotationUsage.getBoolean( "theBoolean" ) ).isEqualTo( true );
		assertThat( annotationUsage.getByte( "theByte" ) ).isEqualTo( (byte) 1 );
		assertThat( annotationUsage.getShort( "theShort" ) ).isEqualTo( (short) 2 );
		assertThat( annotationUsage.getInteger( "theInteger" ) ).isEqualTo( 3 );
		assertThat( annotationUsage.getLong( "theLong" ) ).isEqualTo( 4L );
		assertThat( annotationUsage.getFloat( "theFloat" ) ).isEqualTo( 5.1F );
		assertThat( annotationUsage.getDouble( "theDouble" ) ).isEqualTo( 6.2 );

		assertThat( annotationUsage.getClassDetails( "theClass" ).toJavaClass() ).isEqualTo( TheClass.class );
		assertThat( annotationUsage.getNestedUsage( "theNested" ) ).isNotNull();
		assertThat( annotationUsage.getList( "theStrings" ) ).containsExactly( "a", "b", "c" );
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
