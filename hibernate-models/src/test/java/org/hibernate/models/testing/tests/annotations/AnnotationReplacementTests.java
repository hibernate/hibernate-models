/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.domain.SimpleEntity;
import org.hibernate.models.testing.orm.JpaAnnotations;
import org.hibernate.models.testing.orm.SecondaryTableAnnotation;
import org.hibernate.models.testing.orm.SecondaryTablesAnnotation;

import org.junit.jupiter.api.Test;

import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationReplacementTests {
	@Test
	void testBasicReplacement() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleEntity.class );

		final MutableClassDetails classDetails = (MutableClassDetails) buildingContext.getClassDetailsRegistry().getClassDetails( SimpleEntity.class.getName() );
		assertThat( classDetails.hasDirectAnnotationUsage( SecondaryTable.class ) ).isTrue();
		assertThat( classDetails.hasDirectAnnotationUsage( SecondaryTables.class ) ).isFalse();

		final SecondaryTablesAnnotation replacement = (SecondaryTablesAnnotation) classDetails.replaceAnnotationUsage(
				JpaAnnotations.SECONDARY_TABLE,
				JpaAnnotations.SECONDARY_TABLES,
				buildingContext
		);

		assertThat( classDetails.hasDirectAnnotationUsage( SecondaryTable.class ) ).isFalse();
		assertThat( classDetails.hasDirectAnnotationUsage( SecondaryTables.class ) ).isTrue();

		assertThat( replacement.value() ).hasSize( 0 );

		final SecondaryTable[] newValues = new SecondaryTable[1];
		replacement.value( newValues );
		assertThat( replacement.value() ).hasSize( 1 );

		final SecondaryTableAnnotation fromXml = (SecondaryTableAnnotation) JpaAnnotations.SECONDARY_TABLE.createUsage( buildingContext );
		newValues[0] = fromXml;
		fromXml.name( "from_xml" );

		final SecondaryTable annotationUsage = classDetails.getAnnotationUsage( SecondaryTable.class, buildingContext );
		assertThat( annotationUsage.name() ).isEqualTo( "from_xml" );

		final SecondaryTables annotationUsage1 = classDetails.getAnnotationUsage( SecondaryTables.class, buildingContext );
		assertThat( annotationUsage1.value() ).isSameAs( newValues );

		// see #76
		classDetails.locateAnnotationUsage( SecondaryTable.class, buildingContext );
	}

}
