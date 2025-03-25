/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import java.lang.annotation.Annotation;

import org.hibernate.models.bytebuddy.internal.ByteBuddyBuilders;
import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting nested annotation values
 *
 * @author Steve Ebersole
 */
public class NestedValueConverter<A extends Annotation> implements ValueConverter<A> {
	private final AnnotationDescriptor<A> descriptor;

	public NestedValueConverter(AnnotationDescriptor<A> descriptor) {
		assert descriptor != null : "AnnotationDescriptor was null";
		this.descriptor = descriptor;
	}

	@Override
	public A convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		final AnnotationDescription resolved = byteBuddyValue.resolve( AnnotationDescription.class );
		return ByteBuddyBuilders.makeUsage( resolved, descriptor, modelContext );
	}
}
