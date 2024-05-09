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

import jakarta.persistence.SequenceGenerator;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class SequenceGeneratorAnnotation implements SequenceGenerator, Named, DatabaseObjectDetails {
	private String name;
	private String sequenceName;
	private String catalog;
	private String schema;
	private int initialValue;
	private int allocationSize;
	private String options;

	public SequenceGeneratorAnnotation(SourceModelBuildingContext modelContext) {
		name = "";
		sequenceName = "";
		catalog = "";
		schema = "";
		initialValue = 1;
		allocationSize = 50;
		options = "";
	}

	public SequenceGeneratorAnnotation(SequenceGenerator usage, SourceModelBuildingContext modelContext) {
		name = usage.name();
		sequenceName = usage.sequenceName();
		catalog = usage.catalog();
		schema = usage.schema();
		initialValue = usage.initialValue();
		allocationSize = usage.allocationSize();
		options = usage.options();
	}

	public SequenceGeneratorAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		name = usage.value( "name" ).asString();
		sequenceName = usage.value( "sequenceName" ).asString();
		catalog = usage.value( "catalog" ).asString();
		schema = usage.value( "schema" ).asString();
		initialValue = usage.value( "initialValue" ).asInt();
		allocationSize = usage.value( "allocationSize" ).asInt();
		options = usage.value( "options" ).asString();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void name(String name) {
		this.name = name;
	}

	@Override
	public String sequenceName() {
		return sequenceName;
	}

	public void sequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	@Override
	public String catalog() {
		return catalog;
	}

	public void catalog(String catalog) {
		this.catalog = catalog;
	}

	@Override
	public String schema() {
		return schema;
	}

	public void schema(String schema) {
		this.schema = schema;
	}

	@Override
	public int initialValue() {
		return initialValue;
	}

	public void initialValue(int initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public int allocationSize() {
		return allocationSize;
	}

	public void allocationSize(int allocationSize) {
		this.allocationSize = allocationSize;
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
		return SequenceGenerator.class;
	}
}
