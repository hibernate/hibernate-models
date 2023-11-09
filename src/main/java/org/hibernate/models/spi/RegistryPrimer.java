/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

/**
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface RegistryPrimer {
	void primeRegistries(Contributions contributions, SourceModelBuildingContext buildingContext);

	interface Contributions {
		<A extends Annotation> void registerAnnotation(AnnotationDescriptor<A> descriptor);

		void registerClass(ClassDetails details);
	}
}
