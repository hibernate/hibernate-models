/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;

/**
 * @author Steve Ebersole
 */
public class CollectionTableAnnotation implements CollectionTable, CommonTableDetails {
	private String name;
	private String catalog;
	private String schema;
	private String options;
	private JoinColumn[] joinColumns;
	private ForeignKey foreignKey;
	private UniqueConstraint[] uniqueConstraints;
	private Index[] indexes;

	public CollectionTableAnnotation(SourceModelBuildingContext modelContext) {
		name = "";
		catalog = "";
		schema = "";
		options = "";
		joinColumns = new JoinColumn[0];
		foreignKey = new ForeignKeyAnnotation( modelContext );
		uniqueConstraints = new UniqueConstraint[0];
		indexes = new Index[0];
	}

	public CollectionTableAnnotation(CollectionTable usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		catalog = usage.catalog();
		schema = usage.schema();
		options = usage.options();
		joinColumns = usage.joinColumns();
		foreignKey = usage.foreignKey();
		uniqueConstraints = usage.uniqueConstraints();
		indexes = usage.indexes();
	}

	public CollectionTableAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		name = (String) attributeValues.get( "name" );
		catalog = (String) attributeValues.get( "catalog" );
		schema = (String) attributeValues.get( "schema" );
		options = (String) attributeValues.get( "options" );
		joinColumns = (JoinColumn[]) attributeValues.get( "joinColumns" );
		foreignKey = (ForeignKey) attributeValues.get( "foreignKey" );
		uniqueConstraints = (UniqueConstraint[]) attributeValues.get( "uniqueConstraints" );
		indexes = (Index[]) attributeValues.get( "indexes" );
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
	public String catalog() {
		return catalog;
	}

	@Override
	public void catalog(String catalog) {
		this.catalog = catalog;
	}

	@Override
	public String schema() {
		return schema;
	}

	@Override
	public void schema(String schema) {
		this.schema = schema;
	}

	@Override
	public JoinColumn[] joinColumns() {
		return joinColumns;
	}

	public void joinColumns(JoinColumn[] joinColumns) {
		this.joinColumns = joinColumns;
	}

	@Override
	public ForeignKey foreignKey() {
		return foreignKey;
	}

	public void foreignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
	}

	@Override
	public UniqueConstraint[] uniqueConstraints() {
		return uniqueConstraints;
	}

	@Override
	public void uniqueConstraints(UniqueConstraint[] uniqueConstraints) {
		this.uniqueConstraints = uniqueConstraints;
	}

	@Override
	public Index[] indexes() {
		return indexes;
	}

	@Override
	public void indexes(Index[] indexes) {
		this.indexes = indexes;
	}

	@Override
	public String options() {
		return options;
	}

	@Override
	public void options(String options) {
		this.options = options;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return CollectionTable.class;
	}
}
