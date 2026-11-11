/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Locale;

import jakarta.annotation.Nonnull;

/**
 * Context object for hibernate-models.
 * Basically support for accessing the {@linkplain #getClassDetailsRegistry() ClassDetails registry}
 * and the {@linkplain #getAnnotationDescriptorRegistry() AnnotationDescriptor registry}.
 * Additionally, defines support for {@linkplain #as treat-style casting}.
 *
 * @author Steve Ebersole
 */
public interface ModelsContext {
	/**
	 * The registry of annotation descriptors.
	 */
	@Nonnull
	AnnotationDescriptorRegistry getAnnotationDescriptorRegistry();

	/**
	 * Registry of managed-classes.
	 */
	@Nonnull
	ClassDetailsRegistry getClassDetailsRegistry();

	/**
	 * Registry of modules.
	 */
	@Nonnull
	ModuleDetailsRegistry getModuleDetailsRegistry();

	/**
	 * Access to {@linkplain ClassLoader} operations.
	 */
	@Nonnull
	ClassLoading getClassLoading();

	/**
	 * Treat support.
	 */
	@Nonnull
	default <S> S as(@Nonnull Class<S> type) {
		if ( type.isInstance( this ) ) {
			return type.cast( this );
		}

		if ( type.isInstance( getClassDetailsRegistry() ) ) {
			return type.cast( getClassDetailsRegistry() );
		}

		if ( type.isInstance( getModuleDetailsRegistry() ) ) {
			return type.cast( getModuleDetailsRegistry() );
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

}
