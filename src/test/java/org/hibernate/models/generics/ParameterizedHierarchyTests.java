/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.generics;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class ParameterizedHierarchyTests {

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

		final ClassDetails rootClassDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Root.class.getName() );
		final FieldDetails idField = rootClassDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();

		{
			final TypeDetails resolvedRelativeType = idField.resolveRelativeType( rootClassDetails );
			assertThat( resolvedRelativeType ).isInstanceOf( TypeVariableDetails.class );
			final TypeDetails bound = ( (TypeVariableDetails) resolvedRelativeType ).getBounds().get( 0 );
			assertThat( bound.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
			assertThat( bound.asClassType().getClassDetails().toJavaClass() ).isEqualTo( Object.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( rootClassDetails );
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Object.class );
		}

		{
			final ClassDetails base1ClassDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Base1.class.getName() );
			final TypeDetails concreteType = idField.resolveRelativeType( base1ClassDetails );
			assertThat( concreteType ).isInstanceOf( ClassTypeDetails.class );
			final ClassDetails concreteClassDetails = ( (ClassTypeDetails) concreteType ).getClassDetails();
			assertThat( concreteClassDetails.toJavaClass() ).isEqualTo( Integer.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( base1ClassDetails );
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Integer.class );
		}

		{
			final ClassDetails base2ClassDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Base2.class.getName() );
			final TypeDetails concreteType = idField.resolveRelativeType( base2ClassDetails );
			assertThat( concreteType ).isInstanceOf( ClassTypeDetails.class );
			final ClassDetails concreteClassDetails = ( (ClassTypeDetails) concreteType ).getClassDetails();
			assertThat( concreteClassDetails.toJavaClass() ).isEqualTo( String.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( base2ClassDetails );
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( String.class );
		}

	}

	@SuppressWarnings("unused")
	static class Root<I> {
		I id;
	}

	static class Base1 extends Root<Integer> {
	}

	static class Base2 extends Root<String> {
	}
}
