/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.models.ModelsException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.MutableAnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Specialized AnnotationDescriptor implementation intended for use in describing
 * Hibernate and JPA annotations.  Note especially that this implementation
 * does not collect annotations from the annotation class as we never care about
 * meta-annotations in these cases.
 *
 * @implNote There are a few cases in Hibernate ORM e.g. where we do care about meta-annotations,
 * but those are handled specially there.
 *
 * @author Steve Ebersole
 */
public class OrmAnnotationDescriptor<A extends Annotation, C extends A>
		extends AbstractAnnotationDescriptor<A>
		implements MutableAnnotationDescriptor<A,C> {
	private final Class<C> concreteClass;
	private final List<AttributeDescriptor<?>> attributeDescriptors;

	private final DynamicCreator<A,C> dynamicCreator;
	private final JdkCreator<A,C> jdkCreator;
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

		this.dynamicCreator = new DynamicCreator<>( annotationType, concreteClass );
		this.jdkCreator = new JdkCreator<>( annotationType, concreteClass );
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
	public C createUsage(SourceModelBuildingContext context) {
		return dynamicCreator.createUsage( context );
	}

	@Override
	public C createUsage(A jdkAnnotation, SourceModelBuildingContext context) {
		return jdkCreator.createUsage( jdkAnnotation, context );
	}

	@Override
	public A createUsage(Map<String,Object> attributeValues, SourceModelBuildingContext context) {
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
		private final MethodHandle constructor;
		private final Class<C> concreteClass;

		public DynamicCreator(Class<A> annotationType, Class<C> concreteClass) {
			this( resolveConstructor( concreteClass ), concreteClass );
		}

		private static <A extends Annotation, C extends A> MethodHandle resolveConstructor(Class<C> concreteClass) {
			try {
				final MethodType methodType = MethodType.methodType( void.class, SourceModelBuildingContext.class );
				return MethodHandles.publicLookup().findConstructor( concreteClass, methodType );
			}
			catch (Exception e) {
				throw new ModelsException( "Unable to locate default-variant constructor for `" + concreteClass.getName() + "`", e );
			}
		}

		public DynamicCreator(MethodHandle constructor, Class<C> concreteClass) {
			this.constructor = constructor;
			this.concreteClass = concreteClass;
		}

		public C createUsage(SourceModelBuildingContext context) {
			try {
				//noinspection unchecked
				return (C) constructor.invoke( context );
			}
			catch (Throwable e) {
				throw new ModelsException( "Unable to invoke default-variant constructor for `" + concreteClass.getName() + "`", e );
			}
		}
	}

	public static class JdkCreator<A extends Annotation, C extends A> {
		private final MethodHandle constructor;
		private final Class<C> concreteClass;

		public JdkCreator(Class<A> annotationType, Class<C> concreteClass) {
			this( resolveConstructor( annotationType, concreteClass ), concreteClass );
		}

		private static <A extends Annotation, C extends A> MethodHandle resolveConstructor(
				Class<A> annotationType,
				Class<C> concreteClass) {
			try {
				final MethodType methodType = MethodType.methodType( void.class, annotationType, SourceModelBuildingContext.class );
				return MethodHandles.publicLookup().findConstructor( concreteClass, methodType );
			}
			catch (Exception e) {
				throw new ModelsException( "Unable to locate JDK-variant constructor for `" + concreteClass.getName() + "`", e );
			}
		}

		public JdkCreator(MethodHandle constructor, Class<C> concreteClass) {
			this.constructor = constructor;
			this.concreteClass = concreteClass;
		}

		public C createUsage(A jdkAnnotation, SourceModelBuildingContext context) {
			try {
				//noinspection unchecked
				return (C) constructor.invoke( jdkAnnotation, context );
			}
			catch (Throwable e) {
				throw new ModelsException( "Unable to invoke JDK-variant constructor for `" + concreteClass.getName() + "`", e );
			}
		}
	}

	public static class DeTypedCreator<A extends Annotation, C extends A> {
		private final MethodHandle constructor;
		private final Class<C> concreteClass;

		public DeTypedCreator(Class<A> annotationType, Class<C> concreteClass) {
			this( resolveConstructor( concreteClass ), concreteClass );
		}

		private static <A extends Annotation, C extends A> MethodHandle resolveConstructor(Class<C> concreteClass) {
			try {
				final MethodType methodType = MethodType.methodType( void.class, Map.class, SourceModelBuildingContext.class );
				return MethodHandles.publicLookup().findConstructor( concreteClass, methodType );
			}
			catch (Exception e) {
				throw new ModelsException( "Unable to locate Jandex-variant constructor for `" + concreteClass.getName() + "`", e );
			}
		}

		public DeTypedCreator(MethodHandle constructor, Class<C> concreteClass) {
			this.constructor = constructor;
			this.concreteClass = concreteClass;
		}

		public C createUsage(Map<String,?> attributeValues, SourceModelBuildingContext context) {
			try {
				//noinspection unchecked
				return (C) constructor.invoke( attributeValues, context );
			}
			catch (Throwable e) {
				throw new ModelsException( "Unable to invoke Jandex-variant constructor for `" + concreteClass.getName() + "`", e );
			}
		}
	}
}
