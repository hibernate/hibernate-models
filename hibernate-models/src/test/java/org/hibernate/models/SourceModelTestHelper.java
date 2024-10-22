/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

public class SourceModelTestHelper {

	public static SourceModelBuildingContext createBuildingContext(Class<?> modelClass) {
		return createBuildingContext( SIMPLE_CLASS_LOADING, modelClass );
	}

	public static SourceModelBuildingContext createBuildingContext(Class<?>... modelClasses) {
		return createBuildingContext( SIMPLE_CLASS_LOADING, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(
			ClassLoading classLoadingAccess,
			Class<?>... modelClasses) {
		return createBuildingContext(
				classLoadingAccess,
				(contributions, buildingContext) -> JpaAnnotations.forEachAnnotation( contributions::registerAnnotation ),
				modelClasses
		);
	}

	public static SourceModelBuildingContext createBuildingContext(
			RegistryPrimer registryPrimer,
			Class<?>... modelClasses) {
		return createBuildingContext( SIMPLE_CLASS_LOADING, registryPrimer, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(
			ClassLoading classLoadingAccess,
			RegistryPrimer registryPrimer,
			Class<?>... modelClasses) {
		final SourceModelBuildingContext ctx = new BasicModelBuildingContextImpl( classLoadingAccess, registryPrimer );

		if ( CollectionHelper.isNotEmpty( modelClasses ) ) {
			for ( Class<?> modelClass : modelClasses ) {
				ctx.getClassDetailsRegistry().resolveClassDetails( modelClass.getName() );
			}
		}

		return ctx;
	}

}
