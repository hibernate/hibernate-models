/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.classes;

import org.hibernate.models.bytebuddy.SourceModelTestHelper;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import net.bytebuddy.pool.TypePool;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class GenericsTests {
	@Test
	void testWrappedIdWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool(
				Base.class,
				Thing1.class,
				Thing2.class
		);
		testWrappedId( typePool );
	}

	@Test
	void testWrappedIdWithoutJandex() {
		testWrappedId( null );
	}

	void testWrappedId(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				Base.class,
				Thing1.class,
				Thing2.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base.class.getName() );

		final TypeDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
		assertThat( idType.isResolved() ).isFalse();
		assertThat( idType.determineRawClass().isResolved() ).isTrue();
		assertThat( idType.getName() ).isEqualTo( IdWrapper.class.getName() );

		final TypeDetails wrappedType = idType.asParameterizedType().getRawClassDetails().findFieldByName( "value" ).getType();
		assertThat( wrappedType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( wrappedType.asTypeVariable().getIdentifier() ).isEqualTo( "T" );
		assertThat( wrappedType.asTypeVariable().getBounds() ).hasSize( 1 );
		assertThat( wrappedType.asTypeVariable().getBounds() ).contains( ClassBasedTypeDetails.OBJECT_TYPE_DETAILS );
		assertThat( wrappedType.isResolved() ).isFalse();
		assertThat( wrappedType.determineRawClass().isResolved() ).isTrue();

		final ClassDetails thing1ClassDetails = classDetailsRegistry.resolveClassDetails( Thing1.class.getName() );
		final TypeDetails thing1IdType = idType.determineRelativeType( thing1ClassDetails );
		assertThat( thing1IdType.isResolved() ).isTrue();

		final ClassDetails thing2ClassDetails = classDetailsRegistry.resolveClassDetails( Thing2.class.getName() );
		final TypeDetails thing2IdType = idType.determineRelativeType( thing2ClassDetails );
		assertThat( thing2IdType.isResolved() ).isTrue();
	}

	@Test
	void testIdWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool( Base2.class, Thing3.class, Thing4.class );
		testId( typePool );
	}

	@Test
	void testIdWithoutJandex() {
		testId( null );
	}

	void testId(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				Base2.class,
				Thing3.class,
				Thing4.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base2.class.getName() );
		assertThat( baseClassDetails.getFields() ).hasSize( 2 );
		final TypeDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
		assertThat( idType.isResolved() ).isFalse();
		assertThat( idType.determineRawClass().isResolved() ).isTrue();

		final ClassDetails thing3ClassDetails = classDetailsRegistry.resolveClassDetails( Thing3.class.getName() );
		final TypeDetails thing3IdType = idType.determineRelativeType( thing3ClassDetails );
		assertThat( thing3IdType.isResolved() ).isTrue();

		final ClassDetails thing4ClassDetails = classDetailsRegistry.resolveClassDetails( Thing4.class.getName() );
		final TypeDetails thing4IdType = idType.determineRelativeType( thing4ClassDetails );
		assertThat( thing4IdType.isResolved() ).isTrue();
	}

	@Test
	void testArraysWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool( Base3.class, Thing5.class );
		testArrays( typePool );
	}

	@Test
	void testArraysWithoutJandex() {
		testArrays( null );
	}

	void testArrays(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				Base3.class,
				Thing5.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base3.class.getName() );
		assertThat( baseClassDetails.getFields() ).hasSize( 2 );

		final TypeDetails stringArrayType = baseClassDetails.findFieldByName( "strings" ).getType();
		assertThat( stringArrayType.isResolved() ).isTrue();
		assertThat( stringArrayType.determineRawClass().isResolved() ).isTrue();

		final TypeDetails genericArrayType = baseClassDetails.findFieldByName( "generics" ).getType();
		assertThat( genericArrayType.isResolved() ).isFalse();
		assertThat( genericArrayType.determineRawClass().isResolved() ).isTrue();

		final ClassDetails thing5ClassDetails = classDetailsRegistry.getClassDetails( Thing5.class.getName() );
		final TypeDetails thing5GenericArrayType = genericArrayType.determineRelativeType( thing5ClassDetails );
		assertThat( thing5GenericArrayType.isResolved() ).isTrue();
		assertThat( thing5GenericArrayType.determineRawClass().isResolved() ).isTrue();

		assertThat( stringArrayType.determineRawClass().isResolved() ).isTrue();
		assertThat( genericArrayType.determineRawClass().isResolved() ).isTrue();
	}

		public static class IdWrapper<T> {
		T value;
	}

	@MappedSuperclass
	public static class Base<I> {
		@Id
		private IdWrapper<I> id;
		private String name;
	}

	@Entity
	public static class Thing1 extends Base<Integer> {
	}

	@Entity
	public static class Thing2 extends Base<String> {
	}

	@MappedSuperclass
	public static class Base2<I> {
		@Id
		private I id;
		private String name;
	}

	@Entity
	public static class Thing3 extends Base2<Integer> {
	}

	@Entity
	public static class Thing4 extends Base2<String> {
	}

	public static class Base3<I> {
		private String[] strings;
		private I[] generics;
	}

	public static class Thing5 extends Base3<Integer> {
	}
}
