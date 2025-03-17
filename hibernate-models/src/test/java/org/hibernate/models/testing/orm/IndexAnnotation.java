/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.Index;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class IndexAnnotation implements Index {
	private String name;
	private String columnList;
	private boolean unique;
	private String options;

	public IndexAnnotation(SourceModelBuildingContext modelContext) {
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

	public IndexAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		name = (String) attributeValues.get( "name" );
		columnList = (String) attributeValues.get( "columnList" );
		unique = (boolean) attributeValues.get( "unique" );
		options = (String) attributeValues.get( "options" );
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
