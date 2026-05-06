/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex;

import org.hibernate.models.spi.ClassDetails;

/// Settings for hibernate-models Jandex support
///
/// @author Steve Ebersole
public interface Settings {

	/// Used to pass the Jandex {@linkplain org.jboss.jandex.IndexView index}.
	String INDEX_PARAM = "hibernate.models.jandex.index";

	/// Indicates how to handle requests to resolve a [org.hibernate.models.spi.ClassDetails]
	/// from [org.hibernate.models.jandex.internal.JandexClassDetailsRegistry] when that class
	/// does not exist in the Jandex [Index][#INDEX_PARAM].  Supported values are defined by [FallbackStrategy].
	String FALLBACK = "hibernate.models.jandex.fallback";
}
