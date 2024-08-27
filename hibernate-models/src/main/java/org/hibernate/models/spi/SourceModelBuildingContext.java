/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Context object used while building references for {@link AnnotationDescriptor},
 * {@link ClassDetails} and friends.
 *
 * @author Steve Ebersole
 */
public interface SourceModelBuildingContext extends SourceModelContext { //, SharedNamedAnnotationScope {
	/**
	 * If model processing code needs to load things from the class-loader, they should
	 * really use this access.  At this level, accessing the class-loader at all
	 * sh
	 */
	ClassLoading getClassLoading();

	default <S> S as(Class<S> type) {
		//noinspection unchecked
		return (S) this;
	}
}
