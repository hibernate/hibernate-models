/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class JandexAnnotationDescriptorRegistry extends AnnotationDescriptorRegistryStandard {
	public JandexAnnotationDescriptorRegistry(SourceModelBuildingContext modelBuildingContext) {
		super( modelBuildingContext );
	}

	@Override
	protected <A extends Annotation> AnnotationDescriptor<A> buildAnnotationDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<? extends Annotation> containerDescriptor) {
		return new JandexAnnotationDescriptorImpl<>( javaType, containerDescriptor, getModelBuildingContext() );
	}
}
