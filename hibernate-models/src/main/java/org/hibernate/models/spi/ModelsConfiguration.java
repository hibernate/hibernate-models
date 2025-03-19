/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.internal.ModelsLogging;
import org.hibernate.models.internal.SimpleClassLoading;

/**
 * Bootstrapping of {@linkplain SourceModelBuildingContext}
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

	/**
	 * Configuration values in effect.
	 *
	 * @apiNote Configuration settings only have effect with certain
	 * providers.
	 */
	public Map<Object, Object> getConfigValues() {
		return configValues;
	}

	/**
	 * Provide a {@linkplain #getConfigValues() configuration value}.
	 */
	public ModelsConfiguration configValue(Object key, Object value) {
		setConfigValue( key, value );
		return this;
	}

	/**
	 * Provide a {@linkplain #getConfigValues() configuration value}.
	 */
	public Object setConfigValue(Object key, Object value) {
		return configValues.put( key, value );
	}

	/**
	 * {@linkplain ClassLoading} to use.
	 */
	public ClassLoading getClassLoading() {
		return classLoading;
	}

	/**
	 * Specify a specific {@linkplain #getClassLoading() ClassLoading} to use.
	 */
	public ModelsConfiguration setClassLoading(ClassLoading classLoading) {
		this.classLoading = classLoading;
		return this;
	}

	/**
	 * A primer for {@linkplain ClassDetailsRegistry} and
	 * {@linkplain AnnotationDescriptorRegistry} applied when
	 * the {@linkplain SourceModelBuildingContext} is first built.
	 */
	public RegistryPrimer getRegistryPrimer() {
		return registryPrimer;
	}

	/**
	 * Specify a {@linkplain #getRegistryPrimer() registry primer}.
	 */
	public ModelsConfiguration setRegistryPrimer(RegistryPrimer registryPrimer) {
		this.registryPrimer = registryPrimer;
		return this;
	}

	/**
	 * An {@linkplain SourceModelBuildingContextProvider explicit provider} to use.
	 *
	 * @see #setExplicitContextProvider
	 */
	public SourceModelBuildingContextProvider getExplicitContextProvider() {
		return explicitContextProvider;
	}

	/**
	 * Specify an {@linkplain #getExplicitContextProvider explicit provider} for
	 * {@linkplain SourceModelBuildingContext} instances.
	 *
	 * @implNote Prefer use of Java {@linkplain java.util.ServiceLoader service loading}
	 * for supplying a specific provider.
	 */
	public ModelsConfiguration setExplicitContextProvider(SourceModelBuildingContextProvider explicitContextProvider) {
		this.explicitContextProvider = explicitContextProvider;
		return this;
	}

	/**
	 * Build the {@linkplain SourceModelBuildingContext} instance.
	 */
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
		for ( SourceModelBuildingContextProvider provider : discoveredProviders ) {
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
