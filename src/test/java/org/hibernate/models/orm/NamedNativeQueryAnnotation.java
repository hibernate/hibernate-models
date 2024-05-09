/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EntityResult;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.QueryHint;

import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
import static org.hibernate.models.internal.jandex.JandexValueHelper.extractOptionalValue;
import static org.hibernate.models.internal.jandex.JandexValueHelper.extractValue;
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

	public NamedNativeQueryAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		name = usage.value( "name" ).asString();
		query = usage.value( "query" ).asString();

		resultClass = extractValue( usage, NAMED_NATIVE_QUERY.getAttribute( "resultClass" ), modelContext );
		resultSetMapping = extractOptionalValue( usage, NAMED_NATIVE_QUERY.getAttribute( "resultSetMapping" ), modelContext );
		entityResults = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "entities" ), modelContext );
		constructorResults = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "classes" ), modelContext );
		columnResults = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "columns" ), modelContext );

		hints = extractRepeatedValues( usage, NAMED_NATIVE_QUERY.getAttribute( "hints" ), modelContext );
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
