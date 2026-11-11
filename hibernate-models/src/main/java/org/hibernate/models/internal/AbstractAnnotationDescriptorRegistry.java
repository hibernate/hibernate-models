/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationDescriptorRegistry implements AnnotationDescriptorRegistry {
	@Nonnull
	protected final Map<Class<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> descriptorMap;
	@Nonnull
	protected final Map<AnnotationDescriptor<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> repeatableByContainerMap;

	public AbstractAnnotationDescriptorRegistry() {
		this( new ConcurrentHashMap<>(), new ConcurrentHashMap<>() );
	}

	public AbstractAnnotationDescriptorRegistry(
			@Nonnull Map<Class<? extends Annotation>, AnnotationDescriptor<?>> descriptorMap,
			@Nonnull Map<AnnotationDescriptor<?>, AnnotationDescriptor<?>> repeatableByContainerMap) {
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
	@Nullable
	@Override
	public <A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(@Nonnull AnnotationDescriptor<A> containerDescriptor) {
		//noinspection unchecked
		return (AnnotationDescriptor<A>) repeatableByContainerMap.get( containerDescriptor );
	}

	/**
	 * @see #getContainedRepeatableDescriptor
	 */
	@Nullable
	@Override
	public <A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(@Nonnull Class<A> containerJavaType) {
		return getContainedRepeatableDescriptor( getDescriptor( containerJavaType ) );
	}

	@Nonnull
	public Map<Class<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> descriptorMap() {
		return descriptorMap;
	}

	@Nonnull
	public Map<Class<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> getDescriptorMap() {
		return Collections.unmodifiableMap( descriptorMap );
	}

	@Nonnull
	public Map<AnnotationDescriptor<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> getRepeatableByContainerMap() {
		return Collections.unmodifiableMap( repeatableByContainerMap );
	}
}
