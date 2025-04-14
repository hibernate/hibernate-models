/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;


/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class JoinColumnsAnnotation implements JoinColumns, RepeatableContainer<JoinColumn> {
	private JoinColumn[] value;
	private ForeignKey foreignKey;

	public JoinColumnsAnnotation(ModelsContext modelContext) {
		value = new JoinColumn[0];
		foreignKey = new ForeignKeyAnnotation( modelContext );
	}

	public JoinColumnsAnnotation(JoinColumns usage, ModelsContext modelContext) {
		value = usage.value();
		foreignKey = usage.foreignKey();
	}

	public JoinColumnsAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
		value = (JoinColumn[]) attributeValues.get( "value" );
		foreignKey = (ForeignKey) attributeValues.get( "foreignKey" );
	}

	@Override
	public JoinColumn[] value() {
		return value;
	}

	@Override
	public void value(JoinColumn[] value) {
		this.value = value;
	}

	@Override
	public ForeignKey foreignKey() {
		return foreignKey;
	}

	public void foreignKey(ForeignKey foreignKey) {
		this.foreignKey = foreignKey;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return JoinColumns.class;
	}
}
