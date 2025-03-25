/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.util;

import java.lang.annotation.Annotation;

import org.hibernate.models.ModelsException;
import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.internal.EnumTypeDescriptor;
import org.hibernate.models.internal.NestedTypeDescriptor;
import org.hibernate.models.spi.ValueTypeDescriptor;

import static org.hibernate.models.internal.BooleanTypeDescriptor.BOOLEAN_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.ByteTypeDescriptor.BYTE_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.CharacterTypeDescriptor.CHARACTER_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.ClassTypeDescriptor.CLASS_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.DoubleTypeDescriptor.DOUBLE_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.FloatTypeDescriptor.FLOAT_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.IntegerTypeDescriptor.INTEGER_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.LongTypeDescriptor.LONG_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.ShortTypeDescriptor.SHORT_TYPE_DESCRIPTOR;
import static org.hibernate.models.internal.StringTypeDescriptor.STRING_TYPE_DESCRIPTOR;

/**
 * Helper for resolving ValueTypeDescriptor for an annotation attribute.
 *
 * @author Steve Ebersole
 */
public class AnnotationAttributeTypeHelper {

	@SuppressWarnings("unchecked")
	public static <T,W> ValueTypeDescriptor<W> resolveTypeDescriptor(Class<T> attributeType) {
		assert attributeType != null;

		if ( attributeType == byte.class ) {
			return (ValueTypeDescriptor<W>) BYTE_TYPE_DESCRIPTOR;
		}

		if ( attributeType == boolean.class ) {
			return (ValueTypeDescriptor<W>) BOOLEAN_TYPE_DESCRIPTOR;
		}

		if ( attributeType == short.class ) {
			return (ValueTypeDescriptor<W>) SHORT_TYPE_DESCRIPTOR;
		}

		if ( attributeType == int.class ) {
			return (ValueTypeDescriptor<W>) INTEGER_TYPE_DESCRIPTOR;
		}

		if ( attributeType == long.class ) {
			return (ValueTypeDescriptor<W>) LONG_TYPE_DESCRIPTOR;
		}

		if ( attributeType == float.class ) {
			return (ValueTypeDescriptor<W>) FLOAT_TYPE_DESCRIPTOR;
		}

		if ( attributeType == double.class ) {
			return (ValueTypeDescriptor<W>) DOUBLE_TYPE_DESCRIPTOR;
		}

		if ( attributeType == char.class ) {
			return (ValueTypeDescriptor<W>) CHARACTER_TYPE_DESCRIPTOR;
		}

		if ( attributeType == String.class ) {
			return (ValueTypeDescriptor<W>) STRING_TYPE_DESCRIPTOR;
		}

		if ( attributeType == Class.class ) {
			return (ValueTypeDescriptor<W>) CLASS_TYPE_DESCRIPTOR;
		}

		if ( attributeType.isArray() ) {
			final Class<?> componentType = attributeType.getComponentType();
			final ValueTypeDescriptor<?> elementTypeDescriptor = resolveTypeDescriptor( componentType );
			return (ValueTypeDescriptor<W>) new ArrayTypeDescriptor<>( elementTypeDescriptor );
		}

		if ( attributeType.isEnum() ) {
			//noinspection rawtypes
			return new EnumTypeDescriptor<>( (Class) attributeType );
		}

		if ( Annotation.class.isAssignableFrom( attributeType ) ) {
			return (ValueTypeDescriptor<W>) new NestedTypeDescriptor<>( (Class<? extends Annotation>) attributeType );
		}

		throw new ModelsException( "Unsupported attribute value type - " + attributeType.getName() );
	}
}
