/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationDescriptor;

/**
 * @author Steve Ebersole
 */
public interface SerialAnnotationDescriptor<A extends Annotation> extends StorableForm<AnnotationDescriptor<A>> {

}
