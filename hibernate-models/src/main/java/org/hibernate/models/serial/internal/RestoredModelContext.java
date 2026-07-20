/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.util.Map;

import org.hibernate.models.internal.AbstractModelsContext;
import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.internal.ClassDetailsRegistryStandard;
import org.hibernate.models.internal.ModuleDetailsRegistryStandard;
import org.hibernate.models.internal.MutableAnnotationDescriptorRegistry;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;

/**
 * ModelsContext implementation used with serialization support.
 *
 * @author Steve Ebersole
 */
public class RestoredModelContext extends AbstractModelsContext {
	private final MutableAnnotationDescriptorRegistry annotationDescriptorRegistry;
	private final MutableClassDetailsRegistry classDetailsRegistry;
	private final ModuleDetailsRegistryStandard moduleDetailsRegistry;

	public RestoredModelContext(
			Map<String, SerialClassDetails> serialClassDetailsMap,
			ClassLoading classLoading,
			boolean trackImplementors,
			RegistryPrimer registryPrimer) {
		super( classLoading );

		final ClassDetailsBuilderImpl classDetailsBuilder = new ClassDetailsBuilderImpl( serialClassDetailsMap, classLoading );

		this.annotationDescriptorRegistry = new AnnotationDescriptorRegistryStandard( this );
		this.classDetailsRegistry = new ClassDetailsRegistryStandard( classDetailsBuilder, trackImplementors, this );
		this.moduleDetailsRegistry = new ModuleDetailsRegistryStandard( this );

		applyRegistryPrimer( registryPrimer );

		for ( Map.Entry<String, SerialClassDetails> classDetailsEntry : serialClassDetailsMap.entrySet() ) {
			classDetailsRegistry.resolveClassDetails( classDetailsEntry.getKey() );
		}

		classDetailsBuilder.invalidate();
	}

	@Override
	public MutableAnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return annotationDescriptorRegistry;
	}

	@Override
	public MutableClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public ModuleDetailsRegistryStandard getModuleDetailsRegistry() {
		return moduleDetailsRegistry;
	}
}
