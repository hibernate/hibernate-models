/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.JandexValueHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

import jakarta.persistence.Index;

import static org.hibernate.models.orm.JpaAnnotations.INDEX;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class IndexAnnotation implements Index {
	private String name;
	private String columnList;
	private boolean unique;
	private String options;

	public IndexAnnotation() {
		name = "";
		unique = false;
		options = "";
	}

	public IndexAnnotation(Index usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		columnList = usage.columnList();
		unique = usage.unique();
		options = usage.options();
	}

	public IndexAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		name = JandexValueHelper.extractValue( usage, INDEX.getAttribute( "name" ), modelContext );
		columnList = JandexValueHelper.extractValue( usage, INDEX.getAttribute( "columnList" ), modelContext );
		unique = JandexValueHelper.extractValue( usage, INDEX.getAttribute( "unique" ), modelContext );
		options = JandexValueHelper.extractValue( usage, INDEX.getAttribute( "options" ), modelContext );
	}

	@Override
	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	@Override
	public String columnList() {
		return columnList;
	}

	public void columnList(String columnList) {
		this.columnList = columnList;
	}

	@Override
	public boolean unique() {
		return unique;
	}

	public void unique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public String options() {
		return options;
	}

	public void options(String options) {
		this.options = options;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Index.class;
	}
}
