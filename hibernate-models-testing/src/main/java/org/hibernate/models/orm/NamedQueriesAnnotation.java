/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

import static org.hibernate.models.orm.JpaAnnotations.NAMED_QUERIES;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class NamedQueriesAnnotation implements NamedQueries, RepeatableContainer<NamedQuery> {
	private NamedQuery[] value;

	public NamedQueriesAnnotation(SourceModelBuildingContext modelContext) {
	}

	public NamedQueriesAnnotation(NamedQueries usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, NAMED_QUERIES, modelContext );
	}

	public NamedQueriesAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		value = (NamedQuery[]) attributeValues.get( "value" );
	}

	@Override
	public NamedQuery[] value() {
		return value;
	}

	@Override
	public void value(NamedQuery[] value) {
		this.value = value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return NamedQueries.class;
	}
}
