/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @see AnnotationHelper
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageHelper {
	public static <A extends Annotation> A findUsage(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,? extends Annotation> usageMap) {
		//noinspection unchecked
		return (A) usageMap.get( type.getAnnotationType() );
	}

	/**
	 * Get the annotation usage from the {@code usageMap} for the given {@code type}
	 */
	public static <A extends Annotation> A getUsage(
			Class<A> type,
			Map<Class<? extends Annotation>,? extends Annotation> usageMap,
			SourceModelBuildingContext modelContext) {
		return getUsage(
				modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				usageMap,
				modelContext
		);
	}

	public static <A extends Annotation, C extends Annotation> A[] extractRepeatedValues(
			C container,
			AnnotationDescriptor<C> containerDescriptor,
			SourceModelBuildingContext modelContext) {
		return extractRepeatedValues( container, containerDescriptor.getAttribute( "value" ), modelContext );
	}

	public static <A extends Annotation, C extends Annotation> A[] extractRepeatedValues(
			C container,
			AttributeDescriptor<A[]> valuesAttribute,
			SourceModelBuildingContext modelContext) {
		return valuesAttribute.getTypeDescriptor().createJdkValueExtractor( modelContext ).extractValue( container, valuesAttribute, modelContext );
	}

	/**
	 * Get the annotation usage from the {@code usageMap} for the given {@code type}
	 */
	public static <A extends Annotation, C extends Annotation> A getUsage(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,? extends Annotation> usageMap,
			SourceModelBuildingContext modelContext) {
		final A found = findUsage( type, usageMap );
		if ( found == null ) {
			//noinspection unchecked
			final AnnotationDescriptor<C> repeatableContainer = (AnnotationDescriptor<C>) type.getRepeatableContainer();
			if ( repeatableContainer != null ) {
				final C containerUsage = findUsage( repeatableContainer, usageMap );
				if ( containerUsage != null ) {
					final A[] repeatedValues = extractRepeatedValues( containerUsage, repeatableContainer, modelContext );
					if ( CollectionHelper.isEmpty( repeatedValues ) ) {
						return null;
					}
					if ( repeatedValues.length > 1 ) {
						throw new AnnotationAccessException( "Found more than one usage of " + type.getAnnotationType().getName() );
					}
					return repeatedValues[0];
				}
			}
		}
		return found;
	}

	public static <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			Class<A> repeatableType,
			Class<C> containerType,
			Consumer<A> consumer,
			Map<Class<? extends Annotation>, ? extends Annotation> usageMap,
			SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		final A repeatable = (A) usageMap.get( repeatableType );
		if ( repeatable != null ) {
			consumer.accept( repeatable );
		}

		//noinspection unchecked
		final C container = (C) usageMap.get( containerType );
		if ( container != null ) {
			final AnnotationDescriptor<C> containerDescriptor = modelContext.getAnnotationDescriptorRegistry().getDescriptor( containerType );
			final AttributeDescriptor<A[]> attribute = containerDescriptor.getAttribute( "value" );
			final A[] repetitions = AnnotationHelper.extractValue( container, attribute );
			CollectionHelper.forEach( repetitions, consumer );
		}
	}

	public static <A extends Annotation> void forEachRepeatedAnnotationUsages(
			AnnotationDescriptor<A> repeatableDescriptor,
			Consumer<A> consumer,
			Map<Class<? extends Annotation>, ? extends Annotation> usageMap,
			SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		final A repeatable = (A) usageMap.get( repeatableDescriptor.getAnnotationType() );
		if ( repeatable != null ) {
			consumer.accept( repeatable );
		}

		final Class<? extends Annotation> containerType = repeatableDescriptor.getRepeatableContainer().getAnnotationType();
		final Annotation container = usageMap.get( containerType );
		if ( container != null ) {
			final AnnotationDescriptor<?> containerDescriptor = modelContext.getAnnotationDescriptorRegistry().getDescriptor( containerType );
			final AttributeDescriptor<A[]> attribute = containerDescriptor.getAttribute( "value" );
			final A[] repetitions = AnnotationHelper.extractValue( container, attribute );
			CollectionHelper.forEach( repetitions, consumer );
		}
	}

	public static <A extends Annotation> A[] getRepeatedUsages(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>, ? extends Annotation> usageMap,
			SourceModelBuildingContext modelContext) {
		// e.g. `@NamedQuery`
		final A usage = findUsage( type, usageMap );
		// e.g. `@NamedQueries`
		final Annotation containerUsage = type.getRepeatableContainer() != null
				? findUsage( type.getRepeatableContainer(), usageMap )
				: null;

		if ( containerUsage != null ) {
			final AttributeDescriptor<A[]> attribute = type.getRepeatableContainer().getAttribute( "value" );
			final A[] repeatableValues = AnnotationHelper.extractValue( containerUsage, attribute );

			if ( CollectionHelper.isNotEmpty( repeatableValues ) ) {
				if ( usage != null ) {
					//noinspection unchecked
					final A[] combined = (A[]) Array.newInstance( type.getAnnotationType(), repeatableValues.length + 1 );
					// prepend the singular usage
					combined[0] = usage;
					System.arraycopy( repeatableValues, 0, combined, 1, repeatableValues.length );
					return combined;
				}
				return repeatableValues;
			}
		}

		if ( usage != null ) {
			//noinspection unchecked
			final A[] singleton = (A[]) Array.newInstance( type.getAnnotationType(), 1 );
			singleton[0] = usage;
			return singleton;
		}

		//noinspection unchecked
		return (A[]) Array.newInstance( type.getAnnotationType(), 0 );
	}

	public static <A extends Annotation, C extends Annotation> A getNamedUsage(
			AnnotationDescriptor<A> descriptor,
			String matchValue,
			String attributeToMatch,
			Map<Class<? extends Annotation>, ?> usageMap,
			SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		final A annotationUsage = (A) usageMap.get( descriptor.getAnnotationType() );
		if ( annotationUsage != null ) {
			if ( nameMatches( annotationUsage, descriptor, matchValue, attributeToMatch, modelContext ) ) {
				return annotationUsage;
			}
			return null;
		}

		//noinspection unchecked
		final AnnotationDescriptor<C> containerType = (AnnotationDescriptor<C>) descriptor.getRepeatableContainer();
		if ( containerType != null ) {
			//noinspection unchecked
			final C containerUsage = (C) usageMap.get( containerType.getAnnotationType() );
			if ( containerUsage != null ) {
				final A[] repeatedUsages = extractRepeatedValues( containerUsage, containerType, modelContext );
				for ( int i = 0; i < repeatedUsages.length; i++ ) {
					final A repeatedUsage = repeatedUsages[i];
					if ( nameMatches( repeatedUsage, descriptor, matchValue, attributeToMatch, modelContext ) ) {
						return repeatedUsage;
					}
				}
			}
		}

		return null;
	}

	private static <A extends Annotation> boolean nameMatches(
			A annotationUsage,
			AnnotationDescriptor<A> descriptor,
			String matchValue,
			String attributeToMatch,
			SourceModelBuildingContext modelContext) {
		final AttributeDescriptor<String> attributeDescriptor = descriptor.getAttribute( attributeToMatch );
		final String usageName = AnnotationHelper.extractValue( annotationUsage, attributeDescriptor );
		return matchValue.equals( usageName );
	}


	private AnnotationUsageHelper() {
	}
}
