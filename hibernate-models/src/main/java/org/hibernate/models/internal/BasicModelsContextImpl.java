/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.Map;

import org.hibernate.models.Settings;
import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.serial.internal.StorableContextImpl;
import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;

import static java.lang.Boolean.parseBoolean;

/**
 * Standard ModelsContext implementation
 *
 * @author Steve Ebersole
 */
public class BasicModelsContextImpl extends AbstractModelsContext {
	private final AnnotationDescriptorRegistryStandard descriptorRegistry;
	private final ClassDetailsRegistryStandard classDetailsRegistry;
	private final ModuleDetailsRegistryStandard moduleDetailsRegistry;

	public BasicModelsContextImpl(
			ClassLoading classLoadingAccess,
			boolean trackImplementors,
			RegistryPrimer registryPrimer) {
		this( classLoadingAccess, HibernateAccessorFactory.reflection(), trackImplementors, registryPrimer );
	}

	public BasicModelsContextImpl(
			ClassLoading classLoadingAccess,
			HibernateAccessorFactory accessorFactory,
			boolean trackImplementors,
			RegistryPrimer registryPrimer) {
		super( classLoadingAccess, accessorFactory );

		this.descriptorRegistry = new AnnotationDescriptorRegistryStandard( this );
		this.classDetailsRegistry = new ClassDetailsRegistryStandard( trackImplementors, this );
		this.moduleDetailsRegistry = new ModuleDetailsRegistryStandard( this );

		primeRegistries( registryPrimer );
	}

	private static boolean shouldTrackImplementors(Map<Object, Object> configValues) {
		final Object value = configValues.get( Settings.TRACK_IMPLEMENTORS );
		if ( value != null ) {
			return value instanceof Boolean bool
					? bool
					: parseBoolean( value.toString() );
		}
		// false by default
		return false;
	}

	@Override
	public MutableAnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return descriptorRegistry;
	}

	@Override
	public MutableClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public ModuleDetailsRegistryStandard getModuleDetailsRegistry() {
		return moduleDetailsRegistry;
	}

	@Override
	public StorableContext toStorableForm() {
		return new StorableContextImpl(
				classDetailsRegistry.isTrackingImplementors(),
				classDetailsRegistry.classDetailsMap,
				descriptorRegistry.descriptorMap
		);
	}
}
