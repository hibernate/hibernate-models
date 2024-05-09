/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

import org.hibernate.models.internal.AnnotationHelper;

/**
 * AnnotationScope with the ability to resolve "named" annotations
 *
 * @author Steve Ebersole
 */
public interface SharedNamedAnnotationScope extends SharedAnnotationScope {
	/**
	 * Get a usage of the given annotation {@code annotationDescriptor} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param annotationDescriptor The type of annotation to search
	 * @param matchName The name to match.
	 * @return The matching annotation usage, or {@code null} if none matched
	 */
	default <X extends Annotation> X getNamedAnnotation(
			AnnotationDescriptor<X> annotationDescriptor,
			String matchName) {
		return getNamedAnnotation( annotationDescriptor, matchName, "name" );
	}

	/**
	 * Get a usage of the given {@code annotationDescriptor} whose
	 * {@code attributeToMatch} attribute value matches the given {@code matchName}.
	 *
	 * @param annotationDescriptor The type of annotation to search
	 * @param matchName The name to match.
	 * @param attributeToMatch Name of the attribute to match on.
	 *
	 * @return The matching annotation usage, or {@code null} if none matched
	 */
	default <A extends Annotation> A getNamedAnnotation(
			AnnotationDescriptor<A> annotationDescriptor,
			String matchName,
			String attributeToMatch) {
		final List<A> allUsages = getAllAnnotationUsages( annotationDescriptor );
		final AttributeDescriptor<String> matchAttribute = annotationDescriptor.getAttribute( attributeToMatch );
		for ( int i = 0; i < allUsages.size(); i++ ) {
			final A annotationUsage = allUsages.get( i );
			final String usageName = AnnotationHelper.extractValue( annotationUsage, matchAttribute );
			if ( Objects.equals( matchName, usageName ) ) {
				return annotationUsage;
			}
		}
		return null;
	}
}
