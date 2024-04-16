/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Support for extracting values from {@linkplain java.lang.annotation.Annotation JDK}
 * and {@linkplain org.jboss.jandex.AnnotationInstance Jandex} sources.
 * <p/>
 * Supported value types are generally one of <ul>
 *     <li>{@linkplain Byte}</li>
 *     <li>{@linkplain Boolean}</li>
 *     <li>{@linkplain Short}</li>
 *     <li>{@linkplain Integer}</li>
 *     <li>{@linkplain Long}</li>
 *     <li>{@linkplain Float}</li>
 *     <li>{@linkplain Double}</li>
 *     <li>{@linkplain Enum}</li>
 *     <li>{@linkplain AnnotationUsage}</li>
 *     <li>{@linkplain ClassDetails}</li>
 * </ul>
 *
 * @param <S> The annotation source
 * @param <V> The value type
 *
 * @author Steve Ebersole
 */
public interface ValueExtractor<S, V> {
	/**
	 * Extract the value of the named attribute from the given annotation
	 */
	V extractValue(S annotation, String attributeName, SourceModelBuildingContext buildingContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	default V extractValue(
			S annotation,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext buildingContext) {
		return extractValue( annotation, attributeDescriptor.getName(), buildingContext );
	}
}
