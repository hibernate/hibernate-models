/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.models.InvalidAnnotationUsageException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ModelsContext;

/**
 * Internal implementation of descriptor-driven annotation usage validation.
 *
 * @since 2.0
 * @author Steve Ebersole
 */
public final class AnnotationUsageValidation {
	private AnnotationUsageValidation() {
	}

	public static <A extends Annotation> void validateUsage(
			AnnotationDescriptor<A> descriptor,
			A usage,
			ModelsContext modelsContext) {
		final String rootPath = "@" + descriptor.getAnnotationType().getName();
		if ( usage == null ) {
			throw invalid( descriptor, rootPath, "Annotation usage must not be null" );
		}

		final Class<? extends Annotation> actualType;
		try {
			actualType = usage.annotationType();
		}
		catch (RuntimeException e) {
			throw invalid( descriptor, rootPath, "Could not determine annotation usage type", e );
		}
		if ( actualType != descriptor.getAnnotationType() ) {
			throw invalid(
					descriptor,
					rootPath,
					"Expected annotation type " + descriptor.getAnnotationType().getName()
							+ " but found " + ( actualType == null ? "null" : actualType.getName() )
			);
		}

		for ( AttributeDescriptor<?> attribute : descriptor.getAttributes() ) {
			final String attributePath = rootPath + "." + attribute.getName();
			final Object value = extractValue( descriptor, usage, attribute, attributePath );
			validateValue(
					descriptor,
					attribute.getAttributeMethod().getReturnType(),
					value,
					attributePath,
					modelsContext
			);
		}
	}

	private static Object extractValue(
			AnnotationDescriptor<?> descriptor,
			Annotation usage,
			AttributeDescriptor<?> attribute,
			String attributePath) {
		try {
			return attribute.getAttributeMethod().invoke( usage );
		}
		catch (IllegalAccessException | InvocationTargetException | RuntimeException e) {
			throw invalid( descriptor, attributePath, "Could not access annotation attribute value", e );
		}
	}

	private static void validateValue(
			AnnotationDescriptor<?> descriptor,
			Class<?> declaredType,
			Object value,
			String attributePath,
			ModelsContext modelsContext) {
		if ( value == null ) {
			throw invalid( descriptor, attributePath, "Annotation attribute value must not be null" );
		}
		if ( !isInstance( declaredType, value ) ) {
			throw invalid(
					descriptor,
					attributePath,
					"Expected value of type " + declaredType.getTypeName()
							+ " but found " + value.getClass().getTypeName()
			);
		}

		if ( declaredType.isAnnotation() ) {
			validateNestedUsage( descriptor, (Annotation) value, attributePath, modelsContext );
		}
		else if ( declaredType.isArray() && declaredType.getComponentType().isAnnotation() ) {
			for ( int i = 0; i < Array.getLength( value ); i++ ) {
				validateNestedUsage(
						descriptor,
						(Annotation) Array.get( value, i ),
						attributePath + "[" + i + "]",
						modelsContext
				);
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void validateNestedUsage(
			AnnotationDescriptor<?> owningDescriptor,
			Annotation usage,
			String attributePath,
			ModelsContext modelsContext) {
		if ( usage == null ) {
			throw invalid( owningDescriptor, attributePath, "Nested annotation usage must not be null" );
		}
		final AnnotationDescriptor nestedDescriptor = modelsContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( usage.annotationType() );
		validateNestedAttributes( nestedDescriptor, usage, attributePath, modelsContext );
	}

	private static <A extends Annotation> void validateNestedAttributes(
			AnnotationDescriptor<A> descriptor,
			A usage,
			String attributePath,
			ModelsContext modelsContext) {
		for ( AttributeDescriptor<?> attribute : descriptor.getAttributes() ) {
			final String nestedPath = attributePath + "." + attribute.getName();
			final Object value = extractValue( descriptor, usage, attribute, nestedPath );
			validateValue(
					descriptor,
					attribute.getAttributeMethod().getReturnType(),
					value,
					nestedPath,
					modelsContext
			);
		}
	}

	private static boolean isInstance(Class<?> declaredType, Object value) {
		if ( !declaredType.isPrimitive() ) {
			return declaredType.isInstance( value );
		}
		return declaredType == boolean.class && value instanceof Boolean
				|| declaredType == byte.class && value instanceof Byte
				|| declaredType == short.class && value instanceof Short
				|| declaredType == int.class && value instanceof Integer
				|| declaredType == long.class && value instanceof Long
				|| declaredType == float.class && value instanceof Float
				|| declaredType == double.class && value instanceof Double
				|| declaredType == char.class && value instanceof Character;
	}

	private static InvalidAnnotationUsageException invalid(
			AnnotationDescriptor<?> descriptor,
			String attributePath,
			String message) {
		return new InvalidAnnotationUsageException( descriptor.getAnnotationType(), attributePath, message );
	}

	private static InvalidAnnotationUsageException invalid(
			AnnotationDescriptor<?> descriptor,
			String attributePath,
			String message,
			Throwable cause) {
		return new InvalidAnnotationUsageException( descriptor.getAnnotationType(), attributePath, message, cause );
	}
}
