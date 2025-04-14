/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.UniqueConstraint;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class UniqueConstraintAnnotation implements UniqueConstraint {
	private String name;
	private String[] columnNames;
	private String options;

	public UniqueConstraintAnnotation(ModelsContext modelContext) {
		name = "";
		options = "";
	}

	public UniqueConstraintAnnotation(UniqueConstraint usage, ModelsContext modelContext) {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}

	public UniqueConstraintAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
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
