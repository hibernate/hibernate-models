/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.bytebuddy.spi.ValueExtractor;
import org.hibernate.models.internal.AbstractModelsContext;
import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.internal.MutableAnnotationDescriptorRegistry;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.serial.internal.StorableContextImpl;
import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.pool.TypePool;

import static org.hibernate.models.internal.ModelsClassLogging.MODELS_CLASS_LOGGER;

/**
 * Implementation of ByteBuddyModelsContext
 *
 * @author Steve Ebersole
 */
public class ByteBuddyModelsContextImpl
		extends AbstractModelsContext
		implements ByteBuddyModelsContext {
	private final TypePool typePool;

	private final ClassDetailsRegistryImpl classDetailsRegistry;
	private final AnnotationDescriptorRegistryStandard descriptorRegistry;

	@SuppressWarnings("rawtypes")
	private final Map<ValueTypeDescriptor, ValueExtractor> valueExtractors = new HashMap<>();

	public ByteBuddyModelsContextImpl(
			TypePool typePool,
			boolean trackImplementors,
			ClassLoading classLoading,
			RegistryPrimer registryPrimer) {
		super( classLoading );

		MODELS_CLASS_LOGGER.debugf( "Using Byte Buddy support" );

		this.typePool = typePool;

		this.classDetailsRegistry = new ClassDetailsRegistryImpl( this, trackImplementors );
		this.descriptorRegistry = new AnnotationDescriptorRegistryStandard( this );

		primeRegistries( registryPrimer );
	}

	@Override
	public TypePool getTypePool() {
		return typePool;
	}

	@Override
	public MutableClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public MutableAnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return descriptorRegistry;
	}

	@Override
	public StorableContext toStorableForm() {
		return new StorableContextImpl(
				classDetailsRegistry.isTrackingImplementors(),
				classDetailsRegistry.classDetailsMap(),
				descriptorRegistry.descriptorMap()
		);
	}

	@Override
	public <V> ValueExtractor<V> getValueExtractor(ValueTypeDescriptor<V> valueTypeDescriptor) {
		//noinspection unchecked
		final ValueExtractor<V> existing = valueExtractors.get( valueTypeDescriptor );
		if ( existing != null ) {
			return existing;
		}

		final ValueExtractor<V> valueExtractor = ByteBuddyBuilders.buildValueExtractor(
				valueTypeDescriptor,
				this
		);
		valueExtractors.put( valueTypeDescriptor, valueExtractor );
		return valueExtractor;
	}
}
