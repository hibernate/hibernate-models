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
	 * Get an attribute descriptor by name.
	 *
	 * @throws UnknownAnnotationAttributeException if the name is not an
	 * attribute of the described annotation.
	 */
	<V> AttributeDescriptor<V> findAttribute(String name);

	/**
	 * Get an attribute descriptor by name, returning {@code null} if the name
	 * is not an attribute of the described annotation.
	 */
	default <V> AttributeDescriptor<V> getAttribute(String name) {
		final AttributeDescriptor<Object> attribute = findAttribute( name );
		if ( attribute != null ) {
			//noinspection unchecked
			return (AttributeDescriptor<V>) attribute;
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
		final DynamicAnnotationUsage<A> usage = new DynamicAnnotationUsage<>( this, target, context );
		getAttributes().forEach( (attr) -> {
			final Object value = attr.getTypeDescriptor().createValue( attr, target, context );
			if ( value != null ) {
				usage.setAttributeValue( attr.getName(), value );
			}
		} );
		return usage;
	}

	/**
	 * Create a usage of this annotation with all attribute values defaulted, allowing customization prior to return.
	 *
	 * @param target The target of the annotation being created.  May generally be {@code null}.
	 * @param adjuster Callback to allow adjusting the created usage prior to return.
	 * @param context Access to needed services
	 */
	default MutableAnnotationUsage<A> createUsage(AnnotationTarget target, Consumer<MutableAnnotationUsage<A>> adjuster, SourceModelBuildingContext context) {
		final MutableAnnotationUsage<A> usage = createUsage( target, context );
		adjuster.accept( usage );
		return usage;
	}
}
