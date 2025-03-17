/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.intg;

import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Java service loadable factory for {@linkplain SourceModelBuildingContext}
 * instance as part of testing.
 *
 * @author Steve Ebersole
 */
public interface TestingModelContextFactory {
	SourceModelBuildingContext createModelContext(RegistryPrimer registryPrimer, Class<?>... modelClasses);
}
