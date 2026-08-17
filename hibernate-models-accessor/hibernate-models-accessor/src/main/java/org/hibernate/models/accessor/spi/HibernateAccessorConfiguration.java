/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.spi;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class HibernateAccessorConfiguration {

	public static final HibernateAccessorConfiguration EMPTY = new HibernateAccessorConfiguration( Map.of() );

	public static final String LOOKUP = "hibernate.models.accessor.lookup";
	public static final String DUMP_BYTECODE_DIR = "hibernate.models.accessor.bytecode.dump.dir";

	private final Map<String, Object> properties;

	public HibernateAccessorConfiguration(Map<String, Object> properties) {
		this.properties = properties;
	}

	public HibernateAccessorConfiguration(MethodHandles.Lookup lookup) {
		this( Map.of( LOOKUP, lookup ) );
	}

	public HibernateAccessorConfiguration(MethodHandles.Lookup lookup, Map<String, Object> properties) {
		final Map<String, Object> merged = new HashMap<>( properties );
		merged.put( LOOKUP, lookup );
		this.properties = merged;
	}

	public MethodHandles.Lookup lookup() {
		return getProperty( LOOKUP, MethodHandles.Lookup.class );
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(String name, Class<T> type) {
		final Object value = properties.get( name );
		if ( value == null ) {
			return null;
		}
		if ( type == String.class && !(value instanceof String) ) {
			return (T) value.toString();
		}
		return type.cast( value );
	}

	public <T> T getProperty(String name, Class<T> type, T defaultValue) {
		final T value = getProperty( name, type );
		return value != null ? value : defaultValue;
	}
}
