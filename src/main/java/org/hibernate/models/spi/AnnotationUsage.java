/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.models.internal.RenderingCollectorImpl;

/**
 * Describes the usage of an {@linkplain AnnotationDescriptor annotation class} on one of its
 * allowable {@linkplain AnnotationTarget targets}.
 * <p/>
 * The standard way to access values is using {@linkplain #getAttributeValue}.  Convenience
 * methods have been added for the allowable annotation types.  These methods may throw
 * exceptions (generally {@linkplain ClassCastException}, if the expected type does not match).<ul>
 *     <li>{@linkplain #getBoolean}</li>
 *     <li>{@linkplain #getByte}</li>
 *     <li>{@linkplain #getShort}</li>
 *     <li>{@linkplain #getInteger}</li>
 *     <li>{@linkplain #getLong}</li>
 *     <li>{@linkplain #getFloat}</li>
 *     <li>{@linkplain #getDouble}</li>
 *     <li>{@linkplain #getClassDetails}</li>
 *     <li>{@linkplain #getNestedUsage}</li>
 *     <li>{@linkplain #getList}</li>
 * </ul>
 *
 * @apiNote Abstracts the underlying source of the annotation information,
 * whether that is the {@linkplain Annotation annotation} itself, JAXB, Jandex,
 * HCANN, etc.
 *
 * @author Steve Ebersole
 */
public interface AnnotationUsage<A extends Annotation> {
	/**
	 * Descriptor for the type of the used annotation
	 */
	AnnotationDescriptor<A> getAnnotationDescriptor();

	/**
	 * Type of the used annotation
	 */
	default Class<A> getAnnotationType() {
		return getAnnotationDescriptor().getAnnotationType();
	}

	/**
	 * Create the Annotation representation of this usage
	 */
	A toAnnotation();

	/**
	 * The value of the named annotation attribute
	 */
	<V> V findAttributeValue(String name);

	/**
	 * The value of the named annotation attribute
	 */
	default <V> V getAttributeValue(String name) {
		final Object value = findAttributeValue( name );
		if ( value == null ) {
			// this is unusual.  make sure the attribute exists.
			//		NOTE : the call to #getAttribute throws the exception if it does not
			getAnnotationDescriptor().getAttribute( name );
		}
		//noinspection unchecked
		return (V) value;
	}

	default <V> V getAttributeValue(AttributeDescriptor<V> attributeDescriptor) {
		return getAttributeValue( attributeDescriptor.getName() );
	}

	default String getString(String name) {
		return getAttributeValue( name );
	}

	default Boolean getBoolean(String name) {
		return getAttributeValue( name );
	}

	default Byte getByte(String name) {
		return getAttributeValue( name );
	}

	default Short getShort(String name) {
		return getAttributeValue( name );
	}

	default Integer getInteger(String name) {
		return getAttributeValue( name );
	}

	default Long getLong(String name) {
		return getAttributeValue( name );
	}

	default Float getFloat(String name) {
		return getAttributeValue( name );
	}

	default Double getDouble(String name) {
		return getAttributeValue( name );
	}

	default <E extends Enum<E>> E getEnum(String name) {
		return getAttributeValue( name );
	}

	default <E extends Enum<E>> E getEnum(String name, @SuppressWarnings("unused") Class<E> type) {
		return getAttributeValue( name );
	}

	default ClassDetails getClassDetails(String name) {
		return getAttributeValue( name );
	}

	default <X extends Annotation> AnnotationUsage<X> getNestedUsage(String name) {
		return getAttributeValue( name );
	}

	default <E> List<E> getList(String name) {
		return getAttributeValue( name );
	}

	default void render() {
		final RenderingCollectorImpl renderingCollector = new RenderingCollectorImpl();
		render( renderingCollector );
		renderingCollector.render();
	}

	default void render(RenderingCollector collector) {
		final List<AttributeDescriptor<?>> attributes = getAnnotationDescriptor().getAttributes();
		if ( attributes.isEmpty() ) {
			collector.addLine( "@%s", getAnnotationType().getName() );
		}
		else {
			collector.addLine( "@%s(", getAnnotationType().getName() );
			collector.indent( 2 );
			attributes.forEach( (attribute) -> attribute.getTypeDescriptor().render(
					collector,
					attribute.getName(),
					getAttributeValue( attribute.getName() )
			) );

			collector.unindent( 2 );
			collector.addLine( ")" );
		}
	}
}
