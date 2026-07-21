/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.util.List;

import org.hibernate.models.jdk.JdkTrackingTypeSwitcher;
import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

class TypeDetailsFactoryTests {
	@Test
	void classType() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().resolveClassDetails( String.class.getName() );

		final TypeDetails typeDetails = TypeDetails.classType( classDetails );

		assertThat( typeDetails.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( typeDetails.asClassType().getClassDetails() ).isSameAs( classDetails );
	}

	@Test
	void parameterizedType() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetails listClass = modelsContext.getClassDetailsRegistry().resolveClassDetails( List.class.getName() );
		final TypeDetails elementType = TypeDetails.classType(
				modelsContext.getClassDetailsRegistry().resolveClassDetails( String.class.getName() )
		);

		final var parameterizedType = TypeDetails.parameterizedType(
				listClass,
				List.of( elementType ),
				listClass
		);

		assertThat( parameterizedType.getTypeKind() ).isEqualTo( TypeDetails.Kind.PARAMETERIZED_TYPE );
		assertThat( parameterizedType.getRawClassDetails() ).isSameAs( listClass );
		assertThat( parameterizedType.getArguments() ).containsExactly( elementType );
		assertThat( parameterizedType.getOwner() ).isSameAs( listClass );
	}

	@Test
	void wildcardTypes() {
		final ModelsContext modelsContext = createModelContext();
		final TypeDetails bound = TypeDetails.classType(
				modelsContext.getClassDetailsRegistry().resolveClassDetails( Number.class.getName() )
		);

		final var unbounded = TypeDetails.unboundedWildcard();
		assertThat( unbounded.getBound() ).isNull();
		assertThat( unbounded.isExtends() ).isTrue();

		final var upperBounded = TypeDetails.extendsWildcard( bound );
		assertThat( upperBounded.getExtendsBound() ).isSameAs( bound );
		assertThat( upperBounded.getSuperBound() ).isNull();

		final var lowerBounded = TypeDetails.superWildcard( bound );
		assertThat( lowerBounded.getBound() ).isSameAs( bound );
		assertThat( lowerBounded.isExtends() ).isFalse();
		assertThat( lowerBounded.getSuperBound() ).isSameAs( bound );
	}

	@Test
	@SuppressWarnings("removal")
	void arrayTypeAndDeprecatedHelperDelegation() {
		final ModelsContext modelsContext = createModelContext();
		final TypeDetails stringType = TypeDetails.classType(
				modelsContext.getClassDetailsRegistry().resolveClassDetails( String.class.getName() )
		);
		final TypeDetails intType = new JdkTrackingTypeSwitcher( modelsContext ).switchType( int.class );

		final ArrayTypeDetails stringArray = TypeDetails.arrayType( stringType, modelsContext );
		assertThat( stringArray.getConstituentType() ).isSameAs( stringType );
		assertThat( stringArray.getArrayClassDetails().toJavaClass() ).isEqualTo( String[].class );

		final ArrayTypeDetails intArray = TypeDetails.arrayType( intType, modelsContext );
		assertThat( intArray.getConstituentType() ).isSameAs( intType );
		assertThat( intArray.getArrayClassDetails().toJavaClass() ).isEqualTo( int[].class );

		final ArrayTypeDetails nestedArray = TypeDetails.arrayType( stringArray, modelsContext );
		assertThat( nestedArray.getConstituentType() ).isSameAs( stringArray );
		assertThat( nestedArray.getArrayClassDetails().toJavaClass() ).isEqualTo( String[][].class );

		final ArrayTypeDetails delegated = TypeDetailsHelper.arrayOf( stringType, modelsContext );
		assertThat( delegated ).isEqualTo( stringArray );
	}
}
