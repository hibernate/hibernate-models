/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;

/**
 * Contract for rendering parts of a model.
 *
 * @author Steve Ebersole
 */
public interface Renderer {
	/**
	 * Render details about the class
	 */
	void renderClass(@Nonnull ClassDetails classDetails, @Nonnull ModelsContext context);

	/**
	 * Render details about the field
	 */
	void renderField(@Nonnull FieldDetails fieldDetails, @Nonnull ModelsContext context);

	/**
	 * Render details about the method
	 */
	void renderMethod(@Nonnull MethodDetails methodDetails, @Nonnull ModelsContext context);

	/**
	 * Render details about the record component
	 */
	void renderRecordComponent(@Nonnull RecordComponentDetails recordComponentDetails, @Nonnull ModelsContext context);

	/**
	 * Render details about the annotation (top-level)
	 */
	<A extends Annotation> void renderAnnotation(@Nonnull A annotation, @Nonnull ModelsContext context);

	/**
	 * Render details about the named nested annotation.
	 */
	<A extends Annotation> void renderNestedAnnotation(@Nonnull String name, @Nonnull A annotation, @Nonnull ModelsContext context);

	/**
	 * Render details about the unnamed nested annotation.
	 */
	<A extends Annotation> void renderNestedAnnotation(@Nonnull A annotation, @Nonnull ModelsContext context);
}
