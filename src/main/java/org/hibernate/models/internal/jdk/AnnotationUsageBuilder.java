/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;

/**
 * Helper for building {@link AnnotationUsage} instances
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageBuilder {
	/**
	 * Process annotations creating usage instances passed back to the consumer
	 */
	public static void processAnnotations(
			Annotation[] annotations,
			AnnotationTarget target,
			BiConsumer<Class<? extends Annotation>, AnnotationUsage<?>> consumer,
			SourceModelBuildingContext buildingContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		for ( int i = 0; i < annotations.length; i++ ) {
			final Annotation annotation = annotations[ i ];
			final Class<? extends Annotation> annotationType = annotation.annotationType();

			// skip a few well-know ones that are irrelevant
			if ( annotationType == Repeatable.class
					|| annotationType == Target.class
					|| annotationType == Retention.class
					|| annotationType == Documented.class ) {
				continue;
			}

			//noinspection rawtypes
			final AnnotationDescriptor annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationType );
			//noinspection unchecked
			final AnnotationUsage<?> usage = makeUsage(
					annotation,
					annotationDescriptor,
					target,
					buildingContext
			);
			consumer.accept( annotationType, usage );
		}
	}

	public static <A extends Annotation> AnnotationUsage<A> makeUsage(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		//noinspection unchecked,rawtypes
		return new JdkAnnotationUsage( annotation, annotationDescriptor, target, buildingContext );
	}

	/**
	 * Extracts values from an annotation
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <A extends Annotation> Map<String,?> extractAttributeValues(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		final ConcurrentHashMap<String, Object> valueMap = new ConcurrentHashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AttributeDescriptor attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			final ValueExtractor valueExtractor = attributeDescriptor
					.getTypeDescriptor()
					.createJdkExtractor( buildingContext );
			final Object value = valueExtractor.extractValue( annotation, attributeDescriptor, target, buildingContext );
			valueMap.put( attributeDescriptor.getName(), value );
		}
		return valueMap;
	}

	private AnnotationUsageBuilder() {
		// disallow direct instantiation
	}
}
