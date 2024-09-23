/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.Type;

import org.hibernate.models.spi.TypeDetails;

/**
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface JdkTypeSwitcher {
	TypeDetails switchType(Type type);
}
