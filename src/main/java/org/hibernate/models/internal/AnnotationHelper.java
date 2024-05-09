/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Helper for dealing with actual {@link Annotation} references
 *
 * @author Steve Ebersole
 */
public class AnnotationHelper {
	private AnnotationHelper() {
		// disallow direct instantiation
	}

	public static <A extends Annotation> boolean isInherited(Class<A> annotationType) {
		return annotationType.isAnnotationPresent( Inherited.class );
	}

	public static <A extends Annotation> EnumSet<AnnotationTarget.Kind> extractTargets(Class<A> annotationType) {
		return AnnotationTarget.Kind.from( annotationType.getAnnotation( Target.class ) );
	}

	public static <A extends Annotation> void render(
			RenderingCollector collector,
			A annotation,
			SourceModelBuildingContext context) {
		//noinspection unchecked
		render(
				collector,
				annotation,
				(AnnotationDescriptor<A>) context.getAnnotationDescriptorRegistry().getDescriptor( annotation.annotationType() ),
				context
		);
	}

	public static <A extends Annotation> void render(
			RenderingCollector collector,
			A annotation,
			AnnotationDescriptor<A> descriptor,
			SourceModelBuildingContext context) {
		final List<AttributeDescriptor<?>> attributes = descriptor.getAttributes();
		if ( attributes.isEmpty() ) {
			collector.addLine( "@%s", descriptor.getAnnotationType().getName() );
		}
		else {
			collector.addLine( "@%s(", descriptor.getAnnotationType().getName() );
			collector.indent( 2 );
			attributes.forEach( (attribute) -> attribute.getTypeDescriptor().render(
					collector,
					attribute.getName(),
					extractValue( annotation, attribute ),
					context
			) );

			collector.unindent( 2 );
			collector.addLine( ")" );
		}
	}

	public static <A extends Annotation> void render(
			RenderingCollector collector,
			String name,
			A annotation,
			SourceModelBuildingContext context) {
		//noinspection unchecked
		render(
				collector,
				name,
				annotation,
				(AnnotationDescriptor<A>) context.getAnnotationDescriptorRegistry().getDescriptor( annotation.annotationType() ),
				context
		);
	}

	public static <A extends Annotation> void render(
			RenderingCollector collector,
			String name,
			A annotation,
			AnnotationDescriptor<A> descriptor,
			SourceModelBuildingContext context) {
		final List<AttributeDescriptor<?>> attributes = descriptor.getAttributes();
		if ( attributes.isEmpty() ) {
			collector.addLine( "%s = @%s", name, descriptor.getAnnotationType().getName() );
		}
		else {
			collector.addLine( "%s = @%s(", name, descriptor.getAnnotationType().getName() );
			collector.indent( 2 );
			attributes.forEach( (attribute) -> attribute.getTypeDescriptor().render(
					collector,
					attribute.getName(),
					extractValue( annotation, attribute ),
					context
			) );

			collector.unindent( 2 );
			collector.addLine( ")" );
		}
	}

	public static <A extends Annotation, R> R extractValue(A annotationUsage, AttributeDescriptor<R> attributeDescriptor) {
		try {
			//noinspection unchecked
			return (R) attributeDescriptor.getAttributeMethod().invoke( annotationUsage );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new AnnotationAccessException(
					String.format(
							Locale.ROOT,
							"Unable to access annotation attribute value : %s.%s",
							annotationUsage.annotationType().getName(),
							attributeDescriptor.getName()
					),
					e
			);
		}
	}
}
