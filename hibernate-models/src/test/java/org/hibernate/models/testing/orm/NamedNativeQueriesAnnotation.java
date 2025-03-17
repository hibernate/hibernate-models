/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;

import static org.hibernate.models.testing.orm.JpaAnnotations.NAMED_NATIVE_QUERIES;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class NamedNativeQueriesAnnotation implements NamedNativeQueries, RepeatableContainer<NamedNativeQuery> {
	private NamedNativeQuery[] value;

	public NamedNativeQueriesAnnotation(SourceModelBuildingContext modelContext) {
		value = new NamedNativeQuery[0];
	}

	public NamedNativeQueriesAnnotation(NamedNativeQueries usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, NAMED_NATIVE_QUERIES, modelContext );
	}

	public NamedNativeQueriesAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		value = (NamedNativeQuery[]) attributeValues.get( "value" );
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
