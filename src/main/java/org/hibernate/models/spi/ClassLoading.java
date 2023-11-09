/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.net.URL;

import org.hibernate.models.Incubating;

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
	 */
	<T> Class<T> classForName(String name);

	Package packageForName(String name);

	/**
	 * Locate a resource by name
	 *
	 * @param resourceName The name of the resource to resolve
	 * @return The located resource;
	 *         may return {@code null} to indicate the resource was not found
	 */
	URL locateResource(String resourceName);
}
