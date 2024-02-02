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
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class CollectionTests {

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
			assertThat( type.isResolved() ).isTrue();
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
			assertThat( type.isResolved() ).isFalse();
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
			assertThat( type.isResolved() ).isTrue();

			// key
			assertThat( type.getArguments().get(0) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails keyType = (ClassTypeDetails) type.getArguments().get( 0);
			assertThat( keyType.isImplementor( String.class ) ).isTrue();
			assertThat( type.isResolved() ).isTrue();

			// value
			assertThat( type.getArguments().get(1) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails valueType = (ClassTypeDetails) type.getArguments().get( 0);
			assertThat( valueType.isImplementor( String.class ) ).isTrue();
			assertThat( type.isResolved() ).isTrue();
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
			assertThat( type.isResolved() ).isFalse();

			// key
			assertThat( type.getArguments().get(0) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails keyType = (ClassTypeDetails) type.getArguments().get(0);
			assertThat( keyType.isImplementor( String.class ) ).isTrue();
			assertThat( keyType.isResolved() ).isTrue();

			// value
			assertThat( type.getArguments().get(1) ).isInstanceOf( TypeVariableDetails.class );
			final TypeVariableDetails valueType = (TypeVariableDetails) type.getArguments().get(1);
			assertThat( valueType.isImplementor( String.class ) ).isFalse();
			assertThat( valueType.getIdentifier() ).isEqualTo( "T" );
			assertThat( valueType.isResolved() ).isFalse();
		}
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

		{
			final FieldDetails intArrayField = classDetails.findFieldByName( "intArray" );
			final TypeDetails intArrayFieldType = intArrayField.getType();
			assertThat( intArrayField.isArray() ).isTrue();
			assertThat( intArrayFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.ARRAY );
			assertThat( intArrayFieldType.isImplementor( Integer.class ) ).isFalse();
			assertThat( intArrayFieldType.isImplementor( Number.class ) ).isFalse();
			assertThat( intArrayFieldType.isImplementor( Object.class ) ).isTrue();
			assertThat( intArrayFieldType.isImplementor( String.class ) ).isFalse();
			assertThat( intArrayFieldType.isResolved() ).isTrue();
		}

		{
			final FieldDetails field = classDetails.findFieldByName( "tArray" );
			final TypeDetails fieldType = field.getType();
			assertThat( field.isArray() ).isTrue();
			assertThat( fieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.ARRAY );
			final ArrayTypeDetails arrayType = fieldType.asArrayType();
			final TypeDetails constituentType = arrayType.getConstituentType();
			assertThat( constituentType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( constituentType.asTypeVariable().getIdentifier() ).isEqualTo( "T" );
			final TypeDetails fieldConcreteType = field.resolveRelativeType( classDetails );
			assertThat( fieldConcreteType ).isSameAs( arrayType );
			assertThat( constituentType.isResolved() ).isFalse();
		}
	}

	@Test
	void testWildcardWithJandex() {
		testWildcard( SourceModelTestHelper.buildJandexIndex( Stuff.class, Things.class ) );
	}

	@Test
	void testWildcardWithoutJandex() {
		testWildcard( null );
	}

	void testWildcard(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Things.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Things.class.getName() );

		final FieldDetails extendsStuffField = classDetails.findFieldByName( "extendsStuff" );
		final TypeDetails extendsStuffFieldType = extendsStuffField.getType();
		assertThat( extendsStuffFieldType.isImplementor( Collection.class ) ).isTrue();
		assertThat( extendsStuffFieldType.isImplementor( List.class ) ).isTrue();
		assertThat( extendsStuffFieldType.isImplementor( Set.class ) ).isFalse();
		assertThat( extendsStuffFieldType.isImplementor( Map.class ) ).isFalse();
		assertThat( extendsStuffFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.PARAMETERIZED_TYPE );
		assertThat( extendsStuffFieldType.asParameterizedType().getArguments() ).hasSize( 1 );
		assertThat( extendsStuffFieldType.asParameterizedType().getArguments().get( 0 ).getTypeKind() ).isEqualTo( TypeDetails.Kind.WILDCARD_TYPE );
		final WildcardTypeDetails extendsStuffFieldWildcardType = extendsStuffFieldType.asParameterizedType()
				.getArguments()
				.get( 0 )
				.asWildcardType();
		assertThat( extendsStuffFieldWildcardType.isExtends() ).isTrue();
		assertThat( extendsStuffFieldWildcardType.getBound().getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( extendsStuffFieldWildcardType.getBound().asClassType().getClassDetails().getName() ).endsWith( "Stuff" );
		assertThat( extendsStuffField.getElementType() ).isSameAs( extendsStuffFieldWildcardType );
		assertThat( extendsStuffFieldType.isResolved() ).isTrue();

		final FieldDetails superStuffField = classDetails.findFieldByName( "superStuff" );
		final TypeDetails superStuffFieldType = superStuffField.getType();
		assertThat( superStuffFieldType.isImplementor( Collection.class ) ).isTrue();
		assertThat( superStuffFieldType.isImplementor( List.class ) ).isTrue();
		assertThat( superStuffFieldType.isImplementor( Set.class ) ).isFalse();
		assertThat( superStuffFieldType.isImplementor( Map.class ) ).isFalse();
		assertThat( superStuffFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.PARAMETERIZED_TYPE );
		assertThat( superStuffFieldType.asParameterizedType().getArguments() ).hasSize( 1 );
		assertThat( superStuffFieldType.asParameterizedType().getArguments().get( 0 ).getTypeKind() ).isEqualTo( TypeDetails.Kind.WILDCARD_TYPE );
		final WildcardTypeDetails superStuffFieldWildcardType = superStuffFieldType.asParameterizedType()
				.getArguments()
				.get( 0 )
				.asWildcardType();
		assertThat( superStuffFieldWildcardType.isExtends() ).isFalse();
		assertThat( superStuffFieldWildcardType.getBound().getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( superStuffFieldWildcardType.getBound().asClassType().getClassDetails().getName() ).endsWith( "Stuff" );
		assertThat( superStuffField.getElementType() ).isSameAs( superStuffFieldWildcardType );
		assertThat( superStuffFieldWildcardType.isResolved() ).isTrue();

		final FieldDetails namedStuffField = classDetails.findFieldByName( "namedStuff" );
		final TypeDetails namedStuffFieldType = namedStuffField.getType();
		assertThat( namedStuffFieldType.isImplementor( Collection.class ) ).isFalse();
		assertThat( namedStuffFieldType.isImplementor( List.class ) ).isFalse();
		assertThat( namedStuffFieldType.isImplementor( Set.class ) ).isFalse();
		assertThat( namedStuffFieldType.isImplementor( Map.class ) ).isTrue();
		assertThat( namedStuffFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.PARAMETERIZED_TYPE );
		assertThat( namedStuffFieldType.asParameterizedType().getArguments() ).hasSize( 2 );
		assertThat( namedStuffFieldType.asParameterizedType().getArguments().get( 0 ).getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( namedStuffFieldType.asParameterizedType().getArguments().get( 0 ).asClassType().getClassDetails().toJavaClass() ).isEqualTo( String.class );
		assertThat( namedStuffFieldType.asParameterizedType().getArguments().get( 1 ).getTypeKind() ).isEqualTo( TypeDetails.Kind.WILDCARD_TYPE );
		final WildcardTypeDetails namedStuffFieldValueWildcardType = namedStuffFieldType.asParameterizedType()
				.getArguments()
				.get( 1 )
				.asWildcardType();
		assertThat( namedStuffFieldValueWildcardType.isExtends() ).isTrue();
		assertThat( namedStuffFieldValueWildcardType.getBound().getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( namedStuffFieldValueWildcardType.getBound().asClassType().getClassDetails().getName() ).endsWith( "Stuff" );
		assertThat( namedStuffField.getElementType() ).isSameAs( namedStuffFieldValueWildcardType );
		assertThat( namedStuffField.getMapKeyType().getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( namedStuffField.getMapKeyType().asClassType().getClassDetails().toJavaClass() ).isEqualTo( String.class );
		assertThat( namedStuffFieldType.isResolved() ).isTrue();
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

	@SuppressWarnings("unused")
	static class ClassOfArrays<T> {
		int[] intArray;
		int[][] int2Array;

		T[] tArray;
	}

	static class Item {
	}

	static class Stuff extends Item {
	}

	static class Coat extends Stuff {
	}

	static class Hat extends Stuff {
	}

	static class Things {
		List<? extends Stuff> extendsStuff;
		List<? super Stuff> superStuff;

		Map<String, ? extends Stuff> namedStuff;
	}
}
