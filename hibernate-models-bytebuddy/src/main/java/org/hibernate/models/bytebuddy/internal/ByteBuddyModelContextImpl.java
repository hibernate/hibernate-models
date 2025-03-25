/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.bytebuddy.spi.ValueExtractor;
import org.hibernate.models.internal.AbstractModelBuildingContext;
import org.hibernate.models.internal.MutableAnnotationDescriptorRegistry;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.internal.SimpleClassLoading;
import org.hibernate.models.serial.internal.StorableContextImpl;
import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.pool.TypePool;

/**
 * SourceModelBuildingContext implementation based on ByteBuddy, leveraging a
 * {@linkplain TypePool} to inspect
 *
 * @author Steve Ebersole
 */
public class ByteBuddyModelContextImpl
		extends AbstractModelBuildingContext
		implements ByteBuddyModelsContext {
	private final TypePool typePool;

	private final ClassDetailsRegistryImpl classDetailsRegistry;
	private final AnnotationDescriptorRegistryImpl descriptorRegistry;

	private final Map<ValueTypeDescriptor, ValueConverter> valueConverters = new HashMap<>();
	private final Map<ValueTypeDescriptor, ValueExtractor> valueExtractors = new HashMap<>();

	public ByteBuddyModelContextImpl(
			TypePool typePool,
			RegistryPrimer registryPrimer) {
		this( typePool, SimpleClassLoading.SIMPLE_CLASS_LOADING, registryPrimer );
	}

	public ByteBuddyModelContextImpl(
			TypePool typePool,
			ClassLoading classLoading,
			RegistryPrimer registryPrimer) {
		super( classLoading );

		this.typePool = typePool;

		this.classDetailsRegistry = new ClassDetailsRegistryImpl( this );
		this.descriptorRegistry = new AnnotationDescriptorRegistryImpl( this );

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
		return new StorableContextImpl( classDetailsRegistry.getClassDetailsMap(), descriptorRegistry.getDescriptorMap() );
	}

	@Override
	public <V> ValueConverter<V> getValueConverter(ValueTypeDescriptor<V> valueTypeDescriptor) {
		//noinspection unchecked
		final ValueConverter<V> existing = valueConverters.get( valueTypeDescriptor );
		if ( existing != null ) {
			return existing;
		}

		return ByteBuddyBuilders.buildValueHandlersReturnConverter(
				valueTypeDescriptor,
				valueConverters::put,
				valueExtractors::put,
				this
		);
	}

	@Override
	public <V> ValueExtractor<V> getValueExtractor(ValueTypeDescriptor<V> valueTypeDescriptor) {
		//noinspection unchecked
		final ValueExtractor<V> existing = valueExtractors.get( valueTypeDescriptor );
		if ( existing != null ) {
			return existing;
		}

		return ByteBuddyBuilders.buildValueHandlersReturnExtractor(
				valueTypeDescriptor,
				valueConverters::put,
				valueExtractors::put,
				this
		);
	}
}
