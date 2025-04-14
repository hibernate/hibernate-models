/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Locale;

import org.hibernate.models.serial.spi.StorableContext;

/**
 * Context object for hibernate-models.
 * Basically support for accessing the {@linkplain #getClassDetailsRegistry() ClassDetails registry}
 * and the {@linkplain #getAnnotationDescriptorRegistry() AnnotationDescriptor registry}.
 * Additionally, defines support for {@linkplain #as treat-style casting}
 * and {@linkplain #toStorableForm() serialization}.
 *
 * @author Steve Ebersole
 */
public interface ModelsContext {
	/**
	 * The registry of annotation descriptors.
	 */
	AnnotationDescriptorRegistry getAnnotationDescriptorRegistry();

	/**
	 * Registry of managed-classes.
	 */
	ClassDetailsRegistry getClassDetailsRegistry();

	/**
	 * Access to {@linkplain ClassLoader} operations.
	 */
	ClassLoading getClassLoading();

	/**
	 * Treat support.
	 */
	default <S> S as(Class<S> type) {
		if ( type.isInstance( this ) ) {
			return type.cast( this );
		}

		if ( type.isInstance( getClassDetailsRegistry() ) ) {
			return type.cast( getClassDetailsRegistry() );
		}

		if ( type.isInstance( getAnnotationDescriptorRegistry() ) ) {
			return type.cast( getAnnotationDescriptorRegistry() );
		}

		if ( type.isInstance( getClassLoading() ) ) {
			return type.cast(getClassLoading() );
		}

		throw new UnsupportedOperationException(
				String.format(
						Locale.ROOT,
						"Cannot treat ModelsContext(%s) as `%s`",
						this,
						type.getName()
				)
		);
	}

	/**
	 * Serialization support.
	 */
	StorableContext toStorableForm();
}
