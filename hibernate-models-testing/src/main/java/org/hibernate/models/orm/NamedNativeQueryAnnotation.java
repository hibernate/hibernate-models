/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EntityResult;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.QueryHint;

import static org.hibernate.models.internal.AnnotationHelper.extractValue;
import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
import static org.hibernate.models.orm.JpaAnnotations.NAMED_NATIVE_QUERY;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class NamedNativeQueryAnnotation implements NamedNativeQuery, Named {
	private String name;
	private String query;

	private Class<?> resultClass;
	private String resultSetMapping;
	private EntityResult[] entityResults;
	private ConstructorResult[] constructorResults;
	private ColumnResult[] columnResults;

	private QueryHint[] hints;

	public NamedNativeQueryAnnotation(SourceModelBuildingContext modelContext) {
		resultClass = void.class;
		resultSetMapping = "";
		entityResults = new EntityResult[0];
		constructorResults = new ConstructorResult[0];
		columnResults = new ColumnResult[0];
		hints = new QueryHint[0];
	}

	public NamedNativeQueryAnnotation(NamedNativeQuery usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		query = usage.query();

		resultClass = usage.resultClass();
		resultSetMapping = usage.resultSetMapping();
		entityResults = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "entities" ), modelContext );
		constructorResults = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "classes" ), modelContext );
		columnResults = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "columns" ), modelContext );

		hints = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "hints" ), modelContext );
	}

	public NamedNativeQueryAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		name = (String) attributeValues.get( "name" );
		query = (String) attributeValues.get( "query" );

		resultClass = (Class<?>) attributeValues.get( "resultClass" );
		resultSetMapping = (String) attributeValues.get( "resultSetMapping" );
		entityResults = (EntityResult[]) attributeValues.get( "entities" );
		constructorResults = (ConstructorResult[]) attributeValues.get( "classes" );
		columnResults = (ColumnResult[]) attributeValues.get( "columns" );

		hints = (QueryHint[]) attributeValues.get( "hints" );
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

	public void query(String query) {
		this.query = query;
	}

	@Override
	public QueryHint[] hints() {
		return hints;
	}

	public void hints(QueryHint[] hints) {
		this.hints = hints;
	}

	@Override
	public Class<?> resultClass() {
		return resultClass;
	}

	public void resultClass(Class<?> resultClass) {
		this.resultClass = resultClass;
	}

	@Override
	public String resultSetMapping() {
		return resultSetMapping;
	}

	public void resultSetMapping(String resultSetMapping) {
		this.resultSetMapping = resultSetMapping;
	}

	@Override
	public EntityResult[] entities() {
		return entityResults;
	}

	public void entities(EntityResult[] entityResults) {
		this.entityResults = entityResults;
	}

	@Override
	public ConstructorResult[] classes() {
		return constructorResults;
	}

	public void classes(ConstructorResult[] constructorResults) {
		this.constructorResults = constructorResults;
	}

	@Override
	public ColumnResult[] columns() {
		return columnResults;
	}

	public void columns(ColumnResult[] columnResults) {
		this.columnResults = columnResults;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return NamedNativeQuery.class;
	}
}
