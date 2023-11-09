/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;

/**
 * Helper for building {@link AnnotationUsage} instances based on
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
	public static Map<Class<? extends Annotation>, AnnotationUsage<?>> collectUsages(
			org.jboss.jandex.AnnotationTarget jandexAnnotationTarget,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		if ( jandexAnnotationTarget == null ) {
			return Collections.emptyMap();
		}
		final Map<Class<? extends Annotation>, AnnotationUsage<?>> result = new HashMap<>();
		processAnnotations(
				jandexAnnotationTarget.declaredAnnotations(),
				target,
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
			AnnotationTarget target,
			BiConsumer<Class<? extends Annotation>, AnnotationUsage<?>> consumer,
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
			AnnotationInstance annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		return new JandexAnnotationUsage<>( annotation, annotationDescriptor, target, buildingContext );
	}

	/**
	 * Extracts values from an annotation creating AnnotationAttributeValue references.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <A extends Annotation> Map<String,?> extractAttributeValues(
			AnnotationInstance annotationInstance,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		if ( annotationDescriptor.getAttributes().size() == 1 ) {
			final AttributeDescriptor attributeDescriptor = annotationDescriptor.getAttributes().get( 0 );
			final ValueExtractor<AnnotationInstance, ?> extractor = attributeDescriptor
					.getTypeDescriptor()
					.createJandexExtractor( buildingContext );
			final Object attributeValue = extractor.extractValue( annotationInstance, attributeDescriptor, target, buildingContext );
			return Collections.singletonMap( attributeDescriptor.getName(), attributeValue );
		}

		final Map<String,Object> valueMap = new HashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AttributeDescriptor attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			final ValueExtractor<AnnotationInstance, ?> extractor = attributeDescriptor
					.getTypeDescriptor()
					.createJandexExtractor( buildingContext );
			final Object attributeValue = extractor.extractValue( annotationInstance, attributeDescriptor, target, buildingContext );
			valueMap.put( attributeDescriptor.getName(), attributeValue );
		}
		return valueMap;
	}

	private AnnotationUsageBuilder() {
		// disallow direct instantiation
	}
}
