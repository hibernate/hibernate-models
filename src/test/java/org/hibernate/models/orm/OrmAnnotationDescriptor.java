/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.models.internal.AbstractAnnotationDescriptor;
import org.hibernate.models.internal.AnnotationDescriptorBuilding;
import org.hibernate.models.internal.AnnotationHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * @author Steve Ebersole
 */
public class OrmAnnotationDescriptor<A extends Annotation> extends AbstractAnnotationDescriptor<A> {
	private final Class<? extends A> concreteClass;
	private final List<AttributeDescriptor<?>> attributeDescriptors;

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
		return Collections.emptyMap();
	}

	@Override
	public A createUsage(A jdkAnnotation, SourceModelBuildingContext context) {
		try {
			final Constructor<? extends A> constructor = concreteClass.getDeclaredConstructor(
					getAnnotationType(),
					SourceModelBuildingContext.class
			);
			return constructor.newInstance( jdkAnnotation, context );
		}
		catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public A createUsage(AnnotationInstance jandexAnnotation, SourceModelBuildingContext context) {
		try {
			final Constructor<? extends A> constructor = concreteClass.getDeclaredConstructor(
					AnnotationInstance.class,
					SourceModelBuildingContext.class
			);
			return constructor.newInstance( jandexAnnotation, context );
		}
		catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException( e );
		}
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
}
