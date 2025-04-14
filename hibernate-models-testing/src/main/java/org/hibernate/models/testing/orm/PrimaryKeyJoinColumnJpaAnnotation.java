/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.PrimaryKeyJoinColumn;

@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class PrimaryKeyJoinColumnJpaAnnotation implements PrimaryKeyJoinColumn, ColumnDetails, ColumnDetails.Definable {
	private String name;
	private String referencedColumnName;
	private String columnDefinition;
	private String options;
	private jakarta.persistence.ForeignKey foreignKey;

	/**
	 * Used in creating dynamic annotation instances (e.g. from XML)
	 */
	public PrimaryKeyJoinColumnJpaAnnotation(ModelsContext modelContext) {
		this.name = "";
		this.referencedColumnName = "";
		this.columnDefinition = "";
		this.options = "";
		this.foreignKey = new ForeignKeyAnnotation( modelContext );
	}

	/**
	 * Used in creating annotation instances from JDK variant
	 */
	public PrimaryKeyJoinColumnJpaAnnotation(PrimaryKeyJoinColumn annotation, ModelsContext modelContext) {
		this.name = annotation.name();
		this.referencedColumnName = annotation.referencedColumnName();
		this.columnDefinition = annotation.columnDefinition();
		this.options = annotation.options();
		this.foreignKey = new ForeignKeyAnnotation( annotation.foreignKey(), modelContext );
	}

	/**
	 * Used in creating annotation instances from Jandex variant
	 */
	public PrimaryKeyJoinColumnJpaAnnotation(
			Map<String, Object> attributeValues,
			ModelsContext modelContext) {
		this.name = (String) attributeValues.get( "name" );
		this.referencedColumnName = (String) attributeValues.get( "referencedColumnName" );
		this.columnDefinition = (String) attributeValues.get( "columnDefinition" );
		this.options = (String) attributeValues.get( "options" );
		this.foreignKey = (jakarta.persistence.ForeignKey) attributeValues.get( "foreignKey" );
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return PrimaryKeyJoinColumn.class;
	}

	@Override
	public String name() {
		return name;
	}

	public void name(String value) {
		this.name = value;
	}


	@Override
	public String referencedColumnName() {
		return referencedColumnName;
	}

	public void referencedColumnName(String value) {
		this.referencedColumnName = value;
	}


	@Override
	public String columnDefinition() {
		return columnDefinition;
	}

	public void columnDefinition(String value) {
		this.columnDefinition = value;
	}


	@Override
	public String options() {
		return options;
	}

	public void options(String value) {
		this.options = value;
	}


	@Override
	public jakarta.persistence.ForeignKey foreignKey() {
		return foreignKey;
	}

	public void foreignKey(jakarta.persistence.ForeignKey value) {
		this.foreignKey = value;
	}
}
