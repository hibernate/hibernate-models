/*
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
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationDescriptor;

/**
 * Specialized AnnotationDescriptor implementation intended for use in describing
 * Hibernate and JPA annotations.  Note especially that this implementation
 * does not collect annotations from the annotation class as we never care about
 * meta-annotations in these cases.
 *
 * @author Steve Ebersole
 */
public class OrmAnnotationDescriptor<A extends Annotation, C extends A>
		extends AbstractAnnotationDescriptor<A>
		implements MutableAnnotationDescriptor<A,C> {
	private final Class<C> concreteClass;
	private final List<AttributeDescriptor<?>> attributeDescriptors;

	private DynamicCreator<A,C> dynamicCreator;
	private JdkCreator<A,C> jdkCreator;
	private DeTypedCreator<A,C> deTypedCreator;

	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<C> concreteClass) {
		this( annotationType, concreteClass, null );
	}

	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<C> concreteClass,
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
	public Class<C> getMutableAnnotationType() {
		return concreteClass;
	}

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		// we never care about the annotations on these annotation classes
		// (aside from @Repeatable, which is handled separately)
		return Collections.emptyMap();
	}

	@Override
	public C createUsage(ModelsContext context) {
		if ( dynamicCreator == null ) {
			dynamicCreator = new DynamicCreator<>( getAnnotationType(), concreteClass );
		}
		return dynamicCreator.createUsage( context );
	}

	@Override
	public C createUsage(A jdkAnnotation, ModelsContext context) {
		if ( jdkCreator == null ) {
			jdkCreator = new JdkCreator<>( getAnnotationType(), concreteClass );
		}
		return jdkCreator.createUsage( jdkAnnotation, context );
	}

	@Override
	public A createUsage(Map<String,Object> attributeValues, ModelsContext context) {
		if ( deTypedCreator == null ) {
			deTypedCreator = new DeTypedCreator<>( getAnnotationType(), concreteClass );
		}
		return deTypedCreator.createUsage( attributeValues, context );
	}

	@Override
	public List<AttributeDescriptor<?>> getAttributes() {
		return attributeDescriptors;
	}

	@Override
	public String toString() {
		return String.format( "AnnotationDescriptor(%s)", getAnnotationType().getName() );
	}

	public static class DynamicCreator<A extends Annotation, C extends A> {
		private final Constructor<C> constructor;

		public DynamicCreator(@SuppressWarnings("unused") Class<A> annotationType, Class<C> concreteClass) {
			this( resolveConstructor( concreteClass ) );
		}

		private static <A extends Annotation, C extends A> Constructor<C> resolveConstructor(Class<C> concreteClass) {
			try {
				return concreteClass.getDeclaredConstructor( ModelsContext.class );
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException( e );
			}
		}

		public DynamicCreator(Constructor<C> constructor) {
			this.constructor = constructor;
		}

		public C createUsage(ModelsContext context) {
			try {
				return constructor.newInstance( context );
			}
			catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
	}

	public static class JdkCreator<A extends Annotation, C extends A> {
		private final Constructor<C> constructor;

		public JdkCreator(Class<A> annotationType, Class<C> concreteClass) {
			this( resolveConstructor( annotationType, concreteClass ) );
		}

		private static <A extends Annotation, C extends A> Constructor<C> resolveConstructor(
				Class<A> annotationType,
				Class<C> concreteClass) {
			try {
				return concreteClass.getDeclaredConstructor( annotationType, ModelsContext.class );
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException( e );
			}
		}

		public JdkCreator(Constructor<C> constructor) {
			this.constructor = constructor;
		}

		public C createUsage(A jdkAnnotation, ModelsContext context) {
			try {
				return constructor.newInstance( jdkAnnotation, context );
			}
			catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
	}

	public static class DeTypedCreator<A extends Annotation, C extends A> {
		private final Constructor<C> constructor;

		public DeTypedCreator(@SuppressWarnings("unused") Class<A> annotationType, Class<C> concreteClass) {
			this( resolveConstructor( concreteClass ) );
		}

		private static <A extends Annotation, C extends A> Constructor<C> resolveConstructor(Class<C> concreteClass) {
			try {
				return concreteClass.getDeclaredConstructor( Map.class, ModelsContext.class );
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException( e );
			}
		}

		public DeTypedCreator(Constructor<C> constructor) {
			this.constructor = constructor;
		}

		public C createUsage(Map<String,?> attributeValues, ModelsContext context) {
			try {
				return constructor.newInstance( attributeValues, context );
			}
			catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
	}
}
