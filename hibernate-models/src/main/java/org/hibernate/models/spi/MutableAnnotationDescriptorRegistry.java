/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptorRegistry extends AnnotationDescriptorRegistry {
	void register(@Nonnull AnnotationDescriptor<?> descriptor);
}
