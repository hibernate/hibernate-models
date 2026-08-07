/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.members;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RecordComponentDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class RecordTests {
	@Test
	void testRecords() {
		final Map<String, FieldDetails> fieldDetailsByName = new HashMap<>(2);
		final Map<String, MethodDetails> accessorDetailsByName = new HashMap<>( 2);
		final ModelsContext modelsContext = createModelContext( Data.class );

		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.findClassDetails( Data.class.getName() );

		fieldDetailsByName.putAll(
				classDetails.getFields().stream().collect(
						Collectors.toMap(
								FieldDetails::getName,
								Function.identity()
						)
				)
		);

		accessorDetailsByName.putAll(
				classDetails.getMethods().stream().collect(
						Collectors.toMap(
								MethodDetails::getName,
								Function.identity()
						)
				)
		);

		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getFields() ).hasSize( 2 );
		assertThat( classDetails.getRecordComponents() ).hasSize( 2 );

		for ( RecordComponentDetails recordComponent : classDetails.getRecordComponents() ) {
			assertThat( recordComponent.getDeclaringType() ).isSameAs( classDetails );
			assertThat( recordComponent.isPersistable() ).isTrue();
			assertThat( recordComponent.getKind() ).isEqualTo( AnnotationTarget.Kind.RECORD_COMPONENT );
			assertThat( recordComponent.resolveAttributeName() ).isEqualTo( recordComponent.getName() );
			assertThat( recordComponent.getField() ).isSameAs( fieldDetailsByName.get(recordComponent.getName()) );
			assertThat( recordComponent.getAccessor() ).isSameAs( accessorDetailsByName.get(recordComponent.getName()) );
			assertThat( recordComponent.getField().getCorrespondingRecordComponent() ).isSameAs( recordComponent );
			assertThat( recordComponent.getAccessor().getCorrespondingRecordComponent() ).isSameAs( recordComponent );
		}
	}

	public record Data(Integer key, String name) {
	}
}
