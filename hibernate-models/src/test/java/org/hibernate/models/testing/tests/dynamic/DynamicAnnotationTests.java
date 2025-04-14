/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.dynamic;

import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.orm.ForeignKeyAnnotation;
import org.hibernate.models.testing.orm.JpaAnnotations;
import org.hibernate.models.testing.orm.SecondaryTableAnnotation;
import org.hibernate.models.testing.orm.TableAnnotation;

import org.junit.jupiter.api.Test;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class DynamicAnnotationTests {
	@Test
	void testBasicUsage() {
		final ModelsContext modelsContext = createModelContext();
		final SequenceGenerator generatorAnn = JpaAnnotations.SEQUENCE_GENERATOR.createUsage( modelsContext );
		assertThat( generatorAnn.name() ).isEqualTo( "" );
		assertThat( generatorAnn.sequenceName() ).isEqualTo( "" );
		assertThat( generatorAnn.catalog() ).isEqualTo( "" );
		assertThat( generatorAnn.schema() ).isEqualTo( "" );
		assertThat( generatorAnn.initialValue() ).isEqualTo( 1 );
		assertThat( generatorAnn.allocationSize() ).isEqualTo( 50 );
		assertThat( generatorAnn.options() ).isEqualTo("" );

		final SequenceGenerators generatorsAnn = JpaAnnotations.SEQUENCE_GENERATORS.createUsage( modelsContext );
		assertThat( generatorsAnn.value() ).isNotNull();
		assertThat( generatorsAnn.value() ).isEmpty();
	}

	@Test
	void testAnnotationWrapping() {
		final ModelsContext modelsContext = createModelContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", modelsContext );
		final Table tableUsage = dynamicEntity.applyAnnotationUsage(
				JpaAnnotations.TABLE,
				modelsContext
		);
		assertThat( tableUsage ).isInstanceOf( TableAnnotation.class );

		final SecondaryTable secondaryTableUsage = dynamicEntity.applyAnnotationUsage(
				JpaAnnotations.SECONDARY_TABLE,
				modelsContext
		);
		assertThat( secondaryTableUsage ).isInstanceOf( SecondaryTableAnnotation.class );

		final ForeignKey foreignKeyUsage = secondaryTableUsage.foreignKey();
		assertThat( foreignKeyUsage ).isInstanceOf( ForeignKeyAnnotation.class );
		assertThat( foreignKeyUsage.name() ).isEqualTo( "" );
		assertThat( foreignKeyUsage.options() ).isEqualTo( "" );
		assertThat( foreignKeyUsage.foreignKeyDefinition() ).isEqualTo( "" );
	}

}
