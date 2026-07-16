/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.generator;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.accessor.generator.AccessorClassMetadata;
import org.hibernate.models.accessor.generator.AccessorClassMetadata.MultiValueGroupMetadata;
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

		List<MultiValueGroupMetadata> readerGroups = buildReaderGroups();
		List<MultiValueGroupMetadata> writerGroups = buildWriterGroups();

		AccessorGenerator.GenerationResult result = AccessorGenerator.generate( inputs, readerGroups, writerGroups );

		for ( GeneratedClassResult generated : result.generatedClasses() ) {
			writeClass( outputDir, generated.className(), generated.bytecode() );
		}

		for ( GeneratedClassResult transformed : result.transformedClasses() ) {
			writeClass( outputDir, transformed.className(), transformed.bytecode() );
		}

		System.out.println( "Generated " + result.generatedClasses().size() + " classes, transformed "
				+ result.transformedClasses().size() + " host classes to " + outputDir );
	}

	private static List<MultiValueGroupMetadata> buildReaderGroups() throws Exception {
		List<MultiValueGroupMetadata> groups = new ArrayList<>();

		// MultiValueAccessTest groups
		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		Field longField = PrimitiveFieldBean.class.getDeclaredField( "longField" );
		Field doubleField = PrimitiveFieldBean.class.getDeclaredField( "doubleField" );
		Field booleanField = PrimitiveFieldBean.class.getDeclaredField( "booleanField" );
		Field charField = PrimitiveFieldBean.class.getDeclaredField( "charField" );
		Method getLong = PrimitiveFieldBean.class.getDeclaredMethod( "getLongField" );
		Method getDouble = PrimitiveFieldBean.class.getDeclaredMethod( "getDoubleField" );

		groups.add( MultiValueGroupMetadata.readerGroup( PrimitiveFieldBean.class, intField, longField, doubleField ) );
		groups.add( MultiValueGroupMetadata.readerGroup( PrimitiveFieldBean.class, intField, getLong, booleanField ) );
		groups.add( MultiValueGroupMetadata.readerGroup( PrimitiveFieldBean.class, intField, getDouble, charField ) );

		// MultiValueInheritanceTest groups
		Field parentField = ParentBean.class.getDeclaredField( "parentField" );
		Field childField = ChildBean.class.getDeclaredField( "childField" );
		Method getChild = ChildBean.class.getDeclaredMethod( "getChildField" );

		groups.add( MultiValueGroupMetadata.readerGroup( ChildBean.class, parentField, childField ) );
		groups.add( MultiValueGroupMetadata.readerGroup( ChildBean.class, parentField, getChild ) );

		return groups;
	}

	private static List<MultiValueGroupMetadata> buildWriterGroups() throws Exception {
		List<MultiValueGroupMetadata> groups = new ArrayList<>();

		// MultiValueAccessTest groups
		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		Field longField = PrimitiveFieldBean.class.getDeclaredField( "longField" );
		Field booleanField = PrimitiveFieldBean.class.getDeclaredField( "booleanField" );
		Field charField = PrimitiveFieldBean.class.getDeclaredField( "charField" );
		Method setLong = PrimitiveFieldBean.class.getDeclaredMethod( "setLongField", long.class );
		Method setDouble = PrimitiveFieldBean.class.getDeclaredMethod( "setDoubleField", double.class );

		groups.add( MultiValueGroupMetadata.writerGroup( PrimitiveFieldBean.class, intField, longField ) );
		groups.add( MultiValueGroupMetadata.writerGroup( PrimitiveFieldBean.class, intField, setLong, booleanField ) );
		groups.add( MultiValueGroupMetadata.writerGroup( PrimitiveFieldBean.class, intField, setDouble, charField ) );

		// MultiValueInheritanceTest groups
		Field parentField = ParentBean.class.getDeclaredField( "parentField" );
		Field childField = ChildBean.class.getDeclaredField( "childField" );
		Method setParent = ParentBean.class.getDeclaredMethod( "setParentField", String.class );

		groups.add( MultiValueGroupMetadata.writerGroup( ChildBean.class, parentField, childField ) );
		groups.add( MultiValueGroupMetadata.writerGroup( ChildBean.class, setParent, childField ) );

		return groups;
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
