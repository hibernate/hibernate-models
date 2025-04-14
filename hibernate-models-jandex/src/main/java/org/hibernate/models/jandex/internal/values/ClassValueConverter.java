/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.Type;
import org.jboss.jandex.VoidType;

/**
 * Wraps AnnotationValue as a class
 *
 * @author Steve Ebersole
 */
public class ClassValueConverter implements JandexValueConverter<Class<?>> {
	public static final ClassValueConverter JANDEX_CLASS_VALUE_WRAPPER = new ClassValueConverter();

	@Override
	public Class<?> convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		final Type classReference = jandexValue.asClass();
		if ( classReference == VoidType.VOID ) {
			return void.class;
		}
		return modelContext.getClassLoading().classForName( classReference.name().toString() );
	}
}
