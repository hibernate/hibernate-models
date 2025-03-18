/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy;

/**
 * Settings for hibernate-models ByteBuddy support
 *
 * @author Steve Ebersole
 */
public interface Settings {
	/**
	 * Used to pass the ByteBuddy {@linkplain net.bytebuddy.pool.TypePool}.
	 */
	String TYPE_POOL_PARAM = "hibernate.models.bytebuddy.typePool";
}
