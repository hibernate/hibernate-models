/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.intg.TestingModelContextFactory;
import org.hibernate.models.testing.orm.OrmAnnotationHelper;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class TestHelper {
	public static SourceModelBuildingContext createModelContext(Class<?>... modelClasses) {
		return createModelContext( null, modelClasses );
	}

	public static SourceModelBuildingContext buildModelContext(Class<?>... modelClasses) {
		return createModelContext( null, modelClasses );
	}

	public static SourceModelBuildingContext createModelContext(
			RegistryPrimer additionalPrimer,
			Class<?>... modelClasses) {
		final SourceModelBuildingContext builtContext = buildModelContext( additionalPrimer, modelClasses );

		if ( CollectionHelper.isNotEmpty( modelClasses ) ) {
			for ( Class<?> modelClass : modelClasses ) {
				builtContext.getClassDetailsRegistry().resolveClassDetails( modelClass.getName() );
			}
		}

		return builtContext;
	}

	private static SourceModelBuildingContext buildModelContext(
			RegistryPrimer additionalPrimer,
			Class<?>... modelClasses) {
		final ServiceLoader<TestingModelContextFactory> loader = ServiceLoader.load( TestingModelContextFactory.class );
		final Iterator<TestingModelContextFactory> serviceImpls = loader.iterator();
		if ( serviceImpls.hasNext() ) {
			final TestingModelContextFactory contextFactory = serviceImpls.next();
			if ( serviceImpls.hasNext() ) {
				throw new IllegalStateException( "Found more than one TestingModelContextFactory" );
			}
			System.out.println( "Using TestingModelContextFactory: " + contextFactory );
			return contextFactory.createModelContext(
					(contributions, buildingContext) -> {
						OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation );
						if ( additionalPrimer != null ) {
							additionalPrimer.primeRegistries( contributions, buildingContext );
						}
					},
					modelClasses
			);
		}

		return new BasicModelBuildingContextImpl(
				SIMPLE_CLASS_LOADING,
				(contributions, buildingContext) -> {
					OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation );
					if ( additionalPrimer != null ) {
						additionalPrimer.primeRegistries( contributions, buildingContext );
					}
				}
		);
	}
}
