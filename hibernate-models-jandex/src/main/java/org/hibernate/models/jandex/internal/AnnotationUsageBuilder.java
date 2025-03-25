/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.jandex.spi.JandexModelContext;
import org.hibernate.models.jandex.spi.JandexValueExtractor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;

/**
 * Helper for building annotation usages/instances based on
 * Jandex {@linkplain AnnotationInstance} references
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageBuilder {
	public static final DotName REPEATABLE = DotName.createSimple( Repeatable.class );
	public static final DotName TARGET = DotName.createSimple( Target.class );
	public static final DotName RETENTION = DotName.createSimple( Retention.class );
	public static final DotName DOCUMENTED = DotName.createSimple( Documented.class );

	/**
	 * Create the AnnotationUsages map for a given target
	 */
	public static Map<Class<? extends Annotation>, ? extends Annotation> collectUsages(
			org.jboss.jandex.AnnotationTarget jandexAnnotationTarget,
			SourceModelBuildingContext buildingContext) {
		if ( jandexAnnotationTarget == null ) {
			return Collections.emptyMap();
		}
		final Map<Class<? extends Annotation>, Annotation> result = new HashMap<>();
		processAnnotations(
				jandexAnnotationTarget.declaredAnnotations(),
				result::put,
				buildingContext
		);
		return result;
	}

	/**
	 * Process annotations creating usage instances passed back to the consumer
	 */
	public static void processAnnotations(
			Collection<AnnotationInstance> annotations,
			BiConsumer<Class<? extends Annotation>, Annotation> consumer,
			SourceModelBuildingContext buildingContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		for ( AnnotationInstance annotation : annotations ) {
			if ( annotation.name().equals( DOCUMENTED )
					|| annotation.name().equals( REPEATABLE )
					|| annotation.name().equals( RETENTION )
					|| annotation.name().equals( TARGET ) ) {
				continue;
			}

			final Class<? extends Annotation> annotationType = buildingContext
					.getClassLoading()
					.classForName( annotation.name().toString() );

			final AnnotationDescriptor<?> annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationType );
			final Annotation usage = makeUsage(
					annotation,
					annotationDescriptor,
					buildingContext
			);
			consumer.accept( annotationType, usage );
		}
	}

	public static <A extends Annotation> A makeUsage(
			AnnotationInstance jandexAnnotation,
			AnnotationDescriptor<A> annotationDescriptor,
			SourceModelBuildingContext modelContext) {
		final Map<String, Object> attributeValues = extractAttributeValues(
				jandexAnnotation,
				annotationDescriptor,
				modelContext
		);
		return annotationDescriptor.createUsage( attributeValues, modelContext );
	}

	/**
	* Extracts values from an annotation creating AnnotationAttributeValue references.
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <A extends Annotation> Map<String,Object> extractAttributeValues(
		AnnotationInstance annotationInstance,
		AnnotationDescriptor<A> annotationDescriptor,
		SourceModelBuildingContext modelContext) {
		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		final ConcurrentHashMap<String, Object> valueMap = new ConcurrentHashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AttributeDescriptor attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			final JandexValueExtractor<?> extractor = modelContext
					.as( JandexModelContext.class )
					.getJandexValueExtractor( attributeDescriptor.getTypeDescriptor() );
			final Object attributeValue = extractor.extractValue(
					annotationInstance,
					attributeDescriptor,
					modelContext
			);
			valueMap.put( attributeDescriptor.getName(), attributeValue );
		}
		return valueMap;
	}

	private AnnotationUsageBuilder() {
		// disallow direct instantiation
	}
}
