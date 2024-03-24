package org.hibernate.models.dynamic;

import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.dynamic.DynamicAnnotationUsage;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.orm.JpaAnnotations;

import org.junit.jupiter.api.Test;

import jakarta.persistence.JoinTable;

import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

public class ToAnnotationTest {

	@Test
	void testAccessArrayOfAnnotations() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", buildingContext );
		final DynamicAnnotationUsage<JoinTable> generatorAnn = new DynamicAnnotationUsage<>(
				JpaAnnotations.JOIN_TABLE,
				dynamicEntity,
				buildingContext
		);

		JoinTable joinTableAnn = generatorAnn.toAnnotation();

		joinTableAnn.indexes();
	}
}
