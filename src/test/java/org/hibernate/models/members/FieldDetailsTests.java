/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.members;

import java.lang.reflect.Field;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
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
		assertThat( propertyField.toJavaMember() ).isInstanceOf( Field.class );
		assertThat( propertyField.toJavaMember().getName() ).isEqualTo( "property" );
		assertThat( propertyField.resolveAttributeName() ).isEqualTo( "property" );
		assertThat( propertyField.getType().asClassType().getClassDetails().toJavaClass() ).isEqualTo( Integer.class );
		assertThat( propertyField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
		assertThat( propertyField.isPersistable() ).isTrue();
		assertThat( propertyField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails somethingField = classDetails.findFieldByName( "something" );
		assertThat( somethingField.resolveAttributeName() ).isEqualTo( "something" );
		assertThat( somethingField.getType().asClassType().getClassDetails().toJavaClass() ).isEqualTo( Object.class );
		assertThat( somethingField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
		assertThat( somethingField.isPersistable() ).isTrue();
		assertThat( somethingField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails activeField = classDetails.findFieldByName( "active" );
		assertThat( activeField.resolveAttributeName() ).isEqualTo( "active" );
		assertThat( activeField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( boolean.class );
		assertThat( activeField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( activeField.isPersistable() ).isTrue();
		assertThat( activeField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails byteValueField = classDetails.findFieldByName( "byteValue" );
		assertThat( byteValueField.resolveAttributeName() ).isEqualTo( "byteValue" );
		assertThat( byteValueField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( byte.class );
		assertThat( byteValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( byteValueField.isPersistable() ).isTrue();
		assertThat( byteValueField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails shortValueField = classDetails.findFieldByName( "shortValue" );
		assertThat( shortValueField.resolveAttributeName() ).isEqualTo( "shortValue" );
		assertThat( shortValueField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( short.class );
		assertThat( shortValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( shortValueField.isPersistable() ).isTrue();
		assertThat( shortValueField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails intValueField = classDetails.findFieldByName( "intValue" );
		assertThat( intValueField.resolveAttributeName() ).isEqualTo( "intValue" );
		assertThat( intValueField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( int.class );
		assertThat( intValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( intValueField.isPersistable() ).isTrue();
		assertThat( intValueField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails longValueField = classDetails.findFieldByName( "longValue" );
		assertThat( longValueField.resolveAttributeName() ).isEqualTo( "longValue" );
		assertThat( longValueField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( long.class );
		assertThat( longValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( longValueField.isPersistable() ).isTrue();
		assertThat( longValueField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails doubleValueField = classDetails.findFieldByName( "doubleValue" );
		assertThat( doubleValueField.resolveAttributeName() ).isEqualTo( "doubleValue" );
		assertThat( doubleValueField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( double.class );
		assertThat( doubleValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
		assertThat( doubleValueField.isPersistable() ).isTrue();
		assertThat( doubleValueField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails floatValueField = classDetails.findFieldByName( "floatValue" );
		assertThat( floatValueField.resolveAttributeName() ).isEqualTo( "floatValue" );
		assertThat( floatValueField.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( float.class );
		assertThat( floatValueField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PACKAGE );
		assertThat( floatValueField.isPersistable() ).isTrue();
		assertThat( floatValueField.getDeclaringType() ).isSameAs( classDetails );

		final FieldDetails intValueArrayField = classDetails.findFieldByName( "intValueArray" );
		assertThat( intValueArrayField.resolveAttributeName() ).isEqualTo( "intValueArray" );
		assertThat( intValueArrayField.getType().asArrayType().getArrayClassDetails().toJavaClass() ).isEqualTo( int[].class );
		assertThat( intValueArrayField.getVisibility() ).isEqualTo( MemberDetails.Visibility.PROTECTED );
		assertThat( intValueArrayField.isPersistable() ).isTrue();
		assertThat( intValueArrayField.getDeclaringType() ).isSameAs( classDetails );

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
