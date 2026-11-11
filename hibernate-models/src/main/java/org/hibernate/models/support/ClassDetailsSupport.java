/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.support;

import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;

/**
 * @author Steve Ebersole
 */
public interface ClassDetailsSupport extends MutableClassDetails, AnnotationTargetSupport {

	@Override
	default void forEachField(@Nonnull IndexedConsumer<FieldDetails> consumer) {
		final List<FieldDetails> fields = getFields();
		if ( fields == null ) {
			return;
		}

		for ( int i = 0; i < fields.size(); i++ ) {
			consumer.accept( i, fields.get( i ) );
		}
	}

	@Override
	default void forEachMethod(@Nonnull IndexedConsumer<MethodDetails> consumer) {
		final List<MethodDetails> methods = getMethods();
		if ( methods == null ) {
			return;
		}

		for ( int i = 0; i < methods.size(); i++ ) {
			consumer.accept( i, methods.get( i ) );
		}
	}

	@Override
	default void forEachRecordComponent(@Nonnull IndexedConsumer<RecordComponentDetails> consumer) {
		final List<RecordComponentDetails> recordComponents = getRecordComponents();
		if ( recordComponents == null ) {
			return;
		}

		for ( int i = 0; i < recordComponents.size(); i++ ) {
			consumer.accept( i, recordComponents.get( i ) );
		}
	}

	@Nullable
	@Override
	default <A extends Annotation> A getAnnotationUsage(
			@Nonnull AnnotationDescriptor<A> descriptor,
			@Nonnull ModelsContext modelContext) {
		final A localUsage = AnnotationUsageHelper.getUsage( descriptor, getUsageMap(), modelContext );
		if ( localUsage != null ) {
			return localUsage;
		}

		if ( descriptor.isInherited() && getSuperClass() != null ) {
			return getSuperClass().getAnnotationUsage( descriptor, modelContext );
		}

		return null;
	}

	@Nullable
	@Override
	default <A extends Annotation> A getAnnotationUsage(@Nonnull Class<A> annotationType, @Nonnull ModelsContext modelContext) {
		return getAnnotationUsage(
				modelContext.getAnnotationDescriptorRegistry().getDescriptor( annotationType ),
				modelContext
		);
	}

	@Nonnull
	@Override
	default MutableClassDetails asClassDetails() {
		return this;
	}

	@Nonnull
	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "ClassDetails cannot be cast as AnnotationDescriptor" );
	}

	@Nonnull
	@Override
	default MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as MemberDetails" );
	}

	@Nonnull
	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as FieldDetails" );
	}

	@Nonnull
	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as MethodDetails" );
	}

	@Nonnull
	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as RecordComponentDetails" );
	}
}
