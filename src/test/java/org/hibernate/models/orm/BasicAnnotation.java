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

import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;

import static org.hibernate.models.internal.jandex.JandexValueHelper.extractOptionalValue;
import static org.hibernate.models.orm.JpaAnnotations.BASIC;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class BasicAnnotation implements Basic {
	private FetchType fetch;
	private boolean optional;

	public BasicAnnotation() {
		fetch = FetchType.EAGER;
		optional = true;
	}

	public BasicAnnotation(Basic usage, SourceModelBuildingContext modelContext) {
		fetch = usage.fetch();
		optional = usage.optional();
	}

	public BasicAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		fetch = extractOptionalValue( usage, BASIC, "fetch", modelContext );
		optional = extractOptionalValue( usage, BASIC, "optional", modelContext );
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
