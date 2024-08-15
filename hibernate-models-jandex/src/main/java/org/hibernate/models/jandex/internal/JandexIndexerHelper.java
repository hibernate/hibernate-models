/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.hibernate.models.ModelsException;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.Indexer;

/**
 * Helper for dealing with the Jandex {@linkplain Indexer}
 *
 * @author Steve Ebersole
 */
public class JandexIndexerHelper {
	/**
	 * Apply a class to the indexer
	 *
	 * @param clazz The class to apply to the indexer
	 * @param indexer The Jandex Indexer
	 * @param classLoading Used to load the class file (bytes)
	 *
	 * @implNote Simply delegates to {@linkplain #apply(String, Indexer, ClassLoading)} with the class name
	 */
	public static void apply(Class<?> clazz, Indexer indexer, ClassLoading classLoading) {
		apply( clazz.getName(), indexer, classLoading );
	}

	/**
	 * Apply a class to the indexer
	 *
	 * @param className The class to apply to the indexer
	 * @param indexer The Jandex Indexer
	 * @param classLoading Used to load the class file (bytes)
	 */
	public static void apply(String className, Indexer indexer, ClassLoading classLoading) {
		final String resourceName = StringHelper.classNameToResourceName( className );
		final URL resource = classLoading.locateResource( resourceName );
		if ( resource == null ) {
			throw new JandexIndexingException( className );
		}
		try (InputStream inputStream = resource.openStream()) {
			indexer.index( inputStream );
		}
		catch (IOException e) {
			throw new JandexIndexingException( e );
		}
	}

	public static class JandexIndexingException extends ModelsException {
		public JandexIndexingException(String className) {
			super( "Could not locate classpath resource for " + className );
		}

		public JandexIndexingException(Throwable cause) {
			super( "Error indexing standard types", cause );
		}
	}
}
