/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Base support for SourceModelBuildingContext implementations
 *
 * @author Steve Ebersole
 */
public abstract class AbstractModelBuildingContext implements SourceModelBuildingContext {
	private final ClassLoading classLoadingAccess;

	public AbstractModelBuildingContext(ClassLoading classLoadingAccess) {
		this.classLoadingAccess = classLoadingAccess;
	}

	@Override
	public abstract MutableAnnotationDescriptorRegistry getAnnotationDescriptorRegistry();

	@Override
	public abstract MutableClassDetailsRegistry getClassDetailsRegistry();

	@Override
	public ClassLoading getClassLoading() {
		return classLoadingAccess;
	}

	protected void primeRegistries(RegistryPrimer registryPrimer) {
		BaseLineJavaTypes.forEachJavaType( this::primeClassDetails );

		if ( registryPrimer != null ) {
			registryPrimer.primeRegistries( new RegistryContributions(), this );
		}
	}

	private void primeClassDetails(Class<?> javaType) {
		// Since we have a Class reference already, it is safe to directly use
		// the reflection
		getClassDetailsRegistry().resolveClassDetails( javaType.getName() );
	}

	private <A extends Annotation> void primeAnnotation(AnnotationDescriptor<A> descriptor) {
		getAnnotationDescriptorRegistry().register( descriptor );
		primeClassDetails( descriptor.getAnnotationType() );
	}

	private class RegistryContributions implements RegistryPrimer.Contributions {
		@Override
		public <A extends Annotation> void registerAnnotation(AnnotationDescriptor<A> descriptor) {
			primeAnnotation( descriptor );
		}

		@Override
		public void registerClass(ClassDetails details) {
			getClassDetailsRegistry().addClassDetails( details );
		}
	}
}
