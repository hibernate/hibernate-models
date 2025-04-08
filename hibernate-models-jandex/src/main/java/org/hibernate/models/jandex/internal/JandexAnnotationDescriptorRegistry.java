/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class JandexAnnotationDescriptorRegistry extends AnnotationDescriptorRegistryStandard {
	public JandexAnnotationDescriptorRegistry(SourceModelBuildingContext modelBuildingContext) {
		super( modelBuildingContext );
	}
}
