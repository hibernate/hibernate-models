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

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
import static org.hibernate.models.internal.jandex.JandexValueHelper.extractOptionalValue;
import static org.hibernate.models.orm.JpaAnnotations.SECONDARY_TABLE;
import static org.hibernate.models.orm.JpaAnnotations.TABLE;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class TableAnnotation implements Table, CommonTableDetails {
	private String name;
	private String catalog;
	private String schema;
	private UniqueConstraint[] uniqueConstraints;
	private Index[] indexes;
	private CheckConstraint[] check;
	private String comment;
	private String options;

	public TableAnnotation() {
		name = "";
		catalog = "";
		schema = "";
		comment = "";
		options = "";
		uniqueConstraints = new UniqueConstraint[0];
		indexes = new Index[0];
		check = new CheckConstraint[0];
	}

	public TableAnnotation(Table usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		catalog = usage.catalog();
		schema = usage.schema();
		comment = usage.comment();
		options = usage.options();
		uniqueConstraints = extractRepeatedValues( usage, TABLE.getAttribute( "uniqueConstraints" ), modelContext );
		indexes = extractRepeatedValues( usage, TABLE.getAttribute( "indexes" ), modelContext );
		check = extractRepeatedValues( usage, TABLE.getAttribute( "check" ), modelContext );
	}

	public TableAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		name = usage.value( "name" ).asString();
		catalog = extractOptionalValue( usage, SECONDARY_TABLE.getAttribute( "catalog" ), modelContext );
		schema = extractOptionalValue( usage, SECONDARY_TABLE.getAttribute( "schema" ), modelContext );
		comment = extractOptionalValue( usage, SECONDARY_TABLE.getAttribute( "comment" ), modelContext );
		options = extractOptionalValue( usage, SECONDARY_TABLE.getAttribute( "options" ), modelContext );
		uniqueConstraints = extractRepeatedValues( usage, TABLE.getAttribute( "uniqueConstraints" ), modelContext );
		indexes = extractRepeatedValues( usage, TABLE.getAttribute( "indexes" ), modelContext );
		check = extractRepeatedValues( usage, TABLE.getAttribute( "check" ), modelContext );
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
		return Table.class;
	}
}
