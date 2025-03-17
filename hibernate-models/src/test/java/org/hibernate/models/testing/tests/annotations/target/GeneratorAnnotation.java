/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations.target;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Let's mimic a Hibernate/JPA id generator annotation
 *
 * @author Steve Ebersole
 */
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratorAnnotation {
	enum Source { PACKAGE, TYPE, MEMBER }
	Source value();
}
