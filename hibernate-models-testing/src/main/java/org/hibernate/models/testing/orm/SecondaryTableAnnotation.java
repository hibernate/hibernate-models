/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.UniqueConstraint;

import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
import static org.hibernate.models.testing.orm.JpaAnnotations.SECONDARY_TABLE;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class SecondaryTableAnnotation implements SecondaryTable, CommonTableDetails {
	private String name;
	private String catalog;
	private String schema;
	private String comment;
	private String options;
	private UniqueConstraint[] uniqueConstraints;
	private Index[] indexes;
	private CheckConstraint[] check;
	private PrimaryKeyJoinColumn[] primaryKeyJoinColumns;
	private ForeignKey foreignKey;

	public SecondaryTableAnnotation(SourceModelBuildingContext modelContext) {
		catalog = "";
		schema = "";
		comment = "";
		options = "";
		uniqueConstraints = new UniqueConstraint[0];
		indexes = new Index[0];
		check = new CheckConstraint[0];
		primaryKeyJoinColumns = new PrimaryKeyJoinColumn[0];
		foreignKey = new ForeignKeyAnnotation( modelContext );
	}

	public SecondaryTableAnnotation(SecondaryTable usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		catalog = usage.catalog();
		schema = usage.schema();
		comment = usage.comment();
		options = usage.options();
		uniqueConstraints = extractRepeatedValues( usage, SECONDARY_TABLE.getAttribute( "uniqueConstraints" ), modelContext );
		indexes = extractRepeatedValues( usage, SECONDARY_TABLE.getAttribute( "indexes" ), modelContext );
		check = extractRepeatedValues( usage, SECONDARY_TABLE.getAttribute( "check" ), modelContext );
		primaryKeyJoinColumns = extractRepeatedValues( usage, SECONDARY_TABLE.getAttribute( "pkJoinColumns" ), modelContext );
		foreignKey = new ForeignKeyAnnotation( usage.foreignKey(), modelContext );
	}

	public SecondaryTableAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		name = attributeValues.get( "name" ).toString();
		catalog = (String) attributeValues.get( "catalog" );
		schema = (String) attributeValues.get( "schema" );
		comment = (String) attributeValues.get( "comment" );
		options = (String) attributeValues.get( "options" );
		uniqueConstraints = (UniqueConstraint[]) attributeValues.get( "uniqueConstraints" );
		indexes = (Index[]) attributeValues.get( "indexes" );
		check = (CheckConstraint[]) attributeValues.get( "check" );
		primaryKeyJoinColumns = (PrimaryKeyJoinColumn[]) attributeValues.get( "pkJoinColumns" );
		foreignKey = (ForeignKey) attributeValues.get( "foreignKey" );
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
	public PrimaryKeyJoinColumn[] pkJoinColumns() {
		return primaryKeyJoinColumns;
	}

	public void pkJoinColumns(PrimaryKeyJoinColumn[] primaryKeyJoinColumns) {
		this.primaryKeyJoinColumns = primaryKeyJoinColumns;
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
	public CheckConstraint[] check() {
		return check;
	}

	public void check(CheckConstraint[] check) {
		this.check = check;
	}

	@Override
	public String comment() {
		return comment;
	}

	public void comment(String comment) {
		this.comment = comment;
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
		return SecondaryTable.class;
	}
}
