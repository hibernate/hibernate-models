/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.intg;

import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RegistryPrimer;

/**
 * Java service loadable factory for {@linkplain ModelsContext}
 * instance as part of testing.
 *
 * @author Steve Ebersole
 */
public interface ModelsContextFactory {
	/**
	 * Create the ModelsContext to be used for tests
	 */
	ModelsContext createModelContext(RegistryPrimer registryPrimer, Class<?>... modelClasses);

	/**
	 * Create the ModelsContext to be used for tests, with a custom {@linkplain ClassLoading}
	 * and class names (for classes not yet loaded on the test classloader).
	 */
	ModelsContext createModelContext(ClassLoading classLoading, RegistryPrimer registryPrimer, String... classNames);
}
