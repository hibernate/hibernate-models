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

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ForeignKey;

import static org.hibernate.models.orm.JpaAnnotations.FOREIGN_KEY;

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

	public ForeignKeyAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		name = JandexValueHelper.extractValue( usage, FOREIGN_KEY.getAttribute( "name" ), modelContext );
		value = JandexValueHelper.extractValue( usage, FOREIGN_KEY.getAttribute( "value" ), modelContext );
		foreignKeyDefinition = JandexValueHelper.extractValue( usage, FOREIGN_KEY.getAttribute( "foreignKeyDefinition" ), modelContext );
		options = JandexValueHelper.extractValue( usage, FOREIGN_KEY.getAttribute( "options" ), modelContext );
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
