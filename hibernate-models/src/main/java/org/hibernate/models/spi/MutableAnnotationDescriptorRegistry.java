/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * @author Steve Ebersole
 */
public interface MutableAnnotationDescriptorRegistry extends AnnotationDescriptorRegistry {
	void register(AnnotationDescriptor<?> descriptor);
}
