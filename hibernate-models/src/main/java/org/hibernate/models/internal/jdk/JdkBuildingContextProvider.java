/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.util.Map;

import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelBuildingContextProvider;

/**
 * @author Steve Ebersole
 */
public class JdkBuildingContextProvider implements SourceModelBuildingContextProvider {

	@Override
	public SourceModelBuildingContext produceContext(
			ClassLoading classLoading,
			RegistryPrimer registryPrimer,
			Map<Object,Object> configProperties) {
		return null;
	}
}
