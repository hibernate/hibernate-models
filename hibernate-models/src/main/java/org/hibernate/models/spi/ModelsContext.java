/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Locale;

import org.hibernate.models.Incubating;
import org.hibernate.models.accessor.HibernateAccessorFactory;
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
	 * Access to the {@link HibernateAccessorFactory} used for creating field/method
	 * readers, writers, and class instantiators.
	 *
	 * <p>Defaults to a reflection-based factory. Implementations may override
	 * to provide alternative strategies (e.g. a factory backed by generated accessors).
	 */
	@Incubating
	default HibernateAccessorFactory getAccessorFactory() {
		return HibernateAccessorFactory.reflection();
	}

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

		if ( type.isInstance( getAccessorFactory() ) ) {
			return type.cast( getAccessorFactory() );
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
