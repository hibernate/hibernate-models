/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;

/**
 * Information which is common across all table annotations
 *
 * @author Steve Ebersole
 */
public interface CommonTableDetails extends DatabaseObjectDetails {
	/**
	 * The table name
	 */
	String name();

	/**
	 * Setter for {@linkplain #name()}
	 */
	void name(String name);

	UniqueConstraint[] uniqueConstraints();
	void uniqueConstraints(UniqueConstraint[] uniqueConstraints);

	Index[] indexes();
	void indexes(Index[] indexes);

	String options();
	void options(String options);
}
