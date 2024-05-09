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

import jakarta.persistence.UniqueConstraint;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class UniqueConstraintAnnotation implements UniqueConstraint {
	private String name;
	private String[] columnNames;
	private String options;

	public UniqueConstraintAnnotation(SourceModelBuildingContext modelContext) {
		name = "";
		options = "";
	}

	public UniqueConstraintAnnotation(UniqueConstraint usage, SourceModelBuildingContext modelContext) {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	public UniqueConstraintAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	@Override
	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	@Override
	public String[] columnNames() {
		return columnNames;
	}

	public void columnNames(String[] columnNames) {
		this.columnNames = columnNames;
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
		return UniqueConstraint.class;
	}
}
