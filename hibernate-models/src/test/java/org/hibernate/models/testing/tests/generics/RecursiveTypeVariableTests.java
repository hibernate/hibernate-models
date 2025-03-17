/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.generics;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class RecursiveTypeVariableTests {

	@Test
	void testTypeVariableReference() {
		final SourceModelBuildingContext buildingContext = createModelContext( Simple.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Simple.class.getName() );
		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Number.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Integer.class ) ).isFalse();
		assertThat( idFieldType.isImplementor( Comparable.class ) ).isTrue();
	}

	@SuppressWarnings("unused")
	static class Simple<I extends Comparable<I>> {
		I id;
	}
}
