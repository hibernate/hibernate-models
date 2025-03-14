/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.hibernate.models.ModelsException;
import org.hibernate.models.bytebuddy.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.orm.OrmAnnotationHelper;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;

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
		final TypePool typePool = buildByteBuddyTypePool( classLoadingAccess, modelClasses );
		return createBuildingContext( typePool, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(TypePool typePool, Class<?> modelClass) {
		return createBuildingContext( typePool, SIMPLE_CLASS_LOADING, modelClass );
	}

	public static SourceModelBuildingContext createBuildingContext(TypePool typePool, Class<?>... modelClasses) {
		return createBuildingContext( typePool, SIMPLE_CLASS_LOADING, modelClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(
			TypePool typePool,
			ClassLoading classLoadingAccess,
			Class<?>... modelClasses) {
		final SourceModelBuildingContext ctx;
		if ( typePool == null ) {
			ctx = new BasicModelBuildingContextImpl(
					classLoadingAccess,
					(contributions, buildingContext1) -> OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation )
			);
		}
		else {
			ctx = new SourceModelBuildingContextImpl(
					typePool,
					classLoadingAccess,
					(contributions, buildingContext1) -> OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation )
			);
		}

		if ( CollectionHelper.isNotEmpty( modelClasses ) ) {
			for ( Class<?> modelClass : modelClasses ) {
				ctx.getClassDetailsRegistry().resolveClassDetails( modelClass.getName() );
			}
		}

		return ctx;
	}

	public static TypePool buildByteBuddyTypePool(Class<?>... modelClasses) {
		return buildByteBuddyTypePool( SIMPLE_CLASS_LOADING, modelClasses );
	}

	public static TypePool buildByteBuddyTypePool(ClassLoading classLoadingAccess, Class<?>... modelClasses) {
		final TypePool typePool = TypePool.Default.of( new ClassLoadingBridge( classLoadingAccess ) );
		for ( Class<?> modelClass : modelClasses ) {
			typePool.describe( modelClass.getName() ).resolve();
		}
		return typePool;
	}

	private static class ClassLoadingBridge implements ClassFileLocator {
		private final ClassLoading classLoading;

		public ClassLoadingBridge(ClassLoading classLoading) {
			this.classLoading = classLoading;
		}

		@Override
		public Resolution locate(String name) throws IOException {
			final String classFileName = toClassFileName( name );
			final URL locatedResource = classLoading.locateResource( classFileName );
			if ( locatedResource == null ) {
				throw new ModelsException( "Unable to locate resource : " + name + " (" + classFileName + ")" );
			}
			try (InputStream stream = locatedResource.openStream()) {
				return new Resolution.Explicit( stream.readAllBytes() );
			}
		}

		private String toClassFileName(String className) {
			return className.replace( '.', '/' ) + ".class";
		}

		@Override
		public void close() throws IOException {

		}
	}
}
