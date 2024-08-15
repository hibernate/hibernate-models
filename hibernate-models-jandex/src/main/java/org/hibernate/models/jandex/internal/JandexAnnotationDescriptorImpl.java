/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.StandardAnnotationDescriptor;
import org.hibernate.models.jandex.spi.JandexAnnotationDescriptor;
import org.hibernate.models.jandex.spi.JandexModelBuildingContext;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * @author Steve Ebersole
 */
public class JandexAnnotationDescriptorImpl<A extends Annotation>
		extends StandardAnnotationDescriptor<A>
		implements JandexAnnotationDescriptor<A> {
	public JandexAnnotationDescriptorImpl(
			Class<A> annotationType,
			SourceModelBuildingContext buildingContext) {
		super( annotationType, buildingContext );
	}

	public JandexAnnotationDescriptorImpl(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext buildingContext) {
		super( annotationType, repeatableContainer, buildingContext );
	}

	@Override
	public A createUsage(AnnotationInstance jandexAnnotation, JandexModelBuildingContext context) {
		return AnnotationUsageBuilder.makeUsage( jandexAnnotation, this, context );
	}
}
