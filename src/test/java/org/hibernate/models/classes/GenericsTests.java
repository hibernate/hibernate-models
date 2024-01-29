/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.classes;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class GenericsTests {
	@Test
	void testWrappedIdWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Base.class,
				Thing1.class,
				Thing2.class
		);
		testWrappedId( index );
	}

	@Test
	void testWrappedIdWithoutJandex() {
		testWrappedId( null );
	}

	void testWrappedId(Index jandexIndex) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				jandexIndex,
				Base.class,
				Thing1.class,
				Thing2.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base.class.getName() );
		final TypeDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
		assertThat( idType.getName() ).isEqualTo( IdWrapper.class.getName() );
		final TypeDetails wrappedType = idType.asParameterizedType().getGenericClassDetails().findFieldByName( "value" ).getType();
		assertThat( wrappedType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( wrappedType.asTypeVariable().getIdentifier() ).isEqualTo( "T" );
		assertThat( wrappedType.asTypeVariable().getBounds() ).hasSize( 1 );
		assertThat( wrappedType.asTypeVariable().getBounds() ).contains( ClassBasedTypeDetails.OBJECT_TYPE_DETAILS );
	}

	@Test
	void testIdWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Base2.class,
				Thing3.class,
				Thing4.class
		);
		testId( index );
	}

	@Test
	void testIdWithoutJandex() {
		testId( null );
	}

	void testId(Index jandexIndex) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				jandexIndex,
				Base2.class,
				Thing3.class,
				Thing4.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base2.class.getName() );
		assertThat( baseClassDetails.getFields() ).hasSize( 2 );
//		final ClassDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
//		assertThat( idType ).isSameAs( ClassDetails.OBJECT_CLASS_DETAILS );
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
	public static class Thing4 extends Base<String> {
	}
}
