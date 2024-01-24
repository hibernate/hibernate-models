/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.classes;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ObjectClassDetails;

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
	void testWrappedId() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				Base.class,
				Thing1.class,
				Thing2.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base.class.getName() );
		final ClassDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
		assertThat( idType.getName() ).isEqualTo( IdWrapper.class.getName() );
		final ClassDetails wrappedType = idType.findFieldByName( "value" ).getType();
		assertThat( wrappedType ).isSameAs( ObjectClassDetails.OBJECT_CLASS_DETAILS );
	}

	@Test
	void testId() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				Base2.class,
				Thing3.class,
				Thing4.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails baseClassDetails = classDetailsRegistry.getClassDetails( Base2.class.getName() );
		assertThat( baseClassDetails.getFields() ).hasSize( 2 );
		final ClassDetails idType = baseClassDetails.findFieldByName( "id" ).getType();
		assertThat( idType ).isSameAs( ObjectClassDetails.OBJECT_CLASS_DETAILS );
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
