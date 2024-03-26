/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.dynamic.DynamicAnnotationUsage;

/**
 * Describes an annotation type (the Class)
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptor<A extends Annotation> extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return Kind.ANNOTATION;
	}

	/**
	 * The annotation type
	 */
	Class<A> getAnnotationType();

	/**
	 * The places the described annotation can be used
	 */
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
	AnnotationDescriptor<?> getRepeatableContainer();

	/**
	 * The attributes of the annotation
	 */
	List<AttributeDescriptor<?>> getAttributes();

	/**
	 * Get an attribute descriptor by name, returning {@code null} if the name
	 * is not an attribute of the described annotation.
	 */
	default <V> AttributeDescriptor<V> findAttribute(String name) {
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
	default <V> AttributeDescriptor<V> getAttribute(String name) {
		final AttributeDescriptor<V> attribute = findAttribute( name );
		if ( attribute != null ) {
			return attribute;
		}

		throw new UnknownAnnotationAttributeException( getAnnotationType(), name );
	}

	/**
	 * Create a usage of this annotation with all attribute values defaulted.
	 *
	 * @param target The target of the annotation being created.  May generally be {@code null}.
	 * @param context Access to needed services
	 */
	default MutableAnnotationUsage<A> createUsage(AnnotationTarget target, SourceModelBuildingContext context) {
		return createUsage( target, null, context );
	}

	/**
	 * Create a usage of this annotation with all attribute values defaulted, allowing customization prior to return.
	 *
	 * @param target The target of the annotation being created.  May generally be {@code null}.
	 * @param adjuster Callback to allow adjusting the created usage prior to return.
	 * @param context Access to needed services
	 */
	default MutableAnnotationUsage<A> createUsage(
			AnnotationTarget target,
			Consumer<MutableAnnotationUsage<A>> adjuster,
			SourceModelBuildingContext context) {
		// create the "empty" usage
		final DynamicAnnotationUsage<A> usage = new DynamicAnnotationUsage<>( this, target, context );

		// apply attribute defaults
		getAttributes().forEach( (attr) -> {
			final Object value = attr.getTypeDescriptor().createValue( attr, target, context );
			if ( value != null ) {
				usage.setAttributeValue( attr.getName(), value );
			}
		} );

		// allow configuration
		if ( adjuster != null ) {
			adjuster.accept( usage );
		}

		return usage;
	}
}
