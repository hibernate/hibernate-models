/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * Specialized AnnotationDescriptor implementation intended for use in describing
 * Hibernate and JPA annotations.  Note especially that this implementation
 * does not collect annotations from the annotation class as we never care about
 * meta-annotations in these cases.
 *
 * @author Steve Ebersole
 */
public class OrmAnnotationDescriptor<A extends Annotation> extends AbstractAnnotationDescriptor<A> {
	private final Class<? extends A> concreteClass;
	private final List<AttributeDescriptor<?>> attributeDescriptors;

	private JdkCreator<A> jdkCreator;
	private JandexCreator<A> jandexCreator;

	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<? extends A> concreteClass) {
		this( annotationType, concreteClass, null );
	}

	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<? extends A> concreteClass,
			AnnotationDescriptor<?> repeatableContainer) {
		super(
				annotationType,
				AnnotationHelper.extractTargets( annotationType ),
				AnnotationHelper.isInherited( annotationType ),
				repeatableContainer
		);

		this.concreteClass = concreteClass;
		this.attributeDescriptors = AnnotationDescriptorBuilding.extractAttributeDescriptors( annotationType );
	}

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		// we never care about the annotations on these annotation classes
		// (aside from @Repeatable, which is handled separately)
		return Collections.emptyMap();
	}

	@Override
	public A createUsage(A jdkAnnotation, SourceModelBuildingContext context) {
		if ( jdkCreator == null ) {
			jdkCreator = new JdkCreator<>( getAnnotationType(), concreteClass );
		}
		return jdkCreator.createUsage( jdkAnnotation, context );
	}

	@Override
	public A createUsage(AnnotationInstance jandexAnnotation, SourceModelBuildingContext context) {
		if ( jandexCreator == null ) {
			jandexCreator = new JandexCreator<>( concreteClass );
		}
		return jandexCreator.createUsage( jandexAnnotation, context );
	}

	@Override
	public A createUsage(SourceModelBuildingContext context) {
		try {
			final Constructor<? extends A> constructor = concreteClass.getDeclaredConstructor();
			return constructor.newInstance();
		}
		catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public List<AttributeDescriptor<?>> getAttributes() {
		return attributeDescriptors;
	}

	@Override
	public String toString() {
		return String.format( "AnnotationDescriptor(%s)", getAnnotationType().getName() );
	}

	public static class JdkCreator<A extends Annotation> {
		private final Constructor<? extends A> constructor;

		public JdkCreator(Class<A> annotationType, Class<? extends A> concreteClass) {
			this( resolveConstructor( annotationType, concreteClass ) );
		}

		private static <A extends Annotation> Constructor<? extends A> resolveConstructor(
				Class<A> annotationType,
				Class<? extends A> concreteClass) {
			try {
				return concreteClass.getDeclaredConstructor( annotationType, SourceModelBuildingContext.class );
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException( e );
			}
		}

		public JdkCreator(Constructor<? extends A> constructor) {
			this.constructor = constructor;
		}

		public A createUsage(A jdkAnnotation, SourceModelBuildingContext context) {
			try {
				return constructor.newInstance( jdkAnnotation, context );
			}
			catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
	}

	public static class JandexCreator<A extends Annotation> {
		private final Constructor<? extends A> constructor;

		public JandexCreator(Class<? extends A> concreteClass) {
			this( resolveConstructor( concreteClass ) );
		}

		private static <A extends Annotation> Constructor<? extends A> resolveConstructor(Class<? extends A> concreteClass) {
			try {
				return concreteClass.getDeclaredConstructor( AnnotationInstance.class, SourceModelBuildingContext.class );
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException( e );
			}
		}

		public JandexCreator(Constructor<? extends A> constructor) {
			this.constructor = constructor;
		}

		public A createUsage(AnnotationInstance jandexAnnotation, SourceModelBuildingContext context) {
			try {
				return constructor.newInstance( jandexAnnotation, context );
			}
			catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
	}
}
