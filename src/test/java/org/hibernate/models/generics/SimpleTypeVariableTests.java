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
public class SimpleTypeVariableTests {

	@Test
	void testParameterizedClassWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Simple.class );
		testParameterizedClass( index );
	}

	@Test
	void testParameterizedClassWithoutJandex() {
		testParameterizedClass( null );
	}

	void testParameterizedClass(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Simple.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Simple.class.getName() );
		assertThat( classDetails.getTypeParameters() ).hasSize( 1 );
		assertThat( classDetails.getTypeParameters().get( 0 ) ).isInstanceOf( TypeVariableDetails.class );
		final TypeVariableDetails typeParameter = classDetails.getTypeParameters().get( 0 );
		assertThat( typeParameter.getIdentifier() ).isEqualTo( "I" );
		assertThat( typeParameter.getBounds() ).hasSize( 1 );
		assertThat( typeParameter.getBounds().get( 0 ) ).isInstanceOf( ClassTypeDetails.class );
		assertThat( ( (ClassTypeDetails) typeParameter.getBounds().get( 0 ) ).getClassDetails().toJavaClass() ).isEqualTo( Number.class );

		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
		// resolved because we know it is a Number
		assertThat( idFieldType.isResolved() ).isTrue();
		assertThat( idFieldType.determineRelativeType( classDetails ).isResolved() ).isTrue();

		final TypeDetails idFieldConcreteType = idField.resolveRelativeType( classDetails );
		assertThat( idFieldConcreteType ).isInstanceOf( TypeVariableDetails.class );
		final TypeVariableDetails typeVariable = idFieldConcreteType.asTypeVariable();
		assertThat( typeVariable.getBounds() ).hasSize( 1 );
		assertThat( typeVariable.getBounds().get( 0 ).asClassType().getClassDetails().toJavaClass() ).isEqualTo( Number.class );
		assertThat( idFieldConcreteType.isResolved() ).isTrue();

		final ClassBasedTypeDetails classBasedTypeDetails = idField.resolveRelativeClassType( classDetails );
		assertThat( classBasedTypeDetails.getClassDetails().toJavaClass() ).isEqualTo( Number.class );
	}

	@SuppressWarnings("unused")
	static class Simple<I extends Number> {
		I id;
	}
}
