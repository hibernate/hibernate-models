/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.shared.intg;

import java.io.IOException;

import org.hibernate.models.internal.BaseLineJavaTypes;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.jandex.internal.JandexIndexerHelper;
import org.hibernate.models.jandex.internal.JandexModelBuildingContextImpl;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.intg.TestingModelContextFactory;
import org.hibernate.models.testing.orm.JpaAnnotations;

import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class JandexTestingModelContextFactoryImpl implements TestingModelContextFactory {
	@Override
	public SourceModelBuildingContext createModelContext(
			RegistryPrimer registryPrimer,
			Class<?>... modelClasses) {
		final Index jandexIndex = buildJandexIndex( SIMPLE_CLASS_LOADING, modelClasses );
		return new JandexModelBuildingContextImpl( jandexIndex, SIMPLE_CLASS_LOADING, registryPrimer );
	}

	public static Index buildJandexIndex(ClassLoading classLoadingAccess, Class<?>... modelClasses) {
		final Indexer indexer = new Indexer();
		BaseLineJavaTypes.forEachJavaType( (javaType) -> JandexIndexerHelper.apply( javaType, indexer, classLoadingAccess ) );
		JpaAnnotations.forEachAnnotation( (descriptor) -> JandexIndexerHelper.apply( descriptor.getAnnotationType(), indexer, classLoadingAccess ) );

		if ( CollectionHelper.isNotEmpty( modelClasses ) ) {
			for ( Class<?> modelClass : modelClasses ) {
				try {
					indexer.indexClass( modelClass );
				}
				catch (IOException e) {
					throw new RuntimeException( e );
				}
			}
		}

		return indexer.complete();
	}
}
