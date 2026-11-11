/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.internal.AnnotationUsageValidation;

/**
 * Describes an annotation type (the Class)
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptor<A extends Annotation> extends AnnotationTarget {
	@Nonnull
	@Override
	default Kind getKind() {
		return Kind.ANNOTATION;
	}

	/**
	 * The annotation type
	 */
	@Nonnull
	Class<A> getAnnotationType();

	@Nonnull
	default <S> S as(@Nonnull Class<S> type) {
		//noinspection unchecked
		return (S) type;
	}

	@Nullable
	@Override
	default ClassDetails getContainer(@Nonnull ModelsContext modelsContext) {
		final ClassDetails annotationClassDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( getAnnotationType().getName() );
		return annotationClassDetails.getContainer( modelsContext );
	}
	/**
	 * Create an empty usage.  Used when there is no source form, such as XML processing.
	 */
	@Nonnull
	A createUsage(@Nonnull ModelsContext context);

	/**
	 * Create a usage from the JDK representation.  This will often just return the passed annotation,
	 * although for Hibernate and JPA annotations we generally want wrappers to be able to manipulate the
	 * values.
	 */
	@Nonnull
	A createUsage(@Nonnull A jdkAnnotation, @Nonnull ModelsContext context);

	/**
	 * Validate that a completed usage satisfies this annotation descriptor,
	 * including recursively contained annotation usages.
	 *
	 * @throws org.hibernate.models.InvalidAnnotationUsageException if the usage is incomplete or invalid
	 */
	default void validateUsage(@Nonnull A usage, @Nonnull ModelsContext modelsContext) {
		AnnotationUsageValidation.validateUsage( this, usage, modelsContext );
	}

	/**
	 * Create a usage from the JDK representation.  This will often just return the passed annotation,
	 * although for Hibernate and JPA annotations we generally want wrappers to be able to manipulate the
	 * values.
	 */
	@Nonnull
	default A createUsage(@Nonnull Map<String, Object> attributeValues, @Nonnull ModelsContext context) {
		return AnnotationProxy.makeProxy( this, attributeValues );
	}

	/**
	 * The places the described annotation can be used
	 */
	@Nonnull
	EnumSet<Kind> getAllowableTargets();

	/**
	 * Whether the annotation defined as {@linkplain java.lang.annotation.Inherited inherited}
	 */
	boolean isInherited();

	/**
	 * Whether the annotation is {@linkplain Repeatable repeatable}.
	 */
	default boolean isRepeatable() {
		return getRepeatableContainer() != null;
	}

	/**
	 * If the described annotation is {@linkplain #isRepeatable repeatable}, returns the descriptor
	 * for the {@linkplain Repeatable#value() container} annotation.
	 */
	@Nullable
	AnnotationDescriptor<?> getRepeatableContainer();

	/**
	 * The attributes of the annotation
	 */
	@Nonnull
	List<AttributeDescriptor<?>> getAttributes();

	/**
	 * Get an attribute descriptor by name, returning {@code null} if the name
	 * is not an attribute of the described annotation.
	 */
	@Nullable
	default <V> AttributeDescriptor<V> findAttribute(@Nonnull String name) {
		final List<AttributeDescriptor<?>> attributeDescriptors = getAttributes();
		for ( final AttributeDescriptor<?> attributeDescriptor : attributeDescriptors ) {
			if ( attributeDescriptor.getName().equals( name ) ) {
				//noinspection unchecked
				return (AttributeDescriptor<V>) attributeDescriptor;
			}
		}
		return null;
	}

	/**
	 * Get an attribute descriptor by name.
	 *
	 * @throws UnknownAnnotationAttributeException if the name is not an
	 * attribute of the described annotation.
	 */
	@Nonnull
	default <V> AttributeDescriptor<V> getAttribute(@Nonnull String name) {
		final AttributeDescriptor<V> attribute = findAttribute( name );
		if ( attribute != null ) {
			return attribute;
		}

		throw new UnknownAnnotationAttributeException( getAnnotationType(), name );
	}

	@Nonnull
	@Override
	default <X extends Annotation> AnnotationDescriptor<X> asAnnotationDescriptor() {
		//noinspection unchecked
		return (AnnotationDescriptor<X>) this;
	}

}
