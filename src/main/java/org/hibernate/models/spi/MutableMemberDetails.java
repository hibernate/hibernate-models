/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Union of MemberDetails and MutableAnnotationTarget
 *
 * @author Steve Ebersole
 */
public interface MutableMemberDetails extends MemberDetails, MutableAnnotationTarget {
}
