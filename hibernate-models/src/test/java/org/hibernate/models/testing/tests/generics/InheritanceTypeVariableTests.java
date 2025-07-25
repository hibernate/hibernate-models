/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.generics;

import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class InheritanceTypeVariableTests {

	@Test
	void testParameterizedHierarchy() {
		final ModelsContext modelsContext = createModelContext(
				Root.class,
				Base1.class,
				Base2.class,
				Level1.class,
				Level2.class,
				Base3.class
		);

		final ClassDetails rootClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Root.class.getName() );
		final ClassDetails base1ClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Base1.class.getName() );
		final ClassDetails base2ClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Base2.class.getName() );
		final ClassDetails base3ClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Base3.class.getName() );

		final FieldDetails idField = rootClassDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
		assertThat( idFieldType.isResolved() ).isFalse();

		{
			final TypeDetails resolvedRelativeType = idField.resolveRelativeType( rootClassDetails );
			assertThat( resolvedRelativeType.isResolved() ).isFalse();
			assertThat( resolvedRelativeType ).isInstanceOf( TypeVariableDetails.class );
			final TypeDetails bound = ( (TypeVariableDetails) resolvedRelativeType ).getBounds().get( 0 );
			assertThat( bound.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
			assertThat( bound.asClassType().getClassDetails().toJavaClass() ).isEqualTo( Object.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( rootClassDetails );
			assertThat( idField.getType().isResolved() ).isFalse();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Object.class );
		}

		{
			final TypeDetails concreteType = idField.resolveRelativeType( base1ClassDetails );
			assertThat( concreteType ).isInstanceOf( ClassTypeDetails.class );
			final ClassDetails concreteClassDetails = ( (ClassTypeDetails) concreteType ).getClassDetails();
			assertThat( concreteClassDetails.toJavaClass() ).isEqualTo( Integer.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( base1ClassDetails );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Integer.class );
			assertThat( resolvedClassType.isResolved() ).isTrue();
		}

		{
			final TypeDetails concreteType = idField.resolveRelativeType( base2ClassDetails );
			assertThat( concreteType ).isInstanceOf( ClassTypeDetails.class );
			final ClassDetails concreteClassDetails = ( (ClassTypeDetails) concreteType ).getClassDetails();
			assertThat( concreteClassDetails.toJavaClass() ).isEqualTo( String.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( base2ClassDetails );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( String.class );
			assertThat( resolvedClassType.isResolved() ).isTrue();
		}

		{
			final TypeDetails concreteType = idField.resolveRelativeType( base3ClassDetails );
			assertThat( concreteType ).isInstanceOf( ClassTypeDetails.class );
			final ClassDetails concreteClassDetails = ( (ClassTypeDetails) concreteType ).getClassDetails();
			assertThat( concreteClassDetails.toJavaClass() ).isEqualTo( Long.class );

			final ClassBasedTypeDetails resolvedClassType = idField.resolveRelativeClassType( base3ClassDetails );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Long.class );
			assertThat( resolvedClassType.isResolved() ).isTrue();
		}

		{
			final ClassDetails level1Class = modelsContext.getClassDetailsRegistry().getClassDetails( Level1.class.getName() );
			final FieldDetails middleField = level1Class.findFieldByName( "middle" );
			final TypeDetails middleFieldType = middleField.getType();
			assertThat( middleFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( middleFieldType.isResolved() ).isFalse();

			final TypeDetails concreteType = middleField.resolveRelativeType( base3ClassDetails );
			assertThat( concreteType ).isInstanceOf( ClassTypeDetails.class );
			final ClassDetails concreteClassDetails = ( (ClassTypeDetails) concreteType ).getClassDetails();
			assertThat( concreteClassDetails.toJavaClass() ).isEqualTo( Short.class );

			final ClassBasedTypeDetails resolvedClassType = middleField.resolveRelativeClassType( base3ClassDetails );
			assertThat( resolvedClassType.isResolved() ).isTrue();
			assertThat( resolvedClassType.getClassDetails().toJavaClass() ).isEqualTo( Short.class );
			assertThat( resolvedClassType.isResolved() ).isTrue();
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

	// reuse same type parameter identifier as Root
	static class Level1<J, I> extends Root<J> {
		I middle;
	}

	static class Level2<K> extends Level1<K, Short> {
	}

	static class Base3<K> extends Level2<Long> {
	}
}
