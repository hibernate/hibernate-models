/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.internal.ModelsLogging;
import org.hibernate.models.internal.SimpleClassLoading;

/**
 * Bootstrapping
 *
 * @author Steve Ebersole
 */
public class ModelsConfiguration {
	private final Map<Object,Object> configValues = new HashMap<>();

	private ClassLoading classLoading = SimpleClassLoading.SIMPLE_CLASS_LOADING;
	private RegistryPrimer registryPrimer;

	private SourceModelBuildingContextProvider explicitContextProvider;

	public ModelsConfiguration() {
	}

	public Map<Object, Object> getConfigValues() {
		return configValues;
	}

	public Object configValue(Object key, Object value) {
		return configValues.put( key, value );
	}

	public ClassLoading getClassLoading() {
		return classLoading;
	}

	public void setClassLoading(ClassLoading classLoading) {
		this.classLoading = classLoading;
	}

	public RegistryPrimer getRegistryPrimer() {
		return registryPrimer;
	}

	public void setRegistryPrimer(RegistryPrimer registryPrimer) {
		this.registryPrimer = registryPrimer;
	}

	public SourceModelBuildingContextProvider getExplicitContextProvider() {
		return explicitContextProvider;
	}

	public void setExplicitContextProvider(SourceModelBuildingContextProvider explicitContextProvider) {
		this.explicitContextProvider = explicitContextProvider;
	}

	public SourceModelBuildingContext bootstrap() {
		if ( explicitContextProvider != null ) {
			final SourceModelBuildingContext context = explicitContextProvider.produceContext(
					classLoading,
					registryPrimer,
					configValues
			);
			if ( context != null ) {
				return context;
			}
			ModelsLogging.MODELS_LOGGER.debugf( "Explicit SourceModelBuildingContextProvider returned null" );
		}

		final Collection<SourceModelBuildingContextProvider> discoveredProviders = classLoading.loadJavaServices( SourceModelBuildingContextProvider.class );
		if ( discoveredProviders.size() > 1 ) {
			ModelsLogging.MODELS_LOGGER.debugf( "Multiple SourceModelBuildingContextProvider impls found" );
		}
		final Iterator<SourceModelBuildingContextProvider> iterator = discoveredProviders.iterator();
		while ( iterator.hasNext() ) {
			final SourceModelBuildingContextProvider provider = iterator.next();
			final SourceModelBuildingContext context = provider.produceContext(
					classLoading,
					registryPrimer,
					configValues
			);
			if ( context != null ) {
				return context;
			}
		}

		return new BasicModelBuildingContextImpl( classLoading, registryPrimer );
	}
}
