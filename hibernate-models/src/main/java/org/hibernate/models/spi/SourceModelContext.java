/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.io.Serializable;

/**
 * @author Steve Ebersole
 */
public interface SourceModelContext extends Serializable {
	/**
	 * The registry of annotation descriptors
	 */
	AnnotationDescriptorRegistry getAnnotationDescriptorRegistry();

	/**
	 * Registry of managed-classes
	 */
	ClassDetailsRegistry getClassDetailsRegistry();
}
