/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import java.io.IOException;
import java.lang.annotation.Annotation;

import org.hibernate.models.internal.BaseLineJavaTypes;
import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.jandex.internal.JandexIndexerHelper;
import org.hibernate.models.jandex.internal.JandexModelBuildingContextImpl;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.orm.OrmAnnotationHelper;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class SourceModelTestHelper {

	public static SourceModelBuildingContext createBuildingContext(Class<?> modelClass) {
		return createBuildingContext( SIMPLE_CLASS_LOADING, modelClass );
	}

	public static SourceModelBuildingContext createBuildingContext(Class<?>... modelClasses) {
		return createBuildingContext( SIMPLE_CLASS_LOADING, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(ClassLoading classLoadingAccess, Class<?>... modelClasses) {
		final Index jandexIndex = buildJandexIndex( classLoadingAccess, modelClasses );
		return createBuildingContext( jandexIndex, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(IndexView jandexIndex, Class<?> modelClass) {
		return createBuildingContext( jandexIndex, SIMPLE_CLASS_LOADING, modelClass );
	}

	public static SourceModelBuildingContext createBuildingContext(IndexView jandexIndex, Class<?>... modelClasses) {
		return createBuildingContext( jandexIndex, SIMPLE_CLASS_LOADING, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(
			IndexView jandexIndex,
			ClassLoading classLoadingAccess,
			Class<?>... modelClasses) {
		final SourceModelBuildingContext ctx;
		if ( jandexIndex == null ) {
			ctx = new BasicModelBuildingContextImpl(
					classLoadingAccess,
					(contributions, buildingContext1) -> OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation )
			);
		}
		else {
			ctx = new JandexModelBuildingContextImpl(
					jandexIndex,
					classLoadingAccess,
					(contributions, buildingContext1) -> OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation )
			);

			for ( ClassInfo knownClass : jandexIndex.getKnownClasses() ) {
				ctx.getClassDetailsRegistry().resolveClassDetails( knownClass.name().toString() );

				if ( knownClass.isAnnotation() ) {
					final Class<? extends Annotation> annotationClass = classLoadingAccess.classForName( knownClass.name().toString() );
					ctx.getAnnotationDescriptorRegistry().resolveDescriptor(
							annotationClass,
							annotationType -> JdkBuilders.buildAnnotationDescriptor(
									annotationType,
									ctx
							)
					);
				}
			}
		}

		if ( CollectionHelper.isNotEmpty( modelClasses ) ) {
			for ( Class<?> modelClass : modelClasses ) {
				ctx.getClassDetailsRegistry().resolveClassDetails( modelClass.getName() );
			}
		}

		return ctx;
	}

	public static Index buildJandexIndex(Class<?>... modelClasses) {
		return buildJandexIndex( SIMPLE_CLASS_LOADING, modelClasses );
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
