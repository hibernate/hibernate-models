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
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationTarget;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;

/**
 * @author Steve Ebersole
 */
public interface AnnotationTargetSupport extends MutableAnnotationTarget {
	/**
	 * Access to all annotation usages on this target.
	 */
	Map<Class<? extends Annotation>,? extends Annotation> getUsageMap();

	@Override
	default void clearAnnotationUsages() {
		getUsageMap().clear();
	}

	@Override
	default <X extends Annotation> void addAnnotationUsage(X annotationUsage) {
		//noinspection unchecked,rawtypes
		( (Map) getUsageMap() ).put( annotationUsage.annotationType(), annotationUsage );
	}

	@Override
	default <X extends Annotation> void removeAnnotationUsage(AnnotationDescriptor<X> annotationType) {
		getUsageMap().remove( annotationType.getAnnotationType() );
	}

	@Override
	default Collection<? extends Annotation> getDirectAnnotationUsages() {
		return getUsageMap().values();
	}

	@Override
	default <A extends Annotation> A getDirectAnnotationUsage(AnnotationDescriptor<A> descriptor) {
		return getDirectAnnotationUsage( descriptor.getAnnotationType() );
	}

	@Override
	default <A extends Annotation> A getDirectAnnotationUsage(Class<A> type) {
		//noinspection unchecked
		return (A) getUsageMap().get( type );
	}

	@Override
	default <A extends Annotation> boolean hasDirectAnnotationUsage(Class<A> type) {
		return getUsageMap().containsKey( type );
	}

	@Override
	default <X extends Annotation> boolean hasAnnotationUsage(Class<X> type, ModelsContext modelContext) {
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

	@Override
	default <A extends Annotation> A getAnnotationUsage(AnnotationDescriptor<A> descriptor, ModelsContext modelContext) {
		return AnnotationUsageHelper.getUsage( descriptor, getUsageMap(), modelContext );
	}

	@Override
	default <A extends Annotation> A getAnnotationUsage(Class<A> annotationType, ModelsContext modelContext) {
		return AnnotationUsageHelper.getUsage( annotationType, getUsageMap(), modelContext );
	}

	@Override
	default <A extends Annotation> A[] getRepeatedAnnotationUsages(
			AnnotationDescriptor<A> type,
			ModelsContext modelContext) {
		return AnnotationUsageHelper.getRepeatedUsages( type, getUsageMap(), modelContext );
	}

	@Override
	default <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			Class<A> repeatableType,
			Class<C> containerType,
			ModelsContext modelContext, Consumer<A> consumer) {
		AnnotationUsageHelper.forEachRepeatedAnnotationUsages( repeatableType, containerType, consumer, getUsageMap(), modelContext );
	}

	@SuppressWarnings("unused")
	@Override
	default <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			AnnotationDescriptor<A> repeatableDescriptor,
			ModelsContext modelContext,
			Consumer<A> consumer) {
		AnnotationUsageHelper.forEachRepeatedAnnotationUsages( repeatableDescriptor, consumer, getUsageMap(), modelContext );
	}

	@Override
	default <A extends Annotation> A locateAnnotationUsage(Class<A> annotationType, ModelsContext modelContext) {
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

	@Override
	default <A extends Annotation> List<? extends Annotation> getMetaAnnotated(
			Class<A> metaAnnotationType,
			ModelsContext modelContext) {
		final List<Annotation> usages = new ArrayList<>();
		forEachDirectAnnotationUsage( (usage) -> {
			final Annotation metaUsage = usage.annotationType().getAnnotation( metaAnnotationType );
			if ( metaUsage != null ) {
				usages.add( usage );
			}
		} );
		return usages;
	}

	@Override
	default <X extends Annotation> X getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			ModelsContext modelContext) {
		return getNamedAnnotationUsage( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), matchName, modelContext );
	}

	@Override
	default <X extends Annotation> X getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch,
			ModelsContext modelContext) {
		return AnnotationUsageHelper.getNamedUsage( type, matchName, attributeToMatch, getUsageMap(), modelContext );
	}

	@Override
	default <X extends Annotation> X getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch,
			ModelsContext modelContext) {
		return getNamedAnnotationUsage(
				modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				matchName,
				attributeToMatch,
				modelContext
		);
	}

	@Override
	default <S extends Annotation, P extends Annotation> P replaceAnnotationUsage(
			AnnotationDescriptor<S> repeatableType,
			AnnotationDescriptor<P> containerType,
			ModelsContext modelContext) {
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

	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		return null;
	}

	@Override
	FieldDetails asFieldDetails();

	@Override
	MethodDetails asMethodDetails();

	@Override
	RecordComponentDetails asRecordComponentDetails();

	@Override
	MutableClassDetails asClassDetails();

	@Override
	MutableMemberDetails asMemberDetails();
}
