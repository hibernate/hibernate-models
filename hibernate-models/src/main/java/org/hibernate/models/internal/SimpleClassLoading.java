/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.spi.ClassLoading;

/**
 * ClassLoading which simply uses our ClassLoader directly
 *
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

	@Override
	public <S> Collection<S> loadJavaServices(Class<S> serviceType) {
		final ServiceLoader<S> loadedServices = ServiceLoader.load( serviceType );
		final Iterator<S> iterator = loadedServices.iterator();
		final Set<S> services = new HashSet<>();
		while ( iterator.hasNext() ) {
			services.add( iterator.next() );
		}
		return services;
	}
}
