/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;


/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class BasicAnnotation implements Basic {
	private FetchType fetch;
	private boolean optional;

	public BasicAnnotation(ModelsContext modelContext) {
		fetch = FetchType.EAGER;
		optional = true;
	}

	public BasicAnnotation(Basic usage, ModelsContext modelContext) {
		fetch = usage.fetch();
		optional = usage.optional();
	}

	public BasicAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
		fetch = (FetchType) attributeValues.get( "fetch" );
		optional = (boolean) attributeValues.get( "optional" );
	}

	@Override
	public FetchType fetch() {
		return fetch;
	}

	public void fetch(FetchType fetch) {
		this.fetch = fetch;
	}

	@Override
	public boolean optional() {
		return optional;
	}

	public void optional(boolean optional) {
		this.optional = optional;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Basic.class;
	}
}
