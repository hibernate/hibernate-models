/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Supplier;

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

	/**
	 * The value of the named annotation attribute
	 */
	default <V> V getAttributeValue(String name, Supplier<V> defaultValueSupplier) {
		final Object attributeValue = getAttributeValue( name );
		if ( attributeValue == null ) {
			return defaultValueSupplier.get();
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

	default String getString(String name, Supplier<String> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Boolean getBoolean(String name) {
		return getAttributeValue( name );
	}

	default Boolean getBoolean(String name, boolean defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Boolean getBoolean(String name, Supplier<Boolean> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Byte getByte(String name) {
		return getAttributeValue( name );
	}

	default Byte getByte(String name, Byte defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Byte getByte(String name, Supplier<Byte> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Short getShort(String name) {
		return getAttributeValue( name );
	}

	default Short getShort(String name, Short defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Short getShort(String name, Supplier<Short> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Integer getInteger(String name) {
		return getAttributeValue( name );
	}

	default Integer getInteger(String name, Integer defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Integer getInteger(String name, Supplier<Integer> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Long getLong(String name) {
		return getAttributeValue( name );
	}

	default Long getLong(String name, Long defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Long getLong(String name, Supplier<Long> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Float getFloat(String name) {
		return getAttributeValue( name );
	}

	default Float getFloat(String name, Float defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Float getFloat(String name, Supplier<Float> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default Double getDouble(String name) {
		return getAttributeValue( name );
	}

	default Double getDouble(String name, Double defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default Double getDouble(String name, Supplier<Double> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default <E extends Enum<E>> E getEnum(String name) {
		return getAttributeValue( name );
	}

	default <E extends Enum<E>> E getEnum(String name, Class<E> type) {
		return getAttributeValue( name );
	}

	default <E extends Enum<E>> E getEnum(String name, E defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default <E extends Enum<E>> E getEnum(String name, Supplier<E> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default <E extends Enum<E>> E getEnum(String name, E defaultValue, Class<E> type) {
		return getAttributeValue( name, defaultValue );
	}

	default <E extends Enum<E>> E getEnum(String name, Supplier<E> defaultValueSupplier, Class<E> type) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default ClassDetails getClassDetails(String name) {
		return getAttributeValue( name );
	}

	default ClassDetails getClassDetails(String name, ClassDetails defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default ClassDetails getClassDetails(String name, Supplier<ClassDetails> defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default <X extends Annotation> AnnotationUsage<X> getNestedUsage(String name) {
		return getAttributeValue( name );
	}

	default <X extends Annotation> AnnotationUsage<X> getNestedUsage(String name, AnnotationUsage<X> defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default <X extends Annotation> AnnotationUsage<X> getNestedUsage(String name, Supplier<AnnotationUsage<X>> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}

	default <E> List<E> getList(String name) {
		return getAttributeValue( name );
	}

	default <E> List<E> getList(String name, List<E> defaultValue) {
		return getAttributeValue( name, defaultValue );
	}

	default <E> List<E> getList(String name, Supplier<List<E>> defaultValueSupplier) {
		return getAttributeValue( name, defaultValueSupplier );
	}
}
