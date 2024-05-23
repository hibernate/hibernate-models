/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/**
 * A de-typed abstraction over "reflection" and annotations (and in the case of Hibernate ORM,
 * mapping XML also).
 * <p/>
 * Uses a mix of <a href="https://smallrye.io/jandex/">Jandex</a> and Java reflection to build a
 * de-typed abstraction model of classes and annotations referenced by an application's
 * managed resources.
 * <p/>
 * Consumers can then access details from that abstraction model in a unified way, regardless of the
 * underlying source (Jandex or reflection).  For classes which we are able to access from a Jandex index,
 * this has the benefit that the classes are not loaded into the ClassLoader; which is important because
 * once a classes is loaded into a ClassLoader, its bytecode cannot be changed and run-time bytecode
 * enhancement is not possible.
 * <p/>
 * This work is intended to replace the
 * <a href="https://github.com/hibernate/hibernate-commons-annotations">hibernate-commons-annotation</a>
 * library, which suffered from a number of shortcomings.
 * <p/>
 * This abstraction model is largely a mirror of {@link java.lang.Class}, {@link java.lang.reflect.Field},
 * {@link java.lang.reflect.Method} and {@link java.lang.annotation.Annotation}.  The 2 main reasons for
 * this are -<ol>
 *     <li>
 *         Allows for "dynamic models"
 *     </li>
 *     <li>
 *         Avoids prematurely loading these references into the {@link java.lang.ClassLoader}.
 *     </li>
 * </ol>
 *
 * @see org.hibernate.models.spi.ClassDetails
 * @see org.hibernate.models.spi.FieldDetails
 * @see org.hibernate.models.spi.MethodDetails
 * @see org.hibernate.models.spi.AnnotationDescriptor
 * @see org.hibernate.models.spi.AttributeDescriptor
 * @see org.hibernate.models.spi.AnnotationDescriptorRegistry
 * @see org.hibernate.models.spi.ClassDetailsRegistry
 *
 * @author Steve Ebersole
 */
@Incubating
package org.hibernate.models;
