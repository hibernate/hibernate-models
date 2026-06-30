/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.generics;

import java.io.Serializable;

import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class ArrayTypeVariableTests {

	@Test
	void testParameterizedHierarchy() {
		final var modelsContext = createModelContext(
				Root.class,
				Base1.class,
				Base2.class,
				Child2.class
		);

		final var rootClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Root.class.getName() );
		final var base1ClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Base1.class.getName() );
		final var base2ClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Base2.class.getName() );
		final var child2ClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Child2.class.getName() );

		final var rootArrayField = rootClassDetails.findFieldByName( "rootArray" );
		final var rootArrayFieldType = rootArrayField.getType();
		assertThat( rootArrayFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.ARRAY );
		assertThat( rootArrayFieldType.isResolved() ).isFalse();

		final var constituentType = rootArrayFieldType.asArrayType().getConstituentType();
		assertThat( constituentType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( constituentType.isResolved() ).isFalse();

		{
			// array type
			final var resolvedRelativeType = rootArrayField.resolveRelativeType( rootClassDetails, modelsContext );
			assertThat( resolvedRelativeType.isResolved() ).isFalse();
			assertThat( resolvedRelativeType ).isInstanceOf( ArrayTypeDetails.class );
			final var resolvedArrayType = resolvedRelativeType.asArrayType();
			assertThat( resolvedArrayType.getArrayClassDetails().toJavaClass() ).isEqualTo( Object[].class );

			// constituent type
			final var resolvedConstituentType = resolvedArrayType.getConstituentType();
			assertThat( resolvedConstituentType.isResolved() ).isFalse();
			assertThat( resolvedConstituentType ).isInstanceOf( TypeVariableDetails.class );
			final TypeDetails bound = resolvedConstituentType.asTypeVariable().getBounds().get( 0 );
			assertThat( bound.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
			assertThat( bound.asClassType().getClassDetails().toJavaClass() ).isEqualTo( Object.class );

			// array type as class type
			final ClassBasedTypeDetails resolvedClassType = rootArrayField.resolveRelativeClassType( rootClassDetails, modelsContext );
			assertThat( resolvedClassType.isResolved() ).isFalse();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Object[].class );
		}

		{
			// Base1 specializes Root<Integer>, so rootArray should resolve to Integer[]
			final var concreteType = rootArrayField.resolveRelativeType( base1ClassDetails, modelsContext );
			assertThat( concreteType.isResolved() ).isTrue();
			assertThat( concreteType ).isInstanceOf( ArrayTypeDetails.class );
			final var concreteArrayDetails = concreteType.asArrayType();
			assertThat( concreteArrayDetails.getArrayClassDetails().toJavaClass() ).isEqualTo( Integer[].class );

			// constituent type
			final var concreteConstituentType = concreteArrayDetails.getConstituentType();
			assertThat( concreteConstituentType.isResolved() ).isTrue();
			assertThat( concreteConstituentType ).isInstanceOf( ClassBasedTypeDetails.class );
			assertThat( concreteConstituentType.asClassType().getClassDetails().toJavaClass() ).isEqualTo( Integer.class );

			// array type as class type
			final var resolvedClassType = rootArrayField.resolveRelativeClassType( base1ClassDetails, modelsContext );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Integer[].class );
		}

		{
			// Base2 specializes Root<String>, so rootArray should resolve to String[]
			final var concreteType = rootArrayField.resolveRelativeType( base2ClassDetails, modelsContext );
			assertThat( concreteType.isResolved() ).isTrue();
			assertThat( concreteType ).isInstanceOf( ArrayTypeDetails.class );
			final var concreteArrayDetails = concreteType.asArrayType();
			assertThat( concreteArrayDetails.getArrayClassDetails().toJavaClass() ).isEqualTo( String[].class );

			// constituent type
			final var concreteConstituentType = concreteArrayDetails.getConstituentType();
			assertThat( concreteConstituentType.isResolved() ).isTrue();
			assertThat( concreteConstituentType ).isInstanceOf( ClassBasedTypeDetails.class );
			assertThat( concreteConstituentType.asClassType().getClassDetails().toJavaClass() ).isEqualTo( String.class );

			// array type as class type
			final var resolvedClassType = rootArrayField.resolveRelativeClassType( base2ClassDetails, modelsContext );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( String[].class );
		}

		// Test anotherArray field from Base2
		final var anotherArrayField = base2ClassDetails.findFieldByName( "anotherArray" );
		final var anotherArrayFieldType = anotherArrayField.getType();
		assertThat( anotherArrayFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.ARRAY );
		assertThat( anotherArrayFieldType.isResolved() ).isFalse();

		final var anotherConstituentType = anotherArrayFieldType.asArrayType().getConstituentType();
		assertThat( anotherConstituentType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( anotherConstituentType.isResolved() ).isFalse();

		{
			// In Base2, anotherArray is K[] where K extends Serializable, so unresolved with Serializable bound
			final var resolvedRelativeType = anotherArrayField.resolveRelativeType( base2ClassDetails, modelsContext );
			assertThat( resolvedRelativeType.isResolved() ).isFalse();
			assertThat( resolvedRelativeType ).isInstanceOf( ArrayTypeDetails.class );
			final var resolvedArrayType = resolvedRelativeType.asArrayType();
			assertThat( resolvedArrayType.getArrayClassDetails().toJavaClass() ).isEqualTo( Serializable[].class );

			// constituent type
			final var resolvedConstituentType = resolvedArrayType.getConstituentType();
			assertThat( resolvedConstituentType.isResolved() ).isFalse();
			assertThat( resolvedConstituentType ).isInstanceOf( TypeVariableDetails.class );
			final TypeDetails bound = resolvedConstituentType.asTypeVariable().getBounds().get( 0 );
			assertThat( bound.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
			assertThat( bound.asClassType().getClassDetails().toJavaClass() ).isEqualTo( Serializable.class );

			// array type as class type
			final ClassBasedTypeDetails resolvedClassType = anotherArrayField.resolveRelativeClassType( base2ClassDetails, modelsContext );
			assertThat( resolvedClassType.isResolved() ).isFalse();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Serializable[].class );
		}

		{
			// In Child2, anotherArray should resolve to Long[] since Child2 extends Base2<Long>
			final var concreteType = anotherArrayField.resolveRelativeType( child2ClassDetails, modelsContext );
			assertThat( concreteType.isResolved() ).isTrue();
			assertThat( concreteType ).isInstanceOf( ArrayTypeDetails.class );
			final var concreteArrayDetails = concreteType.asArrayType();
			assertThat( concreteArrayDetails.getArrayClassDetails().toJavaClass() ).isEqualTo( Long[].class );

			// constituent type
			final var concreteConstituentType = concreteArrayDetails.getConstituentType();
			assertThat( concreteConstituentType.isResolved() ).isTrue();
			assertThat( concreteConstituentType ).isInstanceOf( ClassBasedTypeDetails.class );
			assertThat( concreteConstituentType.asClassType().getClassDetails().toJavaClass() ).isEqualTo( Long.class );

			// array type as class type
			final var resolvedClassType = anotherArrayField.resolveRelativeClassType( child2ClassDetails, modelsContext );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Long[].class );
		}
	}

	static class Root<T> {
		T[] rootArray;
	}

	static class Base1 extends Root<Integer> {
	}

	static class Base2<K extends Serializable> extends Root<String> {
		K[] anotherArray;
	}

	static class Child2 extends Base2<Long> {
	}
}
