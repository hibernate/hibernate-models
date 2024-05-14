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
import jakarta.persistence.Column;

import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
import static org.hibernate.models.internal.jandex.JandexValueHelper.extractOptionalValue;
import static org.hibernate.models.orm.JpaAnnotations.COLUMN;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class ColumnAnnotation implements Column, ColumnDetails {
	private String name;
	private String table;

	private boolean nullable;
	private boolean unique;
	private boolean insertable;
	private boolean updatable;
	private String comment;
	private String columnDefinition;
	private String options;
	private int length;
	private int precision;
	private int scale;

	private CheckConstraint[] check;

	public ColumnAnnotation() {
		name = "";
		table = "";
		nullable = true;
		unique = false;
		insertable = true;
		updatable = true;
		comment = "";
		columnDefinition = "";
		options = "";
		length = 255;
		precision = 0;
		scale = 0;
		check = new CheckConstraint[0];
	}

	public ColumnAnnotation(Column usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		table = usage.table();
		nullable = usage.nullable();
		unique = usage.unique();
		insertable = usage.insertable();
		updatable = usage.updatable();
		comment = usage.comment();
		columnDefinition = usage.columnDefinition();
		options = usage.options();
		length = usage.length();
		precision = usage.precision();
		scale = usage.scale();
		check = extractRepeatedValues( usage, COLUMN.getAttribute( "check" ), modelContext );
	}

	public ColumnAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		name = extractOptionalValue( usage, COLUMN, "name", modelContext );
		table = extractOptionalValue( usage, COLUMN, "table", modelContext );
		nullable = extractOptionalValue( usage, COLUMN, "nullable", modelContext );
		unique = extractOptionalValue( usage, COLUMN, "unique", modelContext );
		insertable = extractOptionalValue( usage, COLUMN, "insertable", modelContext );
		updatable = extractOptionalValue( usage, COLUMN, "updatable", modelContext );
		comment = extractOptionalValue( usage, COLUMN, "comment", modelContext );
		columnDefinition = extractOptionalValue( usage, COLUMN, "columnDefinition", modelContext );
		options = extractOptionalValue( usage, COLUMN, "options", modelContext );
		length = extractOptionalValue( usage, COLUMN, "length", modelContext );
		precision = extractOptionalValue( usage, COLUMN, "precision", modelContext );
		scale = extractOptionalValue( usage, COLUMN, "scale", modelContext );
		check = extractOptionalValue( usage, COLUMN, "check", modelContext );
	}

	@Override
	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	@Override
	public String table() {
		return table;
	}

	public void table(String table) {
		this.table = table;
	}

	@Override
	public boolean nullable() {
		return nullable;
	}

	public void nullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public boolean unique() {
		return unique;
	}

	public void unique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public boolean insertable() {
		return insertable;
	}

	public void insertable(boolean insertable) {
		this.insertable = insertable;
	}

	@Override
	public boolean updatable() {
		return updatable;
	}

	public void updatable(boolean updatable) {
		this.updatable = updatable;
	}

	@Override
	public String columnDefinition() {
		return columnDefinition;
	}

	public void columnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	@Override
	public String options() {
		return options;
	}

	public void options(String options) {
		this.options = options;
	}


	@Override
	public int length() {
		return length;
	}

	public void length(int length) {
		this.length = length;
	}

	@Override
	public int precision() {
		return precision;
	}

	public void precision(int precision) {
		this.precision = precision;
	}

	@Override
	public int scale() {
		return scale;
	}

	public void scale(int scale) {
		this.scale = scale;
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
	public Class<? extends Annotation> annotationType() {
		return Column.class;
	}
}
