/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

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

	public SequenceGeneratorAnnotation(ModelsContext modelContext) {
		name = "";
		sequenceName = "";
		catalog = "";
		schema = "";
		initialValue = 1;
		allocationSize = 50;
		options = "";
	}

	public SequenceGeneratorAnnotation(SequenceGenerator usage, ModelsContext modelContext) {
		name = usage.name();
		sequenceName = usage.sequenceName();
		catalog = usage.catalog();
		schema = usage.schema();
		initialValue = usage.initialValue();
		allocationSize = usage.allocationSize();
		options = usage.options();
	}

	public SequenceGeneratorAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
		name = (String) attributeValues.get( "name" );
		sequenceName = (String) attributeValues.get( "sequenceName" );
		catalog = (String) attributeValues.get( "catalog" );
		schema = (String) attributeValues.get( "schema" );
		initialValue = (int) attributeValues.get( "initialValue" );
		allocationSize = (int) attributeValues.get( "allocationSize" );
		options = (String) attributeValues.get( "options" );
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
