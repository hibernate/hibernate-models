/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import static org.hibernate.models.internal.ModelsAnnotationLogging.MODELS_ANNOTATION_LOGGER;

/**
 * Access to AnnotationDescriptor instances based on a number of look-ups
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorRegistryStandard
		extends AbstractAnnotationDescriptorRegistry
		implements MutableAnnotationDescriptorRegistry {
	private final SourceModelBuildingContext modelBuildingContext;

	public AnnotationDescriptorRegistryStandard(SourceModelBuildingContext modelBuildingContext) {
		this.modelBuildingContext = modelBuildingContext;
	}

	public void register(AnnotationDescriptor<?> descriptor) {
		MODELS_ANNOTATION_LOGGER.tracef( "Registering AnnotationDescriptor - %s", descriptor );
		descriptorMap.put( descriptor.getAnnotationType(), descriptor );
		if ( descriptor.getRepeatableContainer() != null ) {
			// the descriptor is repeatable - register it under its container
			MODELS_ANNOTATION_LOGGER.tracef( "Registering repeatable AnnotationDescriptor - %s", descriptor.getRepeatableContainer() );
			repeatableByContainerMap.put( descriptor.getRepeatableContainer(), descriptor );
		}
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> getDescriptor(Class<A> javaType) {
		return resolveDescriptor( javaType, this::buildAdHocAnnotationDescriptor );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> resolveDescriptor(
			Class<A> javaType,
			DescriptorCreator<A> creator) {
		//noinspection unchecked
		final AnnotationDescriptor<A> existing = (AnnotationDescriptor<A>) descriptorMap.get( javaType );
		if ( existing != null ) {
			return existing;
		}

		final AnnotationDescriptor<A> created = creator.createDescriptor( javaType );
		descriptorMap.put( javaType, created );
		return created;
	}

	private <A extends Annotation> AnnotationDescriptor<A> buildAdHocAnnotationDescriptor(Class<A> javaType) {
		final Repeatable repeatable = javaType.getAnnotation( Repeatable.class );
		final AnnotationDescriptor<? extends Annotation> containerDescriptor;
		if ( repeatable != null ) {
			containerDescriptor = getDescriptor( repeatable.value() );
			assert containerDescriptor != null;
		}
		else {
			containerDescriptor = null;
		}

		final AnnotationDescriptor<A> descriptor = buildAnnotationDescriptor( javaType, containerDescriptor );
		descriptorMap.put( javaType, descriptor );
		return descriptor;
	}

	protected <A extends Annotation> AnnotationDescriptor<A> buildAnnotationDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<? extends Annotation> containerDescriptor) {
		return new StandardAnnotationDescriptor<>(
				javaType,
				containerDescriptor,
				modelBuildingContext
		);
	}
}
