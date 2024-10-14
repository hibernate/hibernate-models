/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.internal.ClassDetailsRegistryStandard;
import org.hibernate.models.internal.MutableAnnotationDescriptorRegistry;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.serial.spi.SerialAnnotationDescriptor;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;

/**
 * SourceModelBuildingContext implementation used with serialization support.
 *
 * @implNote This implementation is considered immutable after construction.  It implements
 * {@linkplain SourceModelBuildingContext} instead of just {@linkplain SourceModelContext}
 * simply for ease of coding.  From the API point of view, via {@linkplain StorableContext#fromStorableForm},
 * this implementation is always treated as an immutable {@linkplain SourceModelContext}.
 *
 * @author Steve Ebersole
 */
public class RestoredModelContext implements SourceModelBuildingContext {
	private final MutableAnnotationDescriptorRegistry annotationDescriptorRegistry;
	private final MutableClassDetailsRegistry classDetailsRegistry;

	private ClassLoading classLoading;

	public RestoredModelContext(StorableContextImpl serialContext, ClassLoading classLoading) {
		this.classLoading = classLoading;

		final ClassDetailsBuilderImpl classDetailsBuilder = new ClassDetailsBuilderImpl( serialContext, classLoading );

		this.annotationDescriptorRegistry = new AnnotationDescriptorRegistryStandard( this );
		this.classDetailsRegistry = new ClassDetailsRegistryStandard( classDetailsBuilder, this );

		for ( Map.Entry<String, SerialClassDetails> classDetailsEntry : serialContext.getSerialClassDetailsMap().entrySet() ) {
			classDetailsRegistry.resolveClassDetails( classDetailsEntry.getKey() );
		}

		for ( Map.Entry<Class<? extends Annotation>, SerialAnnotationDescriptor<? extends Annotation>> annotationDescriptorEntry
				: serialContext.getSerialAnnotationDescriptorMap().entrySet() ) {
			final SerialAnnotationDescriptor<? extends Annotation> serialDescriptor = annotationDescriptorEntry.getValue();
			annotationDescriptorRegistry.register( serialDescriptor.fromStorableForm( this ) );
		}

		classLoading = null;
		classDetailsBuilder.invalidate();
	}

	@Override
	public ClassLoading getClassLoading() {
		return classLoading;
	}

	@Override
	public AnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return annotationDescriptorRegistry;
	}

	@Override
	public ClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public StorableContext toStorableForm() {
		throw new UnsupportedOperationException( );
	}
}
