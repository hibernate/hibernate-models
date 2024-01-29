/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.generics;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

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
	void testParameterizedClassWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Simple2.class );
		testParameterizedClass( index );
	}

	@Test
	void testParameterizedClassWithoutJandex() {
		testParameterizedClass( null );
	}

	void testParameterizedClass(Index index) {
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

		final ClassTypeDetailsImpl classTypeDetails = new ClassTypeDetailsImpl( classDetails, TypeDetails.Kind.CLASS );
		assertThat( classTypeDetails.getClassDetails() ).isSameAs( classDetails );
	}

	@Test
	void testTypeVariableReferenceJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Simple3.class );
		testTypeVariableReference( index );
	}

	@Test
	void testTypeVariableReferenceWithoutJandex() {
		testTypeVariableReference( null );
	}

	void testTypeVariableReference(Index index) {
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
	void testParameterizedHierarchyWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Root.class,
				Base1.class,
				Base2.class
		);
		testParameterizedHierarchy( index );
	}

	@Test
	void testParameterizedHierarchyWithoutJandex() {
		testParameterizedHierarchy( null );
	}

	void testParameterizedHierarchy(Index index) {
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

	@Test
	void testCollectionsWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( ClassOfCollections.class );
		testCollections( index );
	}

	@Test
	void testCollectionsWithoutJandex() {
		testCollections( null );
	}

	void testCollections(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				ClassOfCollections.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( ClassOfCollections.class.getName() );

		{
			final FieldDetails listOfString = classDetails.findFieldByName( "listOfString" );

			assertThat( listOfString.getType() ).isInstanceOf( ParameterizedTypeDetails.class );
			final ParameterizedTypeDetails type = (ParameterizedTypeDetails) listOfString.getType();
			assertThat( type.isImplementor( Collection.class ) ).isTrue();
			assertThat( type.isImplementor( List.class ) ).isTrue();
			assertThat( type.isImplementor( Set.class ) ).isFalse();
			assertThat( type.isImplementor( Map.class ) ).isFalse();

			assertThat( type.getArguments() ).hasSize( 1 );
			assertThat( type.getArguments().get(0) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails elementType = (ClassTypeDetails) type.getArguments().get( 0);
			assertThat( elementType.isImplementor( String.class ) ).isTrue();
		}

		{
			final FieldDetails listOfT = classDetails.findFieldByName( "listOfT" );

			assertThat( listOfT.getType() ).isInstanceOf( ParameterizedTypeDetails.class );
			final ParameterizedTypeDetails type = (ParameterizedTypeDetails) listOfT.getType();
			assertThat( type.isImplementor( Collection.class ) ).isTrue();
			assertThat( type.isImplementor( List.class ) ).isTrue();
			assertThat( type.isImplementor( Set.class ) ).isFalse();
			assertThat( type.isImplementor( Map.class ) ).isFalse();

			assertThat( type.getArguments() ).hasSize( 1 );
			assertThat( type.getArguments().get(0) ).isInstanceOf( TypeVariableDetails.class );
			final TypeVariableDetails elementType = (TypeVariableDetails) type.getArguments().get( 0);
			assertThat( elementType.isImplementor( String.class ) ).isFalse();
			assertThat( elementType.getIdentifier() ).isEqualTo( "T" );
		}

		{
			final FieldDetails mapOfString = classDetails.findFieldByName( "mapOfString" );

			assertThat( mapOfString.getType() ).isInstanceOf( ParameterizedTypeDetails.class );
			final ParameterizedTypeDetails type = (ParameterizedTypeDetails) mapOfString.getType();
			assertThat( type.isImplementor( Collection.class ) ).isFalse();
			assertThat( type.isImplementor( List.class ) ).isFalse();
			assertThat( type.isImplementor( Set.class ) ).isFalse();
			assertThat( type.isImplementor( Map.class ) ).isTrue();

			assertThat( type.getArguments() ).hasSize( 2 );

			// key
			assertThat( type.getArguments().get(0) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails keyType = (ClassTypeDetails) type.getArguments().get( 0);
			assertThat( keyType.isImplementor( String.class ) ).isTrue();

			// value
			assertThat( type.getArguments().get(1) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails valueType = (ClassTypeDetails) type.getArguments().get( 0);
			assertThat( valueType.isImplementor( String.class ) ).isTrue();
		}

		{
			final FieldDetails mapOfT = classDetails.findFieldByName( "mapOfT" );

			assertThat( mapOfT.getType() ).isInstanceOf( ParameterizedTypeDetails.class );
			final ParameterizedTypeDetails type = (ParameterizedTypeDetails) mapOfT.getType();
			assertThat( type.isImplementor( Collection.class ) ).isFalse();
			assertThat( type.isImplementor( List.class ) ).isFalse();
			assertThat( type.isImplementor( Set.class ) ).isFalse();
			assertThat( type.isImplementor( Map.class ) ).isTrue();

			assertThat( type.getArguments() ).hasSize( 2 );

			// key
			assertThat( type.getArguments().get(0) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails keyType = (ClassTypeDetails) type.getArguments().get(0);
			assertThat( keyType.isImplementor( String.class ) ).isTrue();

			// value
			assertThat( type.getArguments().get(1) ).isInstanceOf( TypeVariableDetails.class );
			final TypeVariableDetails valueType = (TypeVariableDetails) type.getArguments().get(1);
			assertThat( valueType.isImplementor( String.class ) ).isFalse();
			assertThat( valueType.getIdentifier() ).isEqualTo( "T" );
		}
	}

	@SuppressWarnings("unused")
	static class Simple {
		Integer id;
	}

	@SuppressWarnings("unused")
	static class Simple2<I extends Number> {
		I id;
	}

	@SuppressWarnings("unused")
	static class Simple3<I extends Comparable<I>> {
		I id;
	}

	@SuppressWarnings("unused")
	static class Root<I> {
		I id;
	}

	static class Base1 extends Root<Integer> {
	}

	static class Base2 extends Root<String> {
	}

	@SuppressWarnings("unused")
	static class ClassOfArrays<T> {
		int[] intArray;
		int[][] int2Array;

		T[] tArray;
	}

	@SuppressWarnings("unused")
	static class ClassOfCollections<T> {
		List<String> listOfString;
		List<T> listOfT;

		Set<String> setOfString;
		Set<T> setOfT;

		Map<String,String> mapOfString;
		Map<String,T> mapOfT;
	}
}
