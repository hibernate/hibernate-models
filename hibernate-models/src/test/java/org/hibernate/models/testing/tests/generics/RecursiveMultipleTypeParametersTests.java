/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.generics;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class RecursiveMultipleTypeParametersTests {
	@Test
	void testResolveRelativeTypeWithSelfReferenceFirst() {
		final ModelsContext modelsContext = createModelContext( BaseEntityWithSelfReferenceFirst.class );
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().getClassDetails(
				BaseEntityWithSelfReferenceFirst.class.getName()
		);

		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idType = idField.resolveRelativeType( classDetails );
		assertThat( idType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idType.isImplementor( Transitionable.class ) ).isTrue();
	}

	@Test
	void testResolveRelativeTypeWithSelfReferenceAfterType() {
		final ModelsContext modelsContext = createModelContext( BaseEntityWithSelfReferenceAfterType.class );
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().getClassDetails(
				BaseEntityWithSelfReferenceAfterType.class.getName()
		);

		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idType = idField.resolveRelativeType( classDetails );
		assertThat( idType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idType.isImplementor( Transitionable.class ) ).isTrue();
	}

	static class BaseEntityWithSelfReferenceFirst<S extends BaseEntityWithSelfReferenceFirst<S, I>, I extends Transitionable> {
		private I id;

		private String name;

		protected S self() {
			return (S) this;
		}
	}

	static class BaseEntityWithSelfReferenceAfterType<I extends Transitionable, S extends BaseEntityWithSelfReferenceAfterType<I, S>> {
		private I id;

		private String name;

		protected S self() {
			return (S) this;
		}
	}

	interface Transitionable {
		default boolean canTransitionTo(Transitionable other) {
			return other != null && !this.equals( other );
		}
	}
}
