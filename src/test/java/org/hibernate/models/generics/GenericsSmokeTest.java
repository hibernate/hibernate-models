/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.generics;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class GenericsSmokeTest {
	@Test
	void testObjectUse() {
		assertThat( ClassTypeDetails.OBJECT_TYPE_DETAILS.asClassType().getClassDetails() ).isSameAs( ClassDetails.OBJECT_CLASS_DETAILS );
	}

	@Test
	void testSimpleClassWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Simple.class );
		testSimpleClass( index );
	}

	@Test
	void testSimpleClassWithoutJandex() {
		testSimpleClass( null );
	}

	void testSimpleClass(Index jandexIndex) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				jandexIndex,
				Simple.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Simple.class.getName() );
		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( Number.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
	}

	@Test
	void testSimple2ClassWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Simple2.class );
		testSimple2Class( index );
	}

	@Test
	void testSimple2ClassWithoutJandex() {
		testSimple2Class( null );
	}

	void testSimple2Class(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Simple2.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Simple2.class.getName() );
		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
	}

	@Test
	void testSimple3ClassWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Simple3.class );
		testSimple3Class( index );
	}

	@Test
	void testSimple3ClassWithoutJandex() {
		testSimple3Class( null );
	}

	void testSimple3Class(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Simple3.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Simple3.class.getName() );
		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Comparable.class ) ).isTrue();
	}

	@Test
	void testParameterizedClassWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Root.class,
				Base1.class,
				Base2.class
		);
		testParameterizedClass( index );
	}

	@Test
	void testParameterizedClass() {
		testParameterizedClass( null );
	}

	void testParameterizedClass(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Root.class,
				Base1.class,
				Base2.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Root.class.getName() );
		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
	}

	@Test
	void testArraysWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( ClassOfArrays.class );
		testArrays( index );
	}

	@Test
	void testArraysWithoutJandex() {
		testArrays( null );
	}

	void testArrays(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				ClassOfArrays.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( ClassOfArrays.class.getName() );
		final FieldDetails intArrayField = classDetails.findFieldByName( "intArray" );
		final TypeDetails intArrayFieldType = intArrayField.getType();

		assertThat( intArrayField.isArray() ).isTrue();
		assertThat( intArrayFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.ARRAY );
		assertThat( intArrayFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( intArrayFieldType.isImplementor( Number.class ) ).isFalse();
		assertThat( intArrayFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( intArrayFieldType.isImplementor( String.class ) ).isFalse();
	}

	static class Simple {
		Integer id;
	}

	static class Simple2<I extends Number> {
		I id;
	}

	static class Simple3<I extends Comparable<I>> {
		I id;
	}

	static class Root<I> {
		I id;
	}

	static class Base1 extends Root<Integer> {
	}

	static class Base2 extends Root<String> {
	}

	static class ClassOfArrays<T> {
		int[] intArray;
		int[][] int2Array;

		T[] tArray;
	}
}
