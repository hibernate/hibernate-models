/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.net.URL;
import java.util.Collection;

import org.hibernate.models.Incubating;
import org.hibernate.models.UnknownClassException;

/**
 * Access to load resources from a ClassLoader
 *
 * @author Steve Ebersole
 */
@Incubating
public interface ClassLoading {
	/**
	 * Obtain a {@link Class} reference by name
	 *
	 * @param name The name of the class
	 * @return The {@code Class} object with the given name
	 *
	 * @throws UnknownClassException If no registration is found with the given {@code name}
	 */
	<T> Class<T> classForName(String name);

	/**
	 * Like {@linkplain #classForName(String)}, except here we simply return {@code null} if
	 * there is not a matching class.  This is often useful, e.g., when looking for a package-info.
	 */
	<T> Class<T> findClassForName(String name);

	/**
	 * Locate a resource by name
	 *
	 * @param resourceName The name of the resource to resolve
	 * @return The located resource;
	 *         may return {@code null} to indicate the resource was not found
	 */
	URL locateResource(String resourceName);

	/**
	 * Discovers and instantiates implementations of the given {@link java.util.ServiceLoader Java service} contract.
	 *
	 * @param serviceType The java type defining the service contract
	 * @param <S> The type of the service contract
	 *
	 * @return The ordered set of discovered services.
	 */
	<S> Collection<S> loadJavaServices(Class<S> serviceType);
}
