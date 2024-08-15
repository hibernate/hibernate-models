/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.CheckConstraint;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class CheckConstraintAnnotation implements CheckConstraint {
	private String name;
	private String constraint;
	private String options;

	public CheckConstraintAnnotation(SourceModelBuildingContext modelContext) {
		name = "";
		options = "";
	}

	public CheckConstraintAnnotation(CheckConstraint usage, SourceModelBuildingContext modelContext) {
		this.name = usage.name();
		this.constraint = usage.constraint();
		this.options = usage.options();
	}

	public CheckConstraintAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		this.name = (String) attributeValues.get( "name" );
		this.constraint = (String) attributeValues.get( "constraint" );
		this.options = (String) attributeValues.get( "options" );
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
