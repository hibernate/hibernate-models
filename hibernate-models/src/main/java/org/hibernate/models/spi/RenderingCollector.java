/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * @author Steve Ebersole
 */
public interface RenderingCollector {
	void addLine(String line);
	void addLine(String pattern, Object... args);
	void addLine();

	void indent(int depth);
	void unindent(int depth);

	default void render() {
		System.out.println( toString() );
	}
}
