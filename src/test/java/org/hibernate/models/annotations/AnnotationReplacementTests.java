/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.spi.MutableClassDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationReplacementTests {
	@Test
	void testBasicReplacementWithJandex() {
		basicReplacementChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testBasicReplacementWithoutJandex() {
		basicReplacementChecks( null );
	}

	void basicReplacementChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );

		final MutableClassDetails classDetails = (MutableClassDetails) buildingContext.getClassDetailsRegistry().getClassDetails( SimpleEntity.class.getName() );
		assertThat( classDetails.hasAnnotationUsage( SecondaryTable.class ) ).isTrue();
		assertThat( classDetails.hasAnnotationUsage( SecondaryTables.class ) ).isFalse();

		final MutableAnnotationUsage<SecondaryTables> replacement = classDetails.replaceAnnotationUsage(
				JpaAnnotations.SECONDARY_TABLE,
				JpaAnnotations.SECONDARY_TABLES,
				buildingContext
		);

		assertThat( classDetails.hasAnnotationUsage( SecondaryTable.class ) ).isFalse();
		assertThat( classDetails.hasAnnotationUsage( SecondaryTables.class ) ).isTrue();

		List<MutableAnnotationUsage<SecondaryTable>> valueList = replacement.getList( "value" );
		// because it is required
		assertThat( valueList ).isNull();
		valueList = new ArrayList<>();
		replacement.setAttributeValue( "value", valueList );

		final MutableAnnotationUsage<SecondaryTable> fromXml = JpaAnnotations.SECONDARY_TABLE.createUsage( buildingContext );
		valueList.add( fromXml );
		fromXml.setAttributeValue( "name", "from_xml" );

		final AnnotationUsage<SecondaryTable> annotationUsage = classDetails.getAnnotationUsage( SecondaryTable.class );
		assertThat( annotationUsage.getString( "name" ) ).isEqualTo( "from_xml" );

		final AnnotationUsage<SecondaryTables> annotationUsage1 = classDetails.getAnnotationUsage( SecondaryTables.class );
		assertThat( annotationUsage1.getList( "value" ) ).isSameAs( valueList );

		// see #76
		classDetails.locateAnnotationUsage( SecondaryTable.class );
	}

}
