/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * A classloader that breaks parent delegation for specific classes,
 * defining them in its own context (and therefore its own unnamed module on Java 9+).
 * This simulates the cross-classloader scenario that occurs with bytecode enhancement
 * or application server classloading.
 */
public class IsolatingClassLoader extends ClassLoader {

	private final Set<String> isolatedClassNames;

	public IsolatingClassLoader(Set<String> isolatedClassNames, ClassLoader parent) {
		super( parent );
		this.isolatedClassNames = isolatedClassNames;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if ( !isolatedClassNames.contains( name ) ) {
			return super.loadClass( name, resolve );
		}
		Class<?> alreadyLoaded = findLoadedClass( name );
		if ( alreadyLoaded != null ) {
			return alreadyLoaded;
		}
		String resourceName = name.replace( '.', '/' ) + ".class";
		try ( InputStream is = getParent().getResourceAsStream( resourceName ) ) {
			if ( is == null ) {
				throw new ClassNotFoundException( name );
			}
			byte[] bytes = is.readAllBytes();
			Class<?> defined = defineClass( name, bytes, 0, bytes.length );
			if ( resolve ) {
				resolveClass( defined );
			}
			return defined;
		}
		catch (IOException e) {
			throw new ClassNotFoundException( name, e );
		}
	}
}
