/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * @author Steve Ebersole
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SelfReferencingAnnotation
public @interface SelfReferencingAnnotation {
}
