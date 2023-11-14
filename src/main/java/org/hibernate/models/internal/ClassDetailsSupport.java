/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;

/**
 * @author Steve Ebersole
 */
public interface ClassDetailsSupport extends MutableClassDetails, AnnotationTargetSupport, MutableAnnotationTarget {

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
	default <A extends Annotation> AnnotationUsage<A> getAnnotationUsage(AnnotationDescriptor<A> type) {
		final AnnotationUsage<A> localUsage = AnnotationUsageHelper.getUsage( type, getUsageMap() );
		if ( localUsage != null ) {
			return localUsage;
		}

		if ( type.isInherited() && getSuperType() != null ) {
			return getSuperType().getAnnotationUsage( type );
		}

		return null;
	}

	@Override
	default  <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(AnnotationDescriptor<A> type) {
		final List<AnnotationUsage<A>> localUsages = AnnotationTargetSupport.super.getRepeatedAnnotationUsages( type );

		if ( type.isInherited() && getSuperType() != null ) {
			final List<AnnotationUsage<A>> inheritedUsages = getSuperType().getRepeatedAnnotationUsages( type );
			return CollectionHelper.join( localUsages, inheritedUsages );
		}

		return localUsages;
	}

	@Override
	default <A extends Annotation> AnnotationUsage<A> getNamedAnnotationUsage(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch) {
		final AnnotationUsage<A> localUsage = AnnotationTargetSupport.super.getNamedAnnotationUsage( type, matchValue, attributeToMatch );
		if ( localUsage != null ) {
			return localUsage;
		}

		if ( type.isInherited() && getSuperType() != null ) {
			return getSuperType().getNamedAnnotationUsage( type, matchValue, attributeToMatch );
		}
		return null;
	}
}
