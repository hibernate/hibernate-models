/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.models.internal.AnnotationHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import static org.hibernate.models.internal.jdk.JdkBuilders.extractAttributeDescriptors;

/**
 * AnnotationDescriptor built from the Annotation's Class reference
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorImpl<A extends Annotation> extends AbstractAnnotationTarget implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final EnumSet<Kind> allowableTargets;

	private final boolean inherited;
	private final AnnotationDescriptor<?> repeatableContainer;

	private final List<AttributeDescriptor<?>> attributeDescriptors;

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			SourceModelBuildingContext context) {
		this( annotationType, null, context );
	}

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext context) {
		super( annotationType::getAnnotations, context );

		this.annotationType = annotationType;
		this.repeatableContainer = repeatableContainer;

		this.inherited = AnnotationHelper.isInherited( annotationType );
		this.allowableTargets = AnnotationHelper.extractTargets( annotationType );

		this.attributeDescriptors = extractAttributeDescriptors( this, annotationType );
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public EnumSet<Kind> getAllowableTargets() {
		return allowableTargets;
	}

	@Override
	public boolean isInherited() {
		return inherited;
	}

	@Override
	public AnnotationDescriptor<?> getRepeatableContainer() {
		return repeatableContainer;
	}

	@Override
	public List<AttributeDescriptor<?>> getAttributes() {
		return attributeDescriptors;
	}

	@Override
	public String getName() {
		return annotationType.getName();
	}
}
