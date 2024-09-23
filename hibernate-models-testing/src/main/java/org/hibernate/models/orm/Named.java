/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

/**
 * @author Steve Ebersole
 */
public interface Named {
	String name();
	void name(String name);
}
