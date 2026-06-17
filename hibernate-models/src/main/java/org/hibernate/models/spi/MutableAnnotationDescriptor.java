/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.Map;

import org.hibernate.models.internal.ConcreteTypeAnnotationDescriptor;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptor<A extends Annotation, C extends A> extends AnnotationDescriptor<A> {
	/**
	 * The mutable contract for the given annotation
	 */
	Class<C> getMutableAnnotationType();

	@Override
	C createUsage(ModelsContext context);

	@Override
	C createUsage(A jdkAnnotation, ModelsContext context);

	@Override
	C createUsage(Map<String, Object> attributeValues, ModelsContext context);

	static <A extends Annotation, C extends A> MutableAnnotationDescriptor<A, C> create(
			Class<A> annotationType,
			Class<C> concreteClass,
			EnumSet<AnnotationTarget.Kind> allowableTargets,
			boolean inherited) {
		return new ConcreteTypeAnnotationDescriptor<>( annotationType, concreteClass, allowableTargets, inherited );
	}

	static <A extends Annotation, C extends A> MutableAnnotationDescriptor<A, C> create(
			Class<A> annotationType,
			Class<C> concreteClass,
			EnumSet<AnnotationTarget.Kind> allowableTargets,
			boolean inherited,
			AnnotationDescriptor<?> repeatableContainer) {
		return new ConcreteTypeAnnotationDescriptor<>( annotationType, concreteClass, allowableTargets, inherited, repeatableContainer );
	}
}
