/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;

import org.hibernate.models.internal.jdk.AnnotationDescriptorImpl;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;

/**
 * Access to AnnotationDescriptor instances based on a number of look-ups
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorRegistryStandard extends AbstractAnnotationDescriptorRegistry {
	private final SourceModelBuildingContextImpl modelBuildingContext;

	public AnnotationDescriptorRegistryStandard(SourceModelBuildingContextImpl modelBuildingContext) {
		this.modelBuildingContext = modelBuildingContext;
	}

	public void register(AnnotationDescriptor<?> descriptor) {
		descriptorMap.put( descriptor.getAnnotationType(), descriptor );
		if ( descriptor.getRepeatableContainer() != null ) {
			// the descriptor is repeatable - register it under its container
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

		final AnnotationDescriptorImpl<A> descriptor = new AnnotationDescriptorImpl<>(
				javaType,
				containerDescriptor,
				modelBuildingContext
		);
		descriptorMap.put( javaType, descriptor );
		return descriptor;
	}

	@Override
	public AnnotationDescriptorRegistry makeImmutableCopy() {
		return new AnnotationDescriptorRegistryImmutable( descriptorMap, repeatableByContainerMap );
	}
}
