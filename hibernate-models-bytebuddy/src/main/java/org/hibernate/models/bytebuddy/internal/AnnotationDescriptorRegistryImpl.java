/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationDescriptorRegistryImpl extends AnnotationDescriptorRegistryStandard {
	public AnnotationDescriptorRegistryImpl(SourceModelBuildingContext modelBuildingContext) {
		super( modelBuildingContext );
	}

	public Map<Class<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> getDescriptorMap() {
		return descriptorMap;
	}
}
