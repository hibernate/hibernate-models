/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.internal.StandardAnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationDescription;

/**
 * @author Steve Ebersole
 */
public class AnnotationDescriptorImpl<A extends Annotation> extends StandardAnnotationDescriptor<A> {
	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			SourceModelBuildingContext buildingContext) {
		super( annotationType, buildingContext );
	}

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext buildingContext) {
		super( annotationType, repeatableContainer, buildingContext );
	}

	public A createUsage(AnnotationDescription annotationDescription, ByteBuddyModelsContext context) {
		return AnnotationUsageBuilder.makeUsage( annotationDescription, this, context );
	}
}
