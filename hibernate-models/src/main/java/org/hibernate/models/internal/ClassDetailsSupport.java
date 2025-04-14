/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.models.IllegalCastException;
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
	default void forEachField(IndexedConsumer<FieldDetails> consumer) {
		final List<FieldDetails> fields = getFields();
		if ( fields == null ) {
			return;
		}

		for ( int i = 0; i < fields.size(); i++ ) {
			consumer.accept( i, fields.get( i ) );
		}
	}

	@Override
	default void forEachMethod(IndexedConsumer<MethodDetails> consumer) {
		final List<MethodDetails> methods = getMethods();
		if ( methods == null ) {
			return;
		}

		for ( int i = 0; i < methods.size(); i++ ) {
			consumer.accept( i, methods.get( i ) );
		}
	}

	@Override
	default void forEachRecordComponent(IndexedConsumer<RecordComponentDetails> consumer) {
		final List<RecordComponentDetails> recordComponents = getRecordComponents();
		if ( recordComponents == null ) {
			return;
		}

		for ( int i = 0; i < recordComponents.size(); i++ ) {
			consumer.accept( i, recordComponents.get( i ) );
		}
	}

	@Override
	default <A extends Annotation> A getAnnotationUsage(
			AnnotationDescriptor<A> descriptor,
			ModelsContext modelContext) {
		final A localUsage = AnnotationUsageHelper.getUsage( descriptor, getUsageMap(),modelContext );
		if ( localUsage != null ) {
			return localUsage;
		}

		if ( descriptor.isInherited() && getSuperClass() != null ) {
			return getSuperClass().getAnnotationUsage( descriptor, modelContext );
		}

		return null;
	}

	@Override
	default <A extends Annotation> A getAnnotationUsage(Class<A> annotationType, ModelsContext modelContext) {
		return getAnnotationUsage(
				modelContext.getAnnotationDescriptorRegistry().getDescriptor( annotationType ),
				modelContext
		);
	}

	@Override
	default MutableClassDetails asClassDetails() {
		return this;
	}

	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "ClassDetails cannot be cast as AnnotationDescriptor" );
	}

	@Override
	default MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as MemberDetails" );
	}

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast as RecordComponentDetails" );
	}
}
