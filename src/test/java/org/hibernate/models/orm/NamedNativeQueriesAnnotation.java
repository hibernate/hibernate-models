/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;

import static org.hibernate.models.orm.JpaAnnotations.NAMED_NATIVE_QUERIES;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class NamedNativeQueriesAnnotation implements NamedNativeQueries, RepeatableContainer<NamedNativeQuery> {
	private NamedNativeQuery[] value;

	public NamedNativeQueriesAnnotation() {
		value = new NamedNativeQuery[0];
	}

	public NamedNativeQueriesAnnotation(NamedNativeQueries usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, NAMED_NATIVE_QUERIES, modelContext );
	}

	public NamedNativeQueriesAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, NAMED_NATIVE_QUERIES, modelContext );
	}

	@Override
	public NamedNativeQuery[] value() {
		return value;
	}

	@Override
	public void value(NamedNativeQuery[] value) {
		this.value = value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return NamedNativeQueries.class;
	}
}
