/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

import java.lang.annotation.Annotation;
import java.util.Locale;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;

/**
 * Indicates an attempt to {@linkplain org.hibernate.models.spi.MutableAnnotationTarget#addAnnotationUsage add}
 * a {@linkplain AnnotationDescriptor#isRepeatable() repeatable} annotation to a target.  The repeatable
 * {@linkplain AnnotationDescriptor#getRepeatableContainer() container} should always be used instead.
 *
 * @author Steve Ebersole
 */
public class RepeatableAnnotationException extends ModelsException {
	public RepeatableAnnotationException(AnnotationDescriptor<? extends Annotation> repeatableDescriptor, AnnotationTarget target) {
		super(
				String.format(
						Locale.ROOT,
						"A repeatable annotation (%s) was added to a target (%s); use the container annotation (%s) instead",
						repeatableDescriptor.getAnnotationType().getName(),
						target.getName(),
						repeatableDescriptor.getRepeatableContainer().getAnnotationType().getName()
				)
		);
	}
}
