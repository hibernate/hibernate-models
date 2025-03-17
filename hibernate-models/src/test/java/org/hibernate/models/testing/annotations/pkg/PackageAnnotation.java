/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.annotations.pkg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.testing.annotations.CustomMetaAnnotation;

/**
 * @author Steve Ebersole
 */
@Target( ElementType.PACKAGE )
@Retention( RetentionPolicy.RUNTIME )
@CustomMetaAnnotation( someValue = "abc" )
public @interface PackageAnnotation {
}
