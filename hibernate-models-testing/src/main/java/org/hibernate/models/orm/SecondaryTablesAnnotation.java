/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;

import static org.hibernate.models.orm.JpaAnnotations.SECONDARY_TABLES;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class SecondaryTablesAnnotation implements SecondaryTables, RepeatableContainer<SecondaryTable> {
	private SecondaryTable[] value;

	public SecondaryTablesAnnotation(SourceModelBuildingContext modelContext) {
		value = new SecondaryTable[0];
	}

	public SecondaryTablesAnnotation(SecondaryTables usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, SECONDARY_TABLES, modelContext );
	}


	public SecondaryTablesAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		value = (SecondaryTable[]) attributeValues.get( "value" );
	}

	@Override
	public SecondaryTable[] value() {
		return value;
	}

	@Override
	public void value(SecondaryTable[] values) {
		this.value = values;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return SecondaryTables.class;
	}
}
