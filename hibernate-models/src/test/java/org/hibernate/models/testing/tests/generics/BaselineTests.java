/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.generics;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class BaselineTests {
	@Test
	void testObjectUse() {
		assertThat( ClassTypeDetails.OBJECT_TYPE_DETAILS.asClassType().getClassDetails() ).isSameAs( ClassDetails.OBJECT_CLASS_DETAILS );
	}

	@Test
	void testSimpleClass() {
		final SourceModelBuildingContext buildingContext = createModelContext( Simple.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Simple.class.getName() );
		final FieldDetails idField = classDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.CLASS );
		assertThat( idFieldType.isImplementor( Integer.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( Number.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( Object.class ) ).isTrue();
		assertThat( idFieldType.isImplementor( String.class ) ).isFalse();

		assertThat( classDetails.getTypeParameters() ).isEmpty();

		final TypeDetails idFieldConcreteType = idField.resolveRelativeType( classDetails );
		assertThat( idFieldConcreteType ).isInstanceOf( ClassTypeDetails.class );
		assertThat( ( (ClassTypeDetails) idFieldConcreteType ).getClassDetails().toJavaClass() ).isEqualTo( Integer.class );
	}

	@SuppressWarnings("unused")
	static class Simple {
		Integer id;
	}
}
