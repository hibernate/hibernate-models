/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;

@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class PrimaryKeyJoinColumnsJpaAnnotation
		implements PrimaryKeyJoinColumns, RepeatableContainer<PrimaryKeyJoinColumn> {
	private PrimaryKeyJoinColumn[] value;
	private jakarta.persistence.ForeignKey foreignKey;

	/**
	 * Used in creating dynamic annotation instances (e.g. from XML)
	 */
	public PrimaryKeyJoinColumnsJpaAnnotation(ModelsContext modelContext) {
		this.foreignKey = modelContext.getAnnotationDescriptorRegistry()
				.getDescriptor( jakarta.persistence.ForeignKey.class )
				.createUsage( modelContext );
	}

	/**
	 * Used in creating annotation instances from JDK variant
	 */
	public PrimaryKeyJoinColumnsJpaAnnotation(
			PrimaryKeyJoinColumns annotation,
			ModelsContext modelContext) {
		this.value = annotation.value();
		this.foreignKey = new ForeignKeyAnnotation( annotation.foreignKey(), modelContext );
	}

	/**
	 * Used in creating annotation instances from Jandex variant
	 */
	public PrimaryKeyJoinColumnsJpaAnnotation(
			Map<String, Object> attributeValues,
			ModelsContext modelContext) {
		this.value = (PrimaryKeyJoinColumn[]) attributeValues.get( "value" );
		this.foreignKey = (jakarta.persistence.ForeignKey) attributeValues.get( "foreignKey" );
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return PrimaryKeyJoinColumns.class;
	}

	@Override
	public PrimaryKeyJoinColumn[] value() {
		return value;
	}

	public void value(PrimaryKeyJoinColumn[] value) {
		this.value = value;
	}


	@Override
	public jakarta.persistence.ForeignKey foreignKey() {
		return foreignKey;
	}

	public void foreignKey(jakarta.persistence.ForeignKey value) {
		this.foreignKey = value;
	}


}
