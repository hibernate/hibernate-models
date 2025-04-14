/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Map;

import org.hibernate.models.internal.BasicModelsContextImpl;

/**
 * {@linkplain java.util.ServiceLoader Java service loadable} support for
 * providing {@linkplain ModelsContext} implementations.
 *
 * @apiNote If none found, or if the provider(s) return null, the expectation is that the
 * {@linkplain BasicModelsContextImpl default implementation} will be used.
 *
 * @author Steve Ebersole
 */
public interface ModelsContextProvider {
	/**
	 * Produce the ModelsContext.
	 *
	 * @return The alternate context, or null.
	 */
	ModelsContext produceContext(ClassLoading classLoading, RegistryPrimer registryPrimer, Map<Object,Object> configProperties);
}
