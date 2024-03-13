/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
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
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.MutableAnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public interface AnnotationTargetSupport extends MutableAnnotationTarget {
	SourceModelBuildingContext getBuildingContext();
	Map<Class<? extends Annotation>, AnnotationUsage<? extends Annotation>> getUsageMap();

	@Override
	default Collection<AnnotationUsage<?>> getAllAnnotationUsages() {
		return getUsageMap().values();
	}

	@Override
	default <A extends Annotation> boolean hasAnnotationUsage(Class<A> type) {
		return getUsageMap().containsKey( type );
	}

	@Override
	default <A extends Annotation> boolean hasRepeatableAnnotationUsage(Class<A> type) {
		final boolean containsDirectly = getUsageMap().containsKey( type );
		if ( containsDirectly ) {
			return true;
		}

		final AnnotationDescriptorRegistry descriptorRegistry = getBuildingContext().getAnnotationDescriptorRegistry();
		final AnnotationDescriptor<A> descriptor = descriptorRegistry.getDescriptor( type );
		if ( descriptor.isRepeatable() ) {
			// e.g. caller asks about NamedQuery... let's also check for NamedQueries (which implies NamedQuery)
			return getUsageMap().containsKey( descriptor.getRepeatableContainer().getAnnotationType() );
		}

		return false;
	}

	@Override
	default <A extends Annotation> AnnotationUsage<A> getAnnotationUsage(AnnotationDescriptor<A> descriptor) {
		return AnnotationUsageHelper.getUsage( descriptor, getUsageMap() );
	}

	@Override
	default <A extends Annotation> AnnotationUsage<A> getAnnotationUsage(Class<A> annotationType) {
		return getAnnotationUsage( getBuildingContext().getAnnotationDescriptorRegistry().getDescriptor( annotationType ) );
	}

	@Override
	default <A extends Annotation> AnnotationUsage<A> locateAnnotationUsage(Class<A> annotationType) {
		// e.g., locate `@Nationalized`

		// first, check for direct use
		// 		- look for `Nationalized.class` in the usage map of the target
		final AnnotationUsage<A> localUsage = getAnnotationUsage( annotationType );
		if ( localUsage != null ) {
			return localUsage;
		}

		// next, check as a "meta annotation"
		// 		- for each local usage, check that annotation's annotations for `Nationalized.class` (one level deep)
		final Map<Class<? extends Annotation>, AnnotationUsage<? extends Annotation>> localUsageMap = getUsageMap();
		for ( Map.Entry<Class<? extends Annotation>, AnnotationUsage<? extends Annotation>> usageEntry : localUsageMap.entrySet() ) {
			final AnnotationUsage<? extends Annotation> usage = usageEntry.getValue();
			if ( annotationType.equals( usage.getAnnotationType() ) ) {
				// we would have found this on the direct search, so no need
				// to check its meta-annotations
				continue;
			}

			final AnnotationUsage<A> metaAnnotation = usage.getAnnotationDescriptor().getAnnotationUsage( annotationType );
			if ( metaAnnotation != null ) {
				return metaAnnotation;
			}

			// we only check one level deep
		}

		return null;
	}

	@Override
	default <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getRepeatedUsages( type, getUsageMap() );
	}

	@Override
	default <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(Class<A> type) {
		return getRepeatedAnnotationUsages( getBuildingContext().getAnnotationDescriptorRegistry().getDescriptor( type ) );
	}

	@Override
	default <X extends Annotation> void forEachAnnotationUsage(Class<X> type, Consumer<AnnotationUsage<X>> consumer) {
		forEachAnnotationUsage(
				getBuildingContext().getAnnotationDescriptorRegistry().getDescriptor( type ),
				consumer
		);
	}

	@Override
	default <A extends Annotation> List<AnnotationUsage<? extends Annotation>> getMetaAnnotated(Class<A> metaAnnotationType) {
		final List<AnnotationUsage<?>> usages = new ArrayList<>();
		forAllAnnotationUsages( (usage) -> {
			final Annotation metaUsage = usage.getAnnotationType().getAnnotation( metaAnnotationType );
			if ( metaUsage != null ) {
				usages.add( usage );
			}
		} );
		return usages;
	}

	@Override
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(Class<X> type, String matchName) {
		return getNamedAnnotationUsage( getBuildingContext().getAnnotationDescriptorRegistry().getDescriptor( type ), matchName );
	}

	@Override
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch) {
		return AnnotationUsageHelper.getNamedUsage( type, matchName, attributeToMatch, getUsageMap() );
	}

	@Override
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch) {
		return getNamedAnnotationUsage(
				getBuildingContext().getAnnotationDescriptorRegistry().getDescriptor( type ),
				matchName,
				attributeToMatch
		);
	}
}
