/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptorRegistry extends AnnotationDescriptorRegistry {
	void register(AnnotationDescriptor<?> descriptor);
}
