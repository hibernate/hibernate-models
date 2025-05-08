/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.spi.StandardTypeDetails.OBJECT_TYPE_DETAILS;
import static org.hibernate.models.testing.TestHelper.buildModelContext;

/**
 * @author Steve Ebersole
 */
public class GenericsTests {
	@Test
	void testWrappedId() {
		final ModelsContext modelsContext = buildModelContext(
				Base.class,
				Thing1.class,
				Thing2.class
		);

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base.class.getName() );

		final TypeDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
		assertThat( idType.isResolved() ).isFalse();
		assertThat( idType.determineRawClass().isResolved() ).isTrue();
		assertThat( idType.getName() ).isEqualTo( IdWrapper.class.getName() );

		final TypeDetails wrappedType = idType.asParameterizedType().getRawClassDetails().findFieldByName( "value" ).getType();
		assertThat( wrappedType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( wrappedType.asTypeVariable().getIdentifier() ).isEqualTo( "T" );
		assertThat( wrappedType.asTypeVariable().getBounds() ).hasSize( 1 );
		assertThat( wrappedType.asTypeVariable().getBounds() ).contains( OBJECT_TYPE_DETAILS );
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
	void testId() {
		final ModelsContext modelsContext = buildModelContext(
				Base2.class,
				Thing3.class,
				Thing4.class
		);

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

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
	void testArrays() {
		final ModelsContext modelsContext = buildModelContext(
				Base3.class,
				Thing5.class
		);

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
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

	@SuppressWarnings("unused")
	public static class IdWrapper<T> {
		T value;
	}

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	public static class Base3<I> {
		private String[] strings;
		private I[] generics;
	}

	public static class Thing5 extends Base3<Integer> {
	}
}
