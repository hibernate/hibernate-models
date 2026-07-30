/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.spi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HibernateAccessorBytecodeDumper {

	private final Path dumpDir;

	public HibernateAccessorBytecodeDumper(HibernateAccessorConfiguration config) {
		final String dir = config.getProperty( HibernateAccessorConfiguration.DUMP_BYTECODE_DIR, String.class );
		this.dumpDir = dir != null ? Path.of( dir ) : null;
	}

	public void dump(String classInternalName, byte[] bytecode) {
		if ( dumpDir == null ) {
			return;
		}
		final Path classFile = dumpDir.resolve( classInternalName + ".class" );
		try {
			Files.createDirectories( classFile.getParent() );
			Files.write( classFile, bytecode );
		}
		catch (IOException e) {
			throw new RuntimeException( "Failed to dump bytecode to " + classFile, e );
		}
	}
}
