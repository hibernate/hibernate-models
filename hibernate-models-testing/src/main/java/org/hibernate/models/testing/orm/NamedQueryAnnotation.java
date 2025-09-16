/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;

import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
import static org.hibernate.models.testing.orm.JpaAnnotations.NAMED_QUERY;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class NamedQueryAnnotation implements NamedQuery, Named {
	private String name;
	private String query;
	private LockModeType lockModeType;
	private Class<?> resultClass;
	private QueryHint[] hints;

	public NamedQueryAnnotation(ModelsContext modelContext) {
		lockModeType = LockModeType.NONE;
		hints = new QueryHint[0];
		resultClass = void.class;
	}

	public NamedQueryAnnotation(NamedQuery usage, ModelsContext modelContext) {
		name = usage.name();
		query = usage.query();
		hints = extractRepeatedValues( usage, NAMED_QUERY.getAttribute( "hints" ), modelContext );
		resultClass = usage.resultClass();
	}

	public NamedQueryAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
		name = (String) attributeValues.get( "name" );
		query = attributeValues.get( "query" ).toString();
		hints = (QueryHint[]) attributeValues.get( "hints" );
		resultClass = (Class<?>) attributeValues.get( "resultClass" );
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void name(String name) {
		this.name = name;
	}

	@Override
	public String query() {
		return query;
	}

	@Override
	public Class<?> resultClass() {
		return resultClass;
	}

	public void query(String query) {
		this.query = query;
	}

	@Override
	public LockModeType lockMode() {
		return lockModeType;
	}

	public void lockMode(LockModeType lockModeType) {
		this.lockModeType = lockModeType;
	}

	@Override
	public QueryHint[] hints() {
		return hints;
	}

	public void hints(QueryHint[] hints) {
		this.hints = hints;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return NamedQuery.class;
	}
}
