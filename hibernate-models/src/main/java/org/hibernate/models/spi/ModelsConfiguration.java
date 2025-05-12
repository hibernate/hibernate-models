/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.Settings;
import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.internal.ModelsLogging;
import org.hibernate.models.internal.SimpleClassLoading;

import static java.lang.Boolean.parseBoolean;

/**
 * Bootstrapping of {@linkplain ModelsContext}
 *
 * @author Steve Ebersole
 */
public class ModelsConfiguration {
	private final Map<Object,Object> configValues = new HashMap<>();

	private ClassLoading classLoading = SimpleClassLoading.SIMPLE_CLASS_LOADING;
	private RegistryPrimer registryPrimer;

	private ModelsContextProvider explicitContextProvider;

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
	 * the {@linkplain ModelsContext} is first built.
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
	 * An {@linkplain ModelsContextProvider explicit provider} to use.
	 *
	 * @see #setExplicitContextProvider
	 */
	public ModelsContextProvider getExplicitContextProvider() {
		return explicitContextProvider;
	}

	/**
	 * Specify an {@linkplain #getExplicitContextProvider explicit provider} for
	 * {@linkplain ModelsContext} instances.
	 *
	 * @implNote Prefer use of Java {@linkplain java.util.ServiceLoader service loading}
	 * for supplying a specific provider.
	 */
	public ModelsConfiguration setExplicitContextProvider(ModelsContextProvider explicitContextProvider) {
		this.explicitContextProvider = explicitContextProvider;
		return this;
	}

	/**
	 * Build the {@linkplain ModelsContext} instance.
	 */
	public ModelsContext bootstrap() {
		if ( explicitContextProvider != null ) {
			final ModelsContext context = explicitContextProvider.produceContext(
					classLoading,
					registryPrimer,
					configValues
			);
			if ( context != null ) {
				return context;
			}
			ModelsLogging.MODELS_LOGGER.debugf( "Explicit ModelsContext returned null" );
		}

		final Collection<ModelsContextProvider> discoveredProviders = classLoading.loadJavaServices( ModelsContextProvider.class );
		if ( discoveredProviders.size() > 1 ) {
			ModelsLogging.MODELS_LOGGER.debugf( "Multiple ModelsContextProvider implementations found" );
		}
		for ( ModelsContextProvider provider : discoveredProviders ) {
			final ModelsContext context = provider.produceContext(
					classLoading,
					registryPrimer,
					configValues
			);
			if ( context != null ) {
				return context;
			}
		}

		return new BasicModelsContextImpl( classLoading, shouldTrackImplementors(), registryPrimer );
	}

	private boolean shouldTrackImplementors() {
		return shouldTrackImplementors( configValues );
	}

	public static boolean shouldTrackImplementors(Map<Object, Object> configValues) {
		final Object value = configValues.get( Settings.TRACK_IMPLEMENTORS );
		if ( value != null ) {
			return value instanceof Boolean bool
					? bool
					: parseBoolean( value.toString() );
		}
		// false by default
		return false;
	}
}
