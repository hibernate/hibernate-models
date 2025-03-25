/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.type.TypeDescription;


/**
 * Support for converting Class values
 *
 * @author Steve Ebersole
 */
public class ClassValueConverter implements ValueConverter<Class<?>> {
	public static final ClassValueConverter CLASS_VALUE_WRAPPER = new ClassValueConverter();

	@Override
	public Class<?> convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		final TypeDescription typeDescription = byteBuddyValue.resolve( TypeDescription.class );
		final String typeName = typeDescription.getName();
		if ( "void".equals( typeName ) ) {
			return void.class;
		}
		return modelContext.getClassLoading().classForName( typeDescription.getTypeName() );
//		return byteBuddyValue.resolve( Class.class );
	}
}
