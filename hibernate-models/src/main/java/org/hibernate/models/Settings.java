/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import org.hibernate.models.spi.ClassDetailsRegistry;

/**
 * @author Steve Ebersole
 */
public interface Settings {
	/**
	 * Controls whether to track {@linkplain ClassDetailsRegistry#getDirectImplementors implementors}.
	 * This tracking has the potential to grow quite large, so this setting allows to
	 * enable/disable it.  By default, this is {@code false}.
	 */
	@Incubating
	String TRACK_IMPLEMENTORS = "hibernate.models.trackImplementors";
}
