/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationDescriptorRegistry implements AnnotationDescriptorRegistry {
	protected final Map<Class<? extends Annotation>, AnnotationDescriptor<?>> descriptorMap;
	protected final Map<AnnotationDescriptor<?>, AnnotationDescriptor<?>> repeatableByContainerMap;

	public AbstractAnnotationDescriptorRegistry() {
		this( new ConcurrentHashMap<>(), new ConcurrentHashMap<>() );
	}

	public AbstractAnnotationDescriptorRegistry(
			Map<Class<? extends Annotation>, AnnotationDescriptor<?>> descriptorMap,
			Map<AnnotationDescriptor<?>, AnnotationDescriptor<?>> repeatableByContainerMap) {
		this.descriptorMap = descriptorMap;
		this.repeatableByContainerMap = repeatableByContainerMap;
	}

	/**
	 * Returns the descriptor of the {@linkplain Repeatable repeatable} annotation
	 * {@linkplain AnnotationDescriptor#getRepeatableContainer contained} by the given
	 * {@code containerDescriptor}. For example, calling this method with JPA's
	 * {@code NamedQueries} would return the descriptor for {@code NamedQuery}.
	 * <p/>
	 * It is the logical inverse of {@link AnnotationDescriptor#getRepeatableContainer}.
	 */
	@Override
	public <A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(AnnotationDescriptor<A> containerDescriptor) {
		//noinspection unchecked
		return (AnnotationDescriptor<A>) repeatableByContainerMap.get( containerDescriptor );
	}

	/**
	 * @see #getContainedRepeatableDescriptor
	 */
	@Override
	public <A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(Class<A> containerJavaType) {
		return getContainedRepeatableDescriptor( getDescriptor( containerJavaType ) );
	}
}
