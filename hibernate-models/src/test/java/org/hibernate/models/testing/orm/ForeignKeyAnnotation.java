/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ForeignKey;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class ForeignKeyAnnotation implements ForeignKey {
	private String name;
	private ConstraintMode value;
	private String foreignKeyDefinition;
	private String options;

	public ForeignKeyAnnotation(SourceModelBuildingContext modelContext) {
		name = "";
		value = ConstraintMode.CONSTRAINT;
		foreignKeyDefinition = "";
		options = "";
	}

	public ForeignKeyAnnotation(ForeignKey usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		value = usage.value();
		foreignKeyDefinition = usage.foreignKeyDefinition();
		options = usage.options();
	}

	public ForeignKeyAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		name = (String) attributeValues.get( "name" );
		value = (ConstraintMode) attributeValues.get( "value" );
		foreignKeyDefinition = (String) attributeValues.get( "foreignKeyDefinition" );
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
	public ConstraintMode value() {
		return value;
	}

	public void value(ConstraintMode value) {
		this.value = value;
	}

	@Override
	public String foreignKeyDefinition() {
		return foreignKeyDefinition;
	}

	public void foreignKeyDefinition(String foreignKeyDefinition) {
		this.foreignKeyDefinition = foreignKeyDefinition;
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
		return ForeignKey.class;
	}
}
