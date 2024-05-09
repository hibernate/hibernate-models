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

import static org.hibernate.models.internal.jandex.JandexValueHelper.extractOptionalValue;
import static org.hibernate.models.orm.JpaAnnotations.CHECK_CONSTRAINT;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class CheckConstraintAnnotation implements CheckConstraint {
	private String name;
	private String constraint;
	private String options;

	public CheckConstraintAnnotation() {
		name = "";
		options = "";
	}

	public CheckConstraintAnnotation(CheckConstraint usage, SourceModelBuildingContext modelContext) {
		this.name = usage.name();
		this.constraint = usage.constraint();
		this.options = usage.options();
	}

	public CheckConstraintAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		this.name = extractOptionalValue( usage, CHECK_CONSTRAINT, "name", modelContext );
		this.constraint = extractOptionalValue( usage, CHECK_CONSTRAINT, "constraint", modelContext );
		this.options = extractOptionalValue( usage, CHECK_CONSTRAINT, "options", modelContext );
	}

	@Override
	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	@Override
	public String constraint() {
		assert constraint != null;
		return constraint;
	}

	public void constraint(String constraint) {
		this.constraint = constraint;
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
		return CheckConstraint.class;
	}
}
