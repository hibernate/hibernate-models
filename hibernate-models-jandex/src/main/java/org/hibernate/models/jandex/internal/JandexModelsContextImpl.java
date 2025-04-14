/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.internal.AbstractModelsContext;
import org.hibernate.models.internal.MutableAnnotationDescriptorRegistry;
import org.hibernate.models.jandex.spi.JandexModelsContext;
import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.jandex.spi.JandexValueExtractor;
import org.hibernate.models.serial.internal.StorableContextImpl;
import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.ValueTypeDescriptor;

import org.jboss.jandex.IndexView;

import static org.hibernate.models.internal.ModelsClassLogging.MODELS_CLASS_LOGGER;

/**
 * Implementation of JandexModelsContext.
 *
 * @author Steve Ebersole
 */
public class JandexModelsContextImpl extends AbstractModelsContext implements JandexModelsContext {
	private final IndexView jandexIndex;

	private final JandexAnnotationDescriptorRegistry descriptorRegistry;
	private final JandexClassDetailsRegistry classDetailsRegistry;

	@SuppressWarnings("rawtypes")
	private final Map<ValueTypeDescriptor, JandexValueConverter> valueConverters = new HashMap<>();
	@SuppressWarnings("rawtypes")
	private final Map<ValueTypeDescriptor,JandexValueExtractor> valueExtractors = new HashMap<>();

	public JandexModelsContextImpl(
			IndexView jandexIndex,
			ClassLoading classLoading,
			RegistryPrimer registryPrimer) {
		super( classLoading );

		assert jandexIndex != null;
		this.jandexIndex = jandexIndex;

		MODELS_CLASS_LOGGER.debugf( "Using Jandex support" );

		this.descriptorRegistry = new JandexAnnotationDescriptorRegistry( this );
		this.classDetailsRegistry = new JandexClassDetailsRegistry( jandexIndex, this );

		primeRegistries( registryPrimer );
	}

	@Override
	public MutableAnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return descriptorRegistry;
	}

	@Override
	public JandexClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public IndexView getJandexIndex() {
		return jandexIndex;
	}

	@Override
	public <V> JandexValueConverter<V> getJandexValueConverter(ValueTypeDescriptor<V> valueTypeDescriptor) {
		//noinspection unchecked
		final JandexValueConverter<V> existing = valueConverters.get( valueTypeDescriptor );
		if ( existing != null ) {
			return existing;
		}

		return JandexBuilders.buildValueHandlersReturnConverter(
				valueTypeDescriptor,
				valueConverters::put,
				valueExtractors::put,
				this
		);
	}

	@Override
	public <V> JandexValueExtractor<V> getJandexValueExtractor(ValueTypeDescriptor<V> valueTypeDescriptor) {
		//noinspection unchecked
		final JandexValueExtractor<V> existing = valueExtractors.get( valueTypeDescriptor );
		if ( existing != null ) {
			return existing;
		}

		return JandexBuilders.buildValueHandlersReturnExtractor(
				valueTypeDescriptor,
				valueConverters::put,
				valueExtractors::put,
				this
		);
	}

	@Override
	public StorableContext toStorableForm() {
		return new StorableContextImpl( classDetailsRegistry.classDetailsMap(), descriptorRegistry.descriptorMap() );
	}
}
