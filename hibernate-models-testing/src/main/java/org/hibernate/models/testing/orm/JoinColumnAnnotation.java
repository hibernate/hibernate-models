/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.orm.ColumnDetails.AlternateTableCapable;
import org.hibernate.models.testing.orm.ColumnDetails.Definable;
import org.hibernate.models.testing.orm.ColumnDetails.Mutable;
import org.hibernate.models.testing.orm.ColumnDetails.Nullable;
import org.hibernate.models.testing.orm.ColumnDetails.Uniqueable;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;


/**
 * @author Steve Ebersole
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
public class JoinColumnAnnotation
		implements JoinColumn, Nullable, Uniqueable, Mutable, Definable, AlternateTableCapable {
	private String name;
	private String table;
	private String referencedColumnName;
	private boolean nullable;
	private boolean unique;
	private boolean insertable;
	private boolean updatable;
	private String columnDefinition;
	private String options;

	private ForeignKey foreignKey;
	private CheckConstraint[] check;

	private String comment;

	public JoinColumnAnnotation(ModelsContext modelContext) {
		name = "";
		table = "";
		referencedColumnName = "";
		nullable = true;
		unique = false;
		insertable = true;
		updatable = true;
		comment = "";
		columnDefinition = "";
		options = "";
		foreignKey = new ForeignKeyAnnotation( modelContext );
		check = new CheckConstraint[0];
	}

	public JoinColumnAnnotation(JoinColumn usage, ModelsContext modelContext) {
		name = usage.name();
		table = usage.table();
		referencedColumnName = usage.referencedColumnName();
		nullable = usage.nullable();
		unique = usage.unique();
		insertable = usage.insertable();
		updatable = usage.updatable();
		comment = usage.comment();
		columnDefinition = usage.columnDefinition();
		options = usage.options();
		foreignKey = usage.foreignKey();
		check = usage.check();
	}

	public JoinColumnAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
		name = (String) attributeValues.get( "name" );
		table = (String) attributeValues.get( "table" );
		referencedColumnName = (String) attributeValues.get( "referencedColumnName" );
		nullable = (boolean) attributeValues.get( "nullable" );
		unique = (boolean) attributeValues.get( "unique" );
		insertable = (boolean) attributeValues.get( "insertable" );
		updatable = (boolean) attributeValues.get( "updatable" );
		comment = (String) attributeValues.get( "comment" );
		columnDefinition = (String) attributeValues.get( "columnDefinition" );
		options = (String) attributeValues.get( "options" );
		foreignKey = (ForeignKey) attributeValues.get( "foreignKey" );
		check = (CheckConstraint[]) attributeValues.get( "check" );
	}


	@Override
	public String name() {
		return name;
	}

	@Override
	public void name(String value) {
		this.name = value;
	}

	@Override
	public String referencedColumnName() {
		return referencedColumnName;
	}

	public void referencedColumnName(String referencedColumnName) {
		this.referencedColumnName = referencedColumnName;
	}

	@Override
	public boolean unique() {
		return unique;
	}

	@Override
	public void unique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public boolean nullable() {
		return nullable;
	}

	@Override
	public void nullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public boolean insertable() {
		return insertable;
	}

	@Override
	public void insertable(boolean insertable) {
		this.insertable = insertable;
	}

	@Override
	public boolean updatable() {
		return updatable;
	}

	@Override
	public void updatable(boolean updatable) {
		this.updatable = updatable;
	}

	@Override
	public String columnDefinition() {
		return columnDefinition;
	}

	@Override
	public void columnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
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
	public String table() {
		return table;
	}

	@Override
	public void table(String table) {
		this.table = table;
	}

	@Override
	public ForeignKey foreignKey() {
		return foreignKey;
	}

	public void foreignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
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
		return JoinColumn.class;
	}
}
