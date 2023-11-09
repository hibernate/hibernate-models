/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Describes the usage of an annotation.  That is, not the
 * {@linkplain AnnotationDescriptor annotation class} itself, but
 * rather a particular usage of the annotation on one of its
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
	 * Type of the used annotation
	 */
	Class<A> getAnnotationType();

	/**
	 * The target where this usage occurs
	 */
	AnnotationTarget getAnnotationTarget();

	/**
	 * The value of the named annotation attribute
	 */
	<V> V getAttributeValue(String name);

	/**
	 * The value of the named annotation attribute
	 */
	default <V> V getAttributeValue(String name, V defaultValue) {
		final Object attributeValue = getAttributeValue( name );
		if ( attributeValue == null ) {
			return defaultValue;
		}
		//noinspection unchecked
		return (V) attributeValue;
	}

	default <V> V getAttributeValue(AttributeDescriptor<V> attributeDescriptor) {
		return getAttributeValue( attributeDescriptor.getName() );
	}

	default String getString(String name) {
		return getAttributeValue( name );
	}

	default String getString(String name, String defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Boolean getBoolean(String name) {
		return getAttributeValue( name );
	}

	default Boolean getBoolean(String name, boolean defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Byte getByte(String name) {
		return getAttributeValue( name );
	}

	default Byte getByte(String name, Byte defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Short getShort(String name) {
		return getAttributeValue( name );
	}

	default Short getShort(String name, Short defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Integer getInteger(String name) {
		return getAttributeValue( name );
	}

	default Integer getInteger(String name, Integer defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Long getLong(String name) {
		return getAttributeValue( name );
	}

	default Long getLong(String name, Long defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Float getFloat(String name) {
		return getAttributeValue( name );
	}

	default Float getFloat(String name, Float defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Double getDouble(String name) {
		return getAttributeValue( name );
	}

	default Double getDouble(String name, Double defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default <E extends Enum<E>> E getEnum(String name) {
		return getAttributeValue( name );
	}

	default <E extends Enum<E>> E getEnum(String name, E defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default ClassDetails getClassDetails(String name) {
		return getAttributeValue( name );
	}

	default ClassDetails getClassDetails(String name, ClassDetails defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default <X extends Annotation> AnnotationUsage<X> getNestedUsage(String name) {
		return getAttributeValue( name );
	}

	default <E> List<E> getList(String name) {
		return getAttributeValue( name );
	}
}
