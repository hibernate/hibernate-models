/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import jakarta.persistence.SequenceGenerator;

/**
 * Commonality for database objects
 *
 * @author Steve Ebersole
 * @apiNote While they all have names, some use an attribute named something other
 * than {@code name()} - e.g. {@linkplain SequenceGenerator#sequenceName()}
 */
public interface DatabaseObjectDetails {
	/**
	 * The catalog in which the object exists
	 */
	String catalog();

	/**
	 * Setter for {@linkplain #catalog()}
	 */
	void catalog(String catalog);

	/**
	 * The schema in which the object exists
	 */
	String schema();

	/**
	 * Setter for {@linkplain #schema()}
	 */
	void schema(String schema);
}
