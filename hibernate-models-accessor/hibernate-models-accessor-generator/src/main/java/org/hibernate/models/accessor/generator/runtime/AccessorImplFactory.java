/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.runtime;

import java.lang.reflect.Constructor;

public class AccessorImplFactory {

	private static volatile Constructor<?> readerCtor;
	private static volatile Constructor<?> writerCtor;
	private static volatile Constructor<?> instantiatorCtor;

	public static void init(String readerClass, String writerClass, String instantiatorClass) {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			readerCtor = cl.loadClass( readerClass ).getDeclaredConstructor( int.class, int.class );
			writerCtor = cl.loadClass( writerClass ).getDeclaredConstructor( int.class, int.class );
			instantiatorCtor = cl.loadClass( instantiatorClass ).getDeclaredConstructor( int.class, int.class );
		}
		catch (Exception e) {
			throw new RuntimeException( "Failed to initialize AccessorImplFactory", e );
		}
	}

	public static Object createReader(int classIndex, int memberIndex) {
		try {
			return readerCtor.newInstance( classIndex, memberIndex );
		}
		catch (Exception e) {
			throw new RuntimeException(
					"Failed to create reader for classIndex=" + classIndex + " memberIndex=" + memberIndex, e );
		}
	}

	public static Object createWriter(int classIndex, int memberIndex) {
		try {
			return writerCtor.newInstance( classIndex, memberIndex );
		}
		catch (Exception e) {
			throw new RuntimeException(
					"Failed to create writer for classIndex=" + classIndex + " memberIndex=" + memberIndex, e );
		}
	}

	public static Object createInstantiator(int classIndex, int memberIndex) {
		try {
			return instantiatorCtor.newInstance( classIndex, memberIndex );
		}
		catch (Exception e) {
			throw new RuntimeException(
					"Failed to create instantiator for classIndex=" + classIndex + " memberIndex=" + memberIndex, e );
		}
	}
}
