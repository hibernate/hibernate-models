/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.shared.intg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.hibernate.models.ModelsException;
import org.hibernate.models.bytebuddy.internal.ByteBuddyModelContextImpl;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.intg.ModelContextFactory;

import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class ByteBuddyModelContextFactory implements ModelContextFactory {
	public static final ByteBuddyModelContextFactory CONTEXT_FACTORY = new ByteBuddyModelContextFactory();

	@Override
	public SourceModelBuildingContext createModelContext(
			RegistryPrimer registryPrimer,
			Class<?>... modelClasses) {
		final TypePool byteBuddyTypePool = buildTypePool( modelClasses );
		return new ByteBuddyModelContextImpl( byteBuddyTypePool, SIMPLE_CLASS_LOADING, registryPrimer );
	}

	public static TypePool buildTypePool(Class<?>... modelClasses) {
		return buildTypePool( SIMPLE_CLASS_LOADING, modelClasses );
	}

	public static TypePool buildTypePool(ClassLoading classLoadingAccess, Class<?>... modelClasses) {
		final TypePool typePool = TypePool.Default.of( new ClassLoadingBridge( classLoadingAccess ) );
		for ( Class<?> modelClass : modelClasses ) {
			// load each modelClass into the TypePool
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
		@SuppressWarnings("NullableProblems")
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
		public void close() {

		}
	}
}
