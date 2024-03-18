/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.AnnotationHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.AttributeDescriptor;

import static org.hibernate.models.internal.jdk.JdkBuilders.extractAttributeDescriptors;

/**
 * AnnotationDescriptor for cases where we do not want to deal with ANNOTATION_TYPE annotations,
 * most notably, Jakarta and Hibernate annotations
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorOrmImpl<A extends Annotation> implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final EnumSet<Kind> allowableTargets;

	private final boolean inherited;
	private final AnnotationDescriptor<?> repeatableContainer;

	private final List<AttributeDescriptor<?>> attributeDescriptors;

	public AnnotationDescriptorOrmImpl(Class<A> annotationType, AnnotationDescriptor<?> repeatableContainer) {
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

	@Override
	public Collection<AnnotationUsage<?>> getAllAnnotationUsages() {
		return Collections.emptyList();
	}

	@Override
	public <X extends Annotation> boolean hasAnnotationUsage(Class<X> type) {
		return false;
	}

	@Override
	public <A extends Annotation> boolean hasRepeatableAnnotationUsage(Class<A> type) {
		return false;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getAnnotationUsage(AnnotationDescriptor<X> descriptor) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getAnnotationUsage(Class<X> type) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> locateAnnotationUsage(Class<X> type) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> List<AnnotationUsage<X>> getRepeatedAnnotationUsages(AnnotationDescriptor<X> type) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> List<AnnotationUsage<X>> getRepeatedAnnotationUsages(Class<X> type) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> void forEachAnnotationUsage(
			AnnotationDescriptor<X> type,
			Consumer<AnnotationUsage<X>> consumer) {
		// there are none
	}

	@Override
	public <X extends Annotation> void forEachAnnotationUsage(Class<X> type, Consumer<AnnotationUsage<X>> consumer) {
		// there are none
	}

	public <X extends Annotation> List<AnnotationUsage<? extends Annotation>> getMetaAnnotated(Class<X> metaAnnotationType) {
		// there are none
		return Collections.emptyList();
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch) {
		// there are none
		return null;
	}

	@Override
	public String toString() {
		return "AnnotationDescriptor(" + annotationType + ")";
	}
}
