/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationTarget;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Steve Ebersole
 */
public interface AnnotationTargetSupport extends MutableAnnotationTarget {
	/**
	 * Access to all annotation usages on this target.
	 */
	@Nonnull
	Map<Class<? extends Annotation>,? extends Annotation> getUsageMap();

	@Override
	default void clearAnnotationUsages() {
		getUsageMap().clear();
	}

	@Override
	default <X extends Annotation> void addAnnotationUsage(@Nonnull X annotationUsage) {
		//noinspection unchecked,rawtypes
		( (Map) getUsageMap() ).put( annotationUsage.annotationType(), annotationUsage );
	}

	@Override
	default <X extends Annotation> void removeAnnotationUsage(@Nonnull AnnotationDescriptor<X> annotationType) {
		getUsageMap().remove( annotationType.getAnnotationType() );
	}

	@Nonnull
	@Override
	default Collection<? extends Annotation> getDirectAnnotationUsages() {
		return getUsageMap().values();
	}

	@Nullable
	@Override
	default <A extends Annotation> A getDirectAnnotationUsage(@Nonnull AnnotationDescriptor<A> descriptor) {
		return getDirectAnnotationUsage( descriptor.getAnnotationType() );
	}

	@Nullable
	@Override
	default <A extends Annotation> A getDirectAnnotationUsage(@Nonnull Class<A> type) {
		//noinspection unchecked
		return (A) getUsageMap().get( type );
	}

	@Override
	default <A extends Annotation> boolean hasDirectAnnotationUsage(@Nonnull Class<A> type) {
		return getUsageMap().containsKey( type );
	}

	@Override
	default <X extends Annotation> boolean hasAnnotationUsage(@Nonnull Class<X> type, @Nonnull ModelsContext modelContext) {
		final boolean containsDirectly = getUsageMap().containsKey( type );
		if ( containsDirectly ) {
			return true;
		}

		final AnnotationDescriptor<X> descriptor = modelContext.getAnnotationDescriptorRegistry().getDescriptor( type );
		if ( descriptor.isRepeatable() ) {
			// e.g. caller asks about NamedQuery... let's also check for NamedQueries (which implies NamedQuery)
			return getUsageMap().containsKey( descriptor.getRepeatableContainer().getAnnotationType() );
		}

		return false;
	}

	@Nullable
	@Override
	default <A extends Annotation> A getAnnotationUsage(@Nonnull AnnotationDescriptor<A> descriptor, @Nonnull ModelsContext modelContext) {
		return AnnotationUsageHelper.getUsage( descriptor, getUsageMap(), modelContext );
	}

	@Nullable
	@Override
	default <A extends Annotation> A getAnnotationUsage(@Nonnull Class<A> annotationType, @Nonnull ModelsContext modelContext) {
		return AnnotationUsageHelper.getUsage( annotationType, getUsageMap(), modelContext );
	}

	@Nonnull
	@Override
	default <A extends Annotation> A[] getRepeatedAnnotationUsages(
			@Nonnull AnnotationDescriptor<A> type,
			@Nonnull ModelsContext modelContext) {
		return AnnotationUsageHelper.getRepeatedUsages( type, getUsageMap(), modelContext );
	}

	@Override
	default <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			@Nonnull Class<A> repeatableType,
			@Nonnull Class<C> containerType,
			@Nonnull ModelsContext modelContext, @Nonnull Consumer<A> consumer) {
		AnnotationUsageHelper.forEachRepeatedAnnotationUsages( repeatableType, containerType, consumer, getUsageMap(), modelContext );
	}

	@SuppressWarnings("unused")
	@Override
	default <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			@Nonnull AnnotationDescriptor<A> repeatableDescriptor,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<A> consumer) {
		AnnotationUsageHelper.forEachRepeatedAnnotationUsages( repeatableDescriptor, consumer, getUsageMap(), modelContext );
	}

	@Nullable
	@Override
	default <A extends Annotation> A locateAnnotationUsage(@Nonnull Class<A> annotationType, @Nonnull ModelsContext modelContext) {
		// e.g., locate `@Nationalized`

		// first, check for direct use
		// 		- look for `Nationalized.class` in the usage map of the target
		final A localUsage = getAnnotationUsage( annotationType, modelContext );
		if ( localUsage != null ) {
			return localUsage;
		}

		// next, check as a "meta annotation"
		// 		- for each local usage, check that annotation's annotations for `Nationalized.class` (one level deep)
		final Map<Class<? extends Annotation>, ? extends Annotation> localUsageMap = getUsageMap();
		for ( Map.Entry<Class<? extends Annotation>, ? extends Annotation> usageEntry : localUsageMap.entrySet() ) {
			final Annotation usage = usageEntry.getValue();
			if ( annotationType.equals( usage.annotationType() ) ) {
				// we would have found this on the direct search, so no need
				// to check its meta-annotations
				continue;
			}

			final AnnotationDescriptor<? extends Annotation> usageDescriptor = modelContext.getAnnotationDescriptorRegistry().getDescriptor( usage.annotationType() );
			final A metaAnnotation = usageDescriptor.getDirectAnnotationUsage( annotationType );
			if ( metaAnnotation != null ) {
				return metaAnnotation;
			}

			// we only check one level deep
		}

		return null;
	}

	@Nonnull
	@Override
	default <A extends Annotation> List<? extends Annotation> getMetaAnnotated(
			@Nonnull Class<A> metaAnnotationType,
			@Nonnull ModelsContext modelContext) {
		final List<Annotation> usages = new ArrayList<>();
		forEachDirectAnnotationUsage( (usage) -> {
			if (usage.annotationType().getName().startsWith("jakarta.persistence.")) {
				return;
			}

			final Annotation metaUsage = usage.annotationType().getAnnotation( metaAnnotationType );
			if ( metaUsage != null ) {
				usages.add( usage );
			}
		} );
		return usages;
	}

	@Nullable
	@Override
	default <X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull Class<X> type,
			@Nonnull String matchName,
			@Nonnull ModelsContext modelContext) {
		return getNamedAnnotationUsage( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), matchName, modelContext );
	}

	@Nullable
	@Override
	default <X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull AnnotationDescriptor<X> type,
			@Nonnull String matchName,
			@Nonnull String attributeToMatch,
			@Nonnull ModelsContext modelContext) {
		return AnnotationUsageHelper.getNamedUsage( type, matchName, attributeToMatch, getUsageMap(), modelContext );
	}

	@Nullable
	@Override
	default <X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull Class<X> type,
			@Nonnull String matchName,
			@Nonnull String attributeToMatch,
			@Nonnull ModelsContext modelContext) {
		return getNamedAnnotationUsage(
				modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				matchName,
				attributeToMatch,
				modelContext
		);
	}

	@Nonnull
	@Override
	default <S extends Annotation, P extends Annotation> P replaceAnnotationUsage(
			@Nonnull AnnotationDescriptor<S> repeatableType,
			@Nonnull AnnotationDescriptor<P> containerType,
			@Nonnull ModelsContext modelContext) {
		assert repeatableType.isRepeatable();
		assert repeatableType.getRepeatableContainer() == containerType;

		final P containerTypeUsage = containerType.createUsage( modelContext );
		// effectively overwrites any previous registrations
		//noinspection unchecked,rawtypes
		( (Map) getUsageMap() ).put( containerType.getAnnotationType(), containerTypeUsage );
		// remove any entry for the repeatable/singular form
		getUsageMap().remove( repeatableType.getAnnotationType() );

		return containerTypeUsage;
	}
}
