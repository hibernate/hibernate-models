/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.testing.intg.ModelsContextFactory;
import org.hibernate.models.testing.orm.OrmAnnotationHelper;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class TestHelper {
	public static ModelsContext createModelContext(Class<?>... modelClasses) {
		return createModelContext( null, modelClasses );
	}

	public static ModelsContext buildModelContext(Class<?>... modelClasses) {
		return createModelContext( null, modelClasses );
	}

	public static ModelsContext createModelContext(
			RegistryPrimer additionalPrimer,
			Class<?>... modelClasses) {
		final ModelsContext builtContext = buildModelContext( additionalPrimer, modelClasses );

		if ( CollectionHelper.isNotEmpty( modelClasses ) ) {
			for ( Class<?> modelClass : modelClasses ) {
				builtContext.getClassDetailsRegistry().resolveClassDetails( modelClass.getName() );
			}
		}

		return builtContext;
	}

	private static ModelsContext buildModelContext(
			RegistryPrimer additionalPrimer,
			Class<?>... modelClasses) {
		final ServiceLoader<ModelsContextFactory> loader = ServiceLoader.load( ModelsContextFactory.class );
		final Iterator<ModelsContextFactory> serviceImpls = loader.iterator();
		if ( serviceImpls.hasNext() ) {
			final ModelsContextFactory contextFactory = serviceImpls.next();
			if ( serviceImpls.hasNext() ) {
				throw new IllegalStateException( "Found more than one TestingModelContextFactory" );
			}
			System.out.println( "Using TestingModelContextFactory: " + contextFactory );
			return contextFactory.createModelContext(
					(contributions, modelsContext) -> {
						OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation );
						if ( additionalPrimer != null ) {
							additionalPrimer.primeRegistries( contributions, modelsContext );
						}
					},
					modelClasses
			);
		}

		return new BasicModelsContextImpl(
				SIMPLE_CLASS_LOADING,
				true,
				(contributions, modelsContext) -> {
					OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation );
					if ( additionalPrimer != null ) {
						additionalPrimer.primeRegistries( contributions, modelsContext );
					}
				}
		);
	}
}
