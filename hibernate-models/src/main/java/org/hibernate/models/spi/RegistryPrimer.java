/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;

/**
 * Integration contract allowing initialization priming of {@linkplain ClassDetailsRegistry}
 * and {@linkplain AnnotationDescriptorRegistry}.
 *
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface RegistryPrimer {
	void primeRegistries(@Nonnull Contributions contributions, @Nonnull ModelsContext modelsContext);

	interface Contributions {
		/**
		 * Register an annotation descriptor
		 */
		<A extends Annotation> void registerAnnotation(@Nonnull AnnotationDescriptor<A> descriptor);

		/**
		 * Register a class descriptor
		 */
		void registerClass(@Nonnull ClassDetails details);
	}
}
