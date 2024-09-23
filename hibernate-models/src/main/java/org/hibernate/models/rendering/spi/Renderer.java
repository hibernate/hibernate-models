/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelContext;

/**
 * Contract for rendering parts of a model.
 *
 * @author Steve Ebersole
 */
public interface Renderer {
	/**
	 * Render details about the class
	 */
	void renderClass(ClassDetails classDetails, SourceModelContext context);

	/**
	 * Render details about the field
	 */
	void renderField(FieldDetails fieldDetails, SourceModelContext context);

	/**
	 * Render details about the method
	 */
	void renderMethod(MethodDetails methodDetails, SourceModelContext context);

	/**
	 * Render details about the record component
	 */
	void renderRecordComponent(RecordComponentDetails recordComponentDetails, SourceModelContext context);

	/**
	 * Render details about the annotation (top-level)
	 */
	<A extends Annotation> void renderAnnotation(A annotation, SourceModelContext context);

	/**
	 * Render details about the named nested annotation.
	 */
	<A extends Annotation> void renderNestedAnnotation(String name, A annotation, SourceModelContext context);

	/**
	 * Render details about the unnamed nested annotation.
	 */
	<A extends Annotation> void renderNestedAnnotation(A annotation, SourceModelContext context);
}
