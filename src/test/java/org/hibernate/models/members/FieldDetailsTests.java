/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.members;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MemberDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;

/**
 * Tests for modeling methods
 *
 * @author Steve Ebersole
 */
public class FieldDetailsTests {
	@Test
	void testWithJandex() {
		verify( buildJandexIndex( RandomClass.class ) );
	}

	@Test
	void testWithoutJandex() {
		verify( null );
	}

	void verify(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				RandomClass.class
		);

		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.findClassDetails( RandomClass.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getFields() ).hasSize( 10 );

		final FieldDetails propertyField = classDetails.findFieldByName( "property" );
		assertThat( propertyField.resolveAttributeName() ).isEqualTo( "property" );
		assertThat( propertyField.getType().toJavaClass() ).isEqualTo( Integer.class );
		assertThat( propertyField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
		assertThat( propertyField.isPersistable() ).isTrue();

		final FieldDetails somethingField = classDetails.findFieldByName( "something" );
		assertThat( somethingField.resolveAttributeName() ).isEqualTo( "something" );
		assertThat( somethingField.getType().toJavaClass() ).isEqualTo( Object.class );
		assertThat( somethingField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
		assertThat( somethingField.isPersistable() ).isTrue();

		final FieldDetails activeField = classDetails.findFieldByName( "active" );
		assertThat( activeField.resolveAttributeName() ).isEqualTo( "active" );
		assertThat( activeField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( activeField.getType().toJavaClass() ).isEqualTo( boolean.class );
		assertThat( activeField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( activeField.isPersistable() ).isTrue();

		final FieldDetails byteValueField = classDetails.findFieldByName( "byteValue" );
		assertThat( byteValueField.resolveAttributeName() ).isEqualTo( "byteValue" );
		assertThat( byteValueField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( byteValueField.getType().toJavaClass() ).isEqualTo( byte.class );
		assertThat( byteValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( byteValueField.isPersistable() ).isTrue();

		final FieldDetails shortValueField = classDetails.findFieldByName( "shortValue" );
		assertThat( shortValueField.resolveAttributeName() ).isEqualTo( "shortValue" );
		assertThat( shortValueField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( shortValueField.getType().toJavaClass() ).isEqualTo( short.class );
		assertThat( shortValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( shortValueField.isPersistable() ).isTrue();

		final FieldDetails intValueField = classDetails.findFieldByName( "intValue" );
		assertThat( intValueField.resolveAttributeName() ).isEqualTo( "intValue" );
		assertThat( intValueField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( intValueField.getType().toJavaClass() ).isEqualTo( int.class );
		assertThat( intValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( intValueField.isPersistable() ).isTrue();

		final FieldDetails longValueField = classDetails.findFieldByName( "longValue" );
		assertThat( longValueField.resolveAttributeName() ).isEqualTo( "longValue" );
		assertThat( longValueField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( longValueField.getType().toJavaClass() ).isEqualTo( long.class );
		assertThat( longValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( longValueField.isPersistable() ).isTrue();

		final FieldDetails doubleValueField = classDetails.findFieldByName( "doubleValue" );
		assertThat( doubleValueField.resolveAttributeName() ).isEqualTo( "doubleValue" );
		assertThat( doubleValueField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( doubleValueField.getType().toJavaClass() ).isEqualTo( double.class );
		assertThat( doubleValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( doubleValueField.isPersistable() ).isTrue();

		final FieldDetails floatValueField = classDetails.findFieldByName( "floatValue" );
		assertThat( floatValueField.resolveAttributeName() ).isEqualTo( "floatValue" );
		assertThat( floatValueField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( floatValueField.getType().toJavaClass() ).isEqualTo( float.class );
		assertThat( floatValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PACKAGE );
		assertThat( floatValueField.isPersistable() ).isTrue();

		final FieldDetails intValueArrayField = classDetails.findFieldByName( "intValueArray" );
		assertThat( intValueArrayField.resolveAttributeName() ).isEqualTo( "intValueArray" );
		assertThat( intValueArrayField.getType() ).isInstanceOf( JdkClassDetails.class );
		assertThat( intValueArrayField.getType().toJavaClass() ).isEqualTo( int[].class );
		assertThat( intValueArrayField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PROTECTED );
		assertThat( intValueArrayField.isPersistable() ).isTrue();

		classDetails.forEachField( (position, fieldDetails) -> {
			System.out.printf( "  > Field (%s) : %s\n", position, fieldDetails );
		} );
	}


	@SuppressWarnings("unused")
	public static class RandomClass {
		public Integer property;
		public Object something;
		private boolean active;
		private byte byteValue;
		private short shortValue;
		private int intValue;
		private long longValue;
		private double doubleValue;
		float floatValue;
		protected int[] intValueArray;
	}
}
