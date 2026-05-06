/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex;

import java.util.Map;

/// Indicates how to handle requests to resolve a [org.hibernate.models.spi.ClassDetails]
/// from [org.hibernate.models.jandex.internal.JandexClassDetailsRegistry] when that class
/// does not exist in the Jandex [Index][#INDEX_PARAM].
///
/// @see Settings#FALLBACK
///
/// @author Steve Ebersole
public enum FallbackStrategy {
	/// (default) Fallback to using [org.hibernate.models.internal.jdk.JdkBuilders]
	JDK,
	/// No fallback - throw a [NotInJandexException] in such a case.
	NONE;

	public static FallbackStrategy pickStrategy(Map<Object, Object> configProperties) {
		if ( configProperties != null ) {
			var setting = configProperties.get( Settings.FALLBACK );
			if ( setting != null ) {
				return fromSetting( setting );
			}
		}
		return JDK;
	}

	public static FallbackStrategy fromSetting(Object setting) {
		if ( setting instanceof FallbackStrategy fs ) {
			return fs;
		}

		return Enum.valueOf( FallbackStrategy.class, String.valueOf( setting ) );
	}
}
