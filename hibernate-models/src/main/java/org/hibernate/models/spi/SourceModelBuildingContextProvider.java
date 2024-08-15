/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.util.Map;

/**
 * Pluggable support for optional {@linkplain SourceModelBuildingContext} implementations, resolved
 * using {@linkplain java.util.ServiceLoader Java services}.
 *
 * @apiNote If none found, or if the provider(s) return null, the expectation is that the
 * {@linkplain org.hibernate.models.internal.BasicModelBuildingContextImpl default impl} should be used.
 *
 * @author Steve Ebersole
 */
public interface SourceModelBuildingContextProvider {
	/**
	 * Produce an alternate SourceModelBuildingContext.
	 *
	 * @return The alternate context, or null.
	 */
	SourceModelBuildingContext produceContext(ClassLoading classLoading, RegistryPrimer registryPrimer, Map<Object,Object> configProperties);
}
