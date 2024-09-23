/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;

import static org.hibernate.models.internal.AnnotationUsageHelper.extractRepeatedValues;
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

	public ColumnAnnotation(SourceModelBuildingContext modelContext) {
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

	public ColumnAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		name = (String) attributeValues.get( "name" );
		table = (String) attributeValues.get( "table" );
		nullable = (boolean) attributeValues.get( "nullable" );
		unique = (boolean) attributeValues.get( "unique" );
		insertable = (boolean) attributeValues.get( "insertable" );
		updatable = (boolean) attributeValues.get( "updatable" );
		comment = (String) attributeValues.get( "comment" );
		columnDefinition = (String) attributeValues.get( "columnDefinition" );
		options = (String) attributeValues.get( "options" );
		length = (int) attributeValues.get( "length" );
		precision = (int) attributeValues.get( "precision" );
		scale = (int) attributeValues.get( "scale" );
		check = (CheckConstraint[]) attributeValues.get( "check" );
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
