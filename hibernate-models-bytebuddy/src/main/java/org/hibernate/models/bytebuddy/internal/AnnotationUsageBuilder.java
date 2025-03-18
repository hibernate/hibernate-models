/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.bytebuddy.spi.ValueExtractor;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;

/**
 * Helper for building annotation usages/instances based on
 * Jandex {@linkplain AnnotationDescription} references
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageBuilder {
	public static <A extends Annotation> A makeUsage(
			AnnotationDescription annotationDescription,
			AnnotationDescriptor<A> annotationDescriptor,
			SourceModelBuildingContext modelContext) {
		final Map<String, Object> attributeValues = extractAttributeValues(
				annotationDescription,
				annotationDescriptor,
				modelContext
		);
		return annotationDescriptor.createUsage( attributeValues, modelContext );
	}

	private static <A extends Annotation> Map<String, Object> extractAttributeValues(
			AnnotationDescription annotationDescription,
			AnnotationDescriptor<A> annotationDescriptor,
			SourceModelBuildingContext modelContext) {

		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		final ConcurrentHashMap<String, Object> valueMap = new ConcurrentHashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AttributeDescriptor<?> attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			final ValueExtractor<?> extractor = modelContext
					.as( ByteBuddyModelContextImpl.class )
					.getValueExtractor( attributeDescriptor.getTypeDescriptor() );
			final Object attributeValue = extractor.extractValue(
					annotationDescription,
					attributeDescriptor.getName(),
					modelContext
			);
			valueMap.put( attributeDescriptor.getName(), attributeValue );
		}
		return valueMap;
	}

	public static Map<Class<? extends Annotation>, ? extends Annotation> collectUsages(
			AnnotationSource annotationSource,
			ByteBuddyModelsContext modelContext) {
		if ( annotationSource == null ) {
			return Collections.emptyMap();
		}
		final Map<Class<? extends Annotation>, Annotation> result = new HashMap<>();
		processAnnotations(
				annotationSource.getDeclaredAnnotations(),
				result::put,
				modelContext
		);
		return result;
	}

	/**
	 * Process annotations creating usage instances passed back to the consumer
	 */
	public static void processAnnotations(
			AnnotationList annotations,
			BiConsumer<Class<? extends Annotation>, Annotation> consumer,
			ByteBuddyModelsContext buildingContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		for ( AnnotationDescription annotation : annotations ) {
			if ( annotation.getAnnotationType().represents( Documented.class )
					|| annotation.getAnnotationType().represents( Repeatable.class )
					|| annotation.getAnnotationType().represents( Retention.class )
					|| annotation.getAnnotationType().represents( Target.class ) ) {
				continue;
			}

			final Class<? extends Annotation> annotationType = buildingContext
					.getClassLoading()
					.classForName( annotation.getAnnotationType().getTypeName() );
			final AnnotationDescriptor<?> annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationType );
			final Annotation usage = makeUsage(
					annotation,
					annotationDescriptor,
					buildingContext
			);
			consumer.accept( annotationType, usage );
		}
	}
}
