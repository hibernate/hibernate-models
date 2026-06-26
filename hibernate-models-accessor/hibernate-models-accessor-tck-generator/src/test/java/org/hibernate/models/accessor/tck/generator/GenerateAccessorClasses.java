/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.accessor.generator.AccessorClassMetadata;
import org.hibernate.models.accessor.generator.AccessorGenerator;
import org.hibernate.models.accessor.generator.GeneratedClassResult;
import org.hibernate.models.accessor.tck.tests.beans.PrimitiveFieldBean;
import org.hibernate.models.accessor.tck.tests.beans.SimpleRecord;
import org.hibernate.models.accessor.tck.tests.beans.inheritance.ChildBean;
import org.hibernate.models.accessor.tck.tests.beans.inheritance.ParentBean;
import org.hibernate.models.accessor.tck.tests.beans.visibility.PropertyVisibilityBean;
import org.hibernate.models.accessor.tck.tests.beans.visibility.Util;
import org.hibernate.models.accessor.tck.tests.interfacemethod.GreetingServiceImpl;

public class GenerateAccessorClasses {

	private static final Class<?>[] BEAN_CLASSES = {
			PrimitiveFieldBean.class,
			SimpleRecord.class,
			ChildBean.class,
			ParentBean.class,
			PropertyVisibilityBean.class,
			GreetingServiceImpl.class,
	};

	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			throw new IllegalArgumentException( "Usage: GenerateAccessorClasses <outputDir>" );
		}
		Path outputDir = Path.of( args[0] );

		List<AccessorGenerator.GenerationInput> inputs = new ArrayList<>();

		for ( Class<?> beanClass : BEAN_CLASSES ) {
			AccessorClassMetadata metadata = AccessorClassMetadata.Builder.forClass( beanClass )
					.all( beanClass )
					.build();
			byte[] bytecode = readClassBytes( beanClass );
			inputs.add( new AccessorGenerator.GenerationInput( metadata, bytecode ) );
		}

		// Also handle PackagePrivateBean via Util (can't reference directly from another package)
		Class<?> packagePrivateBean = Util.packagePrivateBeanInstance().getClass();
		AccessorClassMetadata ppMetadata = AccessorClassMetadata.Builder.forClass( packagePrivateBean )
				.all( packagePrivateBean )
				.build();
		inputs.add( new AccessorGenerator.GenerationInput( ppMetadata, readClassBytes( packagePrivateBean ) ) );

		AccessorGenerator.GenerationResult result = AccessorGenerator.generate( inputs );

		for ( GeneratedClassResult generated : result.generatedClasses() ) {
			writeClass( outputDir, generated.className(), generated.bytecode() );
		}

		for ( GeneratedClassResult transformed : result.transformedClasses() ) {
			writeClass( outputDir, transformed.className(), transformed.bytecode() );
		}

		System.out.println( "Generated " + result.generatedClasses().size() + " classes, transformed "
				+ result.transformedClasses().size() + " host classes to " + outputDir );
	}

	private static byte[] readClassBytes(Class<?> clazz) throws IOException {
		String resourceName = "/" + clazz.getName().replace( '.', '/' ) + ".class";
		try ( InputStream is = clazz.getResourceAsStream( resourceName ) ) {
			if ( is == null ) {
				throw new IOException( "Cannot find class resource: " + resourceName );
			}
			return is.readAllBytes();
		}
	}

	private static void writeClass(Path outputDir, String className, byte[] bytecode) throws IOException {
		Path classFile = outputDir.resolve( className.replace( '.', '/' ) + ".class" );
		Files.createDirectories( classFile.getParent() );
		Files.write( classFile, bytecode );
	}
}
