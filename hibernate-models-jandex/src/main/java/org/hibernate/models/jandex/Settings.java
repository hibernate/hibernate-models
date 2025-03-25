/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex;

/**
 * Settings for hibernate-models Jandex support
 *
 * @author Steve Ebersole
 */
public interface Settings {
	/**
	 * Used to pass the Jandex {@linkplain org.jboss.jandex.IndexView index}.
	 */
	String INDEX_PARAM = "hibernate.models.jandex.index";
}
