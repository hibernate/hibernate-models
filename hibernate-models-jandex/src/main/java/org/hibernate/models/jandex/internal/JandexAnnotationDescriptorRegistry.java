/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.spi.ModelsContext;

/**
 * AnnotationDescriptorRegistry implementation based on Jandex.
 *
 * @author Steve Ebersole
 */
public class JandexAnnotationDescriptorRegistry extends AnnotationDescriptorRegistryStandard {
	public JandexAnnotationDescriptorRegistry(ModelsContext modelsContext) {
		super( modelsContext );
	}
}
