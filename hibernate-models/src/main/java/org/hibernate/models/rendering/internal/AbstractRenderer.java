/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import org.hibernate.models.rendering.Renderer;
import org.hibernate.models.rendering.RenderingTarget;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;

import static org.hibernate.models.spi.AnnotationHelper.extractValue;

/**
 * Base support for Renderer implementations.  Handles rendering of annotations.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractRenderer implements Renderer {
	protected abstract RenderingTarget getRenderingTarget();

	@Override
	public void renderClass(ClassDetails classDetails, ModelsContext context) {
		renderDirectAnnotations( classDetails, context );
		renderClassDetails( classDetails, context );
	}

	private void renderDirectAnnotations(AnnotationTarget annotationTarget, ModelsContext context) {
		annotationTarget.forEachDirectAnnotationUsage( (usage) -> renderAnnotation( usage, context ) );
	}

	protected abstract void renderClassDetails(ClassDetails classDetails, ModelsContext context);

	@Override
	public void renderField(FieldDetails fieldDetails, ModelsContext context) {
		renderDirectAnnotations( fieldDetails, context );
		renderFieldDetails( fieldDetails, context );
	}

	protected abstract void renderFieldDetails(FieldDetails fieldDetails, ModelsContext context);

	@Override
	public void renderMethod(MethodDetails methodDetails, ModelsContext context) {
		renderDirectAnnotations( methodDetails, context );
		renderMethodDetails( methodDetails, context );
	}

	protected abstract void renderMethodDetails(MethodDetails methodDetails, ModelsContext context);

	@Override
	public void renderRecordComponent(RecordComponentDetails recordComponentDetails, ModelsContext context) {
		renderDirectAnnotations( recordComponentDetails, context );
		renderRecordComponentDetails( recordComponentDetails, context );
	}

	protected abstract void renderRecordComponentDetails(RecordComponentDetails recordComponentDetails, ModelsContext context);


	@Override
	public <A extends Annotation> void renderAnnotation(A annotation, ModelsContext context) {
		//noinspection unchecked
		final AnnotationDescriptor<A> descriptor = (AnnotationDescriptor<A>) context.getAnnotationDescriptorRegistry().getDescriptor( annotation.annotationType() );
		final List<AttributeDescriptor<?>> attributes = descriptor.getAttributes();

		if ( attributes.isEmpty() ) {
			getRenderingTarget().addLine( "@%s", descriptor.getAnnotationType().getName() );
		}
		else {
			getRenderingTarget().addLine( "@%s(", descriptor.getAnnotationType().getName() );
			getRenderingTarget().indent( 2 );
			renderAttributes( annotation, attributes, context );
			getRenderingTarget().unindent( 2 );
			getRenderingTarget().addLine( ")" );
		}
	}

	@Override
	public <A extends Annotation> void renderNestedAnnotation(String name, A annotation, ModelsContext context) {
		//noinspection unchecked
		final AnnotationDescriptor<A> descriptor = (AnnotationDescriptor<A>) context.getAnnotationDescriptorRegistry().getDescriptor( annotation.annotationType() );
		final List<AttributeDescriptor<?>> attributes = descriptor.getAttributes();

		getRenderingTarget().addLine( "%s = @%s(", name, descriptor.getAnnotationType().getName() );
		getRenderingTarget().indent( 2 );
		renderAttributes( annotation, attributes, context );
		getRenderingTarget().unindent( 2 );
		getRenderingTarget().addLine( ")" );
	}

	@Override
	public <A extends Annotation> void renderNestedAnnotation(A annotation, ModelsContext context) {
		//noinspection unchecked
		final AnnotationDescriptor<A> descriptor = (AnnotationDescriptor<A>) context.getAnnotationDescriptorRegistry().getDescriptor( annotation.annotationType() );
		final List<AttributeDescriptor<?>> attributes = descriptor.getAttributes();

		getRenderingTarget().addLine( "@%s(", descriptor.getAnnotationType().getName() );
		getRenderingTarget().indent( 2 );
		renderAttributes( annotation, attributes, context );
		getRenderingTarget().unindent( 2 );
		getRenderingTarget().addLine( ")" );
	}

	private void renderAttributes(
			Annotation annotation,
			List<AttributeDescriptor<?>> attributes,
			ModelsContext context) {
		attributes.forEach( (attribute) -> renderValue(
				attribute.getName(),
				attribute.getAttributeMethod().getReturnType(),
				extractValue( annotation, attribute ),
				context
		) );
	}

	private void renderValue(String name, Class<?> valueType, Object value, ModelsContext context) {
		if ( value == null ) {
			if ( name == null ) {
				getRenderingTarget().addLine( "null" );
			}
			else {
				getRenderingTarget().addLine( "%s = null", name );
			}
			return;
		}

		if ( valueType.isArray() ) {
			renderArray( name, valueType.getComponentType(), value, context );
		}
		else if ( valueType.isAnnotation() ) {
			@SuppressWarnings("unchecked")
			final Annotation nested = (Annotation) value;
			if ( name == null ) {
				renderNestedAnnotation( nested, context );
			}
			else {
				renderNestedAnnotation( name, nested, context );
			}
		}
		else {
			final Object renderedValue = renderedScalarValue( valueType, value );
			if ( name == null ) {
				getRenderingTarget().addLine( "%s", renderedValue );
			}
			else {
				getRenderingTarget().addLine( "%s = %s", name, renderedValue );
			}
		}
	}

	private void renderArray(
			String name,
			Class<?> componentType,
			Object values,
			ModelsContext context) {
		if ( name == null ) {
			getRenderingTarget().addLine( "{" );
		}
		else {
			getRenderingTarget().addLine( "%s = {", name );
		}
		getRenderingTarget().indent( 2 );
		for ( int i = 0; i < Array.getLength( values ); i++ ) {
			renderValue( null, componentType, Array.get( values, i ), context );
		}
		getRenderingTarget().unindent( 2 );
		getRenderingTarget().addLine( "}" );
	}

	private static Object renderedScalarValue(Class<?> valueType, Object value) {
		if ( valueType == String.class ) {
			return '"' + value.toString() + '"';
		}
		if ( valueType == Class.class ) {
			return ( (Class<?>) value ).getName();
		}
		if ( valueType == float.class || valueType == Float.class ) {
			return value + "F";
		}
		if ( valueType == long.class || valueType == Long.class ) {
			return value + "L";
		}
		return value;

	}
}
