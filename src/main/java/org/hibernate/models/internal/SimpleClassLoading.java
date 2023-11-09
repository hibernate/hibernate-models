/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.net.URL;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.spi.ClassLoading;

/**
 * @author Steve Ebersole
 */
public class SimpleClassLoading implements ClassLoading {
	public static final SimpleClassLoading SIMPLE_CLASS_LOADING = new SimpleClassLoading();

	@Override
	public <T> Class<T> classForName(String name) {
		try {
			//noinspection unchecked
			return (Class<T>) getClass().getClassLoader().loadClass( name );
		}
		catch (ClassNotFoundException e) {
			throw new UnknownClassException( "Unable to locate class - " + name, e );
		}
	}

	@Override
	public Package packageForName(String name) {
		return getClass().getClassLoader().getDefinedPackage( name );
	}

	@Override
	public URL locateResource(String resourceName) {
		return getClass().getClassLoader().getResource( resourceName );
	}
}
