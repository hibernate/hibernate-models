/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.members;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class RecordTests {
	@Test
	void testWithJandex() {
		verify( SourceModelTestHelper.buildJandexIndex( Data.class ) );

	}

	@Test
	void testWithoutJandex() {
		verify( null );
	}

	void verify(Index index) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Data.class
		);

		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.findClassDetails( Data.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getFields() ).hasSize( 2 );
		assertThat( classDetails.getRecordComponents() ).hasSize( 2 );

		for ( RecordComponentDetails recordComponent : classDetails.getRecordComponents() ) {
			assertThat( recordComponent.getDeclaringType() ).isSameAs( classDetails );
			assertThat( recordComponent.isPersistable() ).isTrue();
			assertThat( recordComponent.getKind() ).isEqualTo( AnnotationTarget.Kind.RECORD_COMPONENT );
			assertThat( recordComponent.resolveAttributeName() ).isEqualTo( recordComponent.getName() );
		}
	}

	public record Data(Integer key, String name) {
	}
}
