/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.rendering.spi;

/**
 * Abstraction over the target of rendering (streams, buffers, ...).
 *
 * @author Steve Ebersole
 */
public interface RenderingTarget {
	/**
	 * Adds the string, preceded by any necessary indentation, followed by a newline.
	 *
	 * @see #indent
	 * @see #unindent
	 */
	void addLine(String line);

	/**
	 * Adds the rendered pattern, preceded by any necessary indentation, followed by a newline.
	 *
	 * @see #indent
	 * @see #unindent
	 */
	void addLine(String pattern, Object... args);

	/**
	 * Adds a newline.
	 */
	void addLine();

	/**
	 * Increase the indentation by the given factor.
	 *
	 * @apiNote Indentation depth is constant and controlled by the target itself.  {@code depth} here
	 * indicates the number of indentations by which to increase.
	 */
	void indent(int depth);

	/**
	 * Decrease the indentation by the given factor.
	 *
	 * @apiNote Indentation depth is constant and controlled by the target itself.  {@code depth} here
	 * indicates the number of indentations by which to decrease.
	 */
	void unindent(int depth);
}
