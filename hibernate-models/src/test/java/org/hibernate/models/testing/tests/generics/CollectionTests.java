/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class CollectionTests {

	@Test
	void testCollections() {
		final SourceModelBuildingContext buildingContext = createModelContext( ClassOfCollections.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( ClassOfCollections.class.getName() );

		{
			final FieldDetails listOfString = classDetails.findFieldByName( "listOfString" );

			assertThat( listOfString.getType() ).isInstanceOf( ParameterizedTypeDetails.class );
			final ParameterizedTypeDetails type = listOfString.getType().asParameterizedType();
			assertThat( type.isImplementor( Collection.class ) ).isTrue();
			assertThat( type.isImplementor( List.class ) ).isTrue();
			assertThat( type.isImplementor( Set.class ) ).isFalse();
			assertThat( type.isImplementor( Map.class ) ).isFalse();

			assertThat( type.getArguments() ).hasSize( 1 );
			assertThat( type.getArguments().get(0) ).isInstanceOf( ClassTypeDetails.class );
			final ClassTypeDetails elementType = (ClassTypeDetails) type.getArguments().get( 0);
			assertThat( elementType.isImplementor( String.class ) ).isTrue();
			assertThat( type.isResolved() ).isTrue();
			assertThat( type.getRawClassDetails().toJavaClass() ).isEqualTo( List.class );
			assertThat( type.determineRawClass().toJavaClass() ).isEqualTo( List.class );
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
			assertThat( type.getRawClassDetails().toJavaClass() ).isEqualTo( List.class );
			assertThat( type.determineRawClass().toJavaClass() ).isEqualTo( List.class );
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
			assertThat( type.getRawClassDetails().toJavaClass() ).isEqualTo( Map.class );
			assertThat( type.determineRawClass().toJavaClass() ).isEqualTo( Map.class );

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
			assertThat( type.getRawClassDetails().toJavaClass() ).isEqualTo( Map.class );
			assertThat( type.determineRawClass().toJavaClass() ).isEqualTo( Map.class );

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
	void testArrays() {
		final SourceModelBuildingContext buildingContext = createModelContext( ClassOfArrays.class );

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
			assertThat( arrayType.isResolved() ).isFalse();
			assertThat( constituentType.isResolved() ).isFalse();
			assertThat( constituentType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( constituentType.asTypeVariable().getIdentifier() ).isEqualTo( "T" );
		}
	}

	@Test
	void testWildcard() {
		final SourceModelBuildingContext buildingContext = createModelContext( Things.class );

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
		assertThat( extendsStuffFieldWildcardType.determineRawClass() ).isNotNull();

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
		assertThat( superStuffFieldWildcardType.determineRawClass() ).isNotNull();

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
		assertThat( namedStuffFieldValueWildcardType.determineRawClass() ).isNotNull();
	}

	@Test
	void testListSubclasses() {
		final SourceModelBuildingContext buildingContext = createModelContext(
				SpecialList.class,
				SpecialArrayList.class,
				SuperSpecialArrayList.class,
				SpecialListContainer.class
		);

		final ClassDetails specialListDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SpecialList.class.getName() );
		assertThat( specialListDetails.isImplementor( List.class ) ).isTrue();

		final ClassDetails specialArrayListDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SpecialArrayList.class.getName() );
		assertThat( specialArrayListDetails.isImplementor( List.class ) ).isTrue();

		final ClassDetails superSpecialArrayListDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SuperSpecialArrayList.class.getName() );
		assertThat( superSpecialArrayListDetails.isImplementor( List.class ) ).isTrue();

		final ClassDetails containerDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SpecialListContainer.class.getName() );

		final FieldDetails specialListField = containerDetails.findFieldByName( "specialList" );
		assertThat( specialListField.getAssociatedType() ).isEqualTo( specialListField.getElementType() );
		assertThat( specialListField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );

		final FieldDetails specialArrayListField = containerDetails.findFieldByName( "specialArrayList" );
		assertThat( specialArrayListField.getAssociatedType() ).isEqualTo( specialListField.getElementType() );
		assertThat( specialArrayListField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );

		final FieldDetails superSpecialArrayListField = containerDetails.findFieldByName( "superSpecialArrayList" );
		assertThat( superSpecialArrayListField.getAssociatedType() ).isEqualTo( specialListField.getElementType() );
		assertThat( superSpecialArrayListField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
	}

	@Test
	void testMapSubclasses() {
		final SourceModelBuildingContext buildingContext = createModelContext(
				SpecialMap.class,
				SpecialHashMap.class,
				SuperSpecialHashMap.class,
				SpecialMapContainer.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SpecialMapContainer.class.getName() );

		final FieldDetails standardMapField = classDetails.findFieldByName( "standardMap" );
		assertThat( standardMapField.getType().isImplementor( Map.class ) ).isTrue();
		assertThat( standardMapField.getMapKeyType().isImplementor( String.class ) ).isTrue();
		assertThat( standardMapField.getMapKeyType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
		assertThat( standardMapField.getElementType().isImplementor( String.class ) ).isTrue();
		assertThat( standardMapField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );

		final FieldDetails parameterizedMapField = classDetails.findFieldByName( "parameterizedMap" );
		assertThat( parameterizedMapField.getType().isImplementor( Map.class ) ).isTrue();
		assertThat( parameterizedMapField.getMapKeyType().isImplementor( String.class ) ).isTrue();
		assertThat( parameterizedMapField.getMapKeyType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
		// unbound <T>
		assertThat( parameterizedMapField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( Object.class );

		final FieldDetails specialMapField = classDetails.findFieldByName( "specialMap" );
		assertThat( specialMapField.getType().isImplementor( Map.class ) ).isTrue();
		assertThat( specialMapField.getMapKeyType().isImplementor( String.class ) ).isTrue();
		assertThat( specialMapField.getMapKeyType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
		assertThat( specialMapField.getElementType().isImplementor( String.class ) ).isTrue();
		assertThat( specialMapField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );

		final FieldDetails specialHashMapField = classDetails.findFieldByName( "specialHashMap" );
		assertThat( specialHashMapField.getType().isImplementor( Map.class ) ).isTrue();
		assertThat( specialHashMapField.getMapKeyType().isImplementor( String.class ) ).isTrue();
		assertThat( specialHashMapField.getMapKeyType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
		assertThat( specialHashMapField.getElementType().isImplementor( String.class ) ).isTrue();
		assertThat( specialHashMapField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );

		final FieldDetails superSpecialHashMapField = classDetails.findFieldByName( "superSpecialHashMap" );
		assertThat( superSpecialHashMapField.getType().isImplementor( Map.class ) ).isTrue();
		assertThat( superSpecialHashMapField.getMapKeyType().isImplementor( String.class ) ).isTrue();
		assertThat( superSpecialHashMapField.getMapKeyType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
		assertThat( superSpecialHashMapField.getElementType().isImplementor( String.class ) ).isTrue();
		assertThat( superSpecialHashMapField.getElementType().determineRawClass().toJavaClass() ).isEqualTo( String.class );
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

	@SuppressWarnings("unused")
	static class Coat extends Stuff {
	}

	@SuppressWarnings("unused")
	static class Hat extends Stuff {
	}

	@SuppressWarnings("unused")
	static class Things {
		List<? extends Stuff> extendsStuff;
		List<? super Stuff> superStuff;

		Map<String, ? extends Stuff> namedStuff;
	}

	static class SpecialListContainer {
		private SpecialList specialList;
		private SpecialArrayList specialArrayList;
		private SuperSpecialArrayList superSpecialArrayList;
	}

	static class SpecialArrayList extends ArrayList<String> {
	}

	static class SuperSpecialArrayList extends SpecialArrayList {
	}

	@SuppressWarnings({ "NullableProblems", "DataFlowIssue", "Contract" })
	static class SpecialList implements List<String> {

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean contains(Object o) {
			return false;
		}

		@Override
		public Iterator<String> iterator() {
			return null;
		}

		@Override
		public Object[] toArray() {
			return new Object[0];
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return null;
		}

		@Override
		public boolean add(String s) {
			return false;
		}

		@Override
		public boolean remove(Object o) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			return false;
		}

		@Override
		public boolean addAll(int index, Collection<? extends String> c) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
		}

		@Override
		public void clear() {

		}

		@Override
		public String get(int index) {
			return null;
		}

		@Override
		public String set(int index, String element) {
			return null;
		}

		@Override
		public void add(int index, String element) {

		}

		@Override
		public String remove(int index) {
			return null;
		}

		@Override
		public int indexOf(Object o) {
			return 0;
		}

		@Override
		public int lastIndexOf(Object o) {
			return 0;
		}

		@Override
		public ListIterator<String> listIterator() {
			return null;
		}

		@Override
		public ListIterator<String> listIterator(int index) {
			return null;
		}

		@Override
		public List<String> subList(int fromIndex, int toIndex) {
			return null;
		}
	}



	static class SpecialMapContainer<T> {
		private Map<String,String> standardMap;
		private Map<String,T> parameterizedMap;
		private SpecialMap specialMap;
		private SpecialHashMap specialHashMap;
		private SuperSpecialHashMap superSpecialHashMap;
	}

	static class SpecialHashMap extends HashMap<String,String> {
	}

	static class SuperSpecialHashMap extends SpecialHashMap {
	}


	@SuppressWarnings({ "NullableProblems", "DataFlowIssue" })
	static class SpecialMap implements Map<String,String> {

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public String get(Object key) {
			return null;
		}

		@Override
		public String put(String key, String value) {
			return null;
		}

		@Override
		public String remove(Object key) {
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends String> m) {

		}

		@Override
		public void clear() {

		}

		@Override
		public Set<String> keySet() {
			return null;
		}

		@Override
		public Collection<String> values() {
			return null;
		}

		@Override
		public Set<Entry<String, String>> entrySet() {
			return null;
		}
	}
}
