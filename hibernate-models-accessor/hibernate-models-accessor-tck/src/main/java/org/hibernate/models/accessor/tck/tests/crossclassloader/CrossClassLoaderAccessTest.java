/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.crossclassloader;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.visibility.PropertyVisibilityBean;
import org.hibernate.models.accessor.tck.util.IsolatingClassLoader;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests accessor access to classes loaded by a different classloader.
 * <p>
 * On Java 9+, each ClassLoader gets its own unnamed module.
 * When the accessor factory's lookup is scoped to one unnamed module
 * and the target class lives in another (because it was loaded by a
 * different classloader), {@code privateLookupIn} can fail due to
 * cross-module access restrictions. This is the scenario that occurs
 * with bytecode enhancement classloaders or application server
 * classloading.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Cross-classloader accessor access")
public class CrossClassLoaderAccessTest {

	private static final String BEAN_CLASS_NAME = PropertyVisibilityBean.class.getName();

	private HibernateAccessorFactory factory;
	private Class<?> isolatedClass;
	private Object isolatedInstance;

	@BeforeAll
	void setup() throws Exception {
		factory = TckHelper.factory();

		IsolatingClassLoader isolatingLoader = new IsolatingClassLoader(
				Set.of( BEAN_CLASS_NAME ),
				getClass().getClassLoader()
		);
		isolatedClass = isolatingLoader.loadClass( BEAN_CLASS_NAME );

		// Verify the class is genuinely from a different classloader/module
		assertNotSame( PropertyVisibilityBean.class, isolatedClass,
				"Isolated class must be a distinct Class object loaded by a different classloader" );
		assertNotSame( PropertyVisibilityBean.class.getModule(), isolatedClass.getModule(),
				"Isolated class must be in a different (unnamed) module" );

		Constructor<?> ctor = isolatedClass.getDeclaredConstructor();
		ctor.setAccessible( true );
		isolatedInstance = ctor.newInstance();
	}

	@Test
	@DisplayName("Instantiate class from foreign classloader")
	void testInstantiation() throws Exception {
		Constructor<?> ctor = isolatedClass.getDeclaredConstructor();
		ctor.setAccessible( true );
		HibernateAccessorInstantiator<?> instantiator = factory.instantiator( ctor );

		Object instance = instantiator.create();
		assertNotNull( instance );
		assertEquals( isolatedClass, instance.getClass() );
	}

	@Test
	@DisplayName("Read/write public field on foreign-classloader class")
	void testPublicFieldAccess() throws Exception {
		Field field = isolatedClass.getDeclaredField( "publicField" );
		HibernateAccessorValueWriter writer = factory.valueWriter( field );
		HibernateAccessorValueReader<?> reader = factory.valueReader( field );

		writer.set( isolatedInstance, "cross-cl-public" );
		assertEquals( "cross-cl-public", reader.get( isolatedInstance ) );
	}

	@Test
	@DisplayName("Read/write private field on foreign-classloader class")
	void testPrivateFieldAccess() throws Exception {
		Field field = isolatedClass.getDeclaredField( "privateField" );
		HibernateAccessorValueWriter writer = factory.valueWriter( field );
		HibernateAccessorValueReader<?> reader = factory.valueReader( field );

		writer.set( isolatedInstance, "cross-cl-private" );
		assertEquals( "cross-cl-private", reader.get( isolatedInstance ) );
	}

	@Test
	@DisplayName("Read/write via public getter/setter on foreign-classloader class")
	void testPublicMethodAccess() throws Exception {
		Method setter = isolatedClass.getDeclaredMethod( "setPublicField", String.class );
		Method getter = isolatedClass.getDeclaredMethod( "getPublicField" );

		HibernateAccessorValueWriter writer = factory.valueWriter( setter );
		HibernateAccessorValueReader<?> reader = factory.valueReader( getter );

		writer.set( isolatedInstance, "cross-cl-method" );
		assertEquals( "cross-cl-method", reader.get( isolatedInstance ) );
	}

	@Test
	@DisplayName("Read/write via private getter/setter on foreign-classloader class")
	void testPrivateMethodAccess() throws Exception {
		Method setter = isolatedClass.getDeclaredMethod( "setPrivateField", String.class );
		Method getter = isolatedClass.getDeclaredMethod( "getPrivateField" );

		HibernateAccessorValueWriter writer = factory.valueWriter( setter );
		HibernateAccessorValueReader<?> reader = factory.valueReader( getter );

		writer.set( isolatedInstance, "cross-cl-private-method" );
		assertEquals( "cross-cl-private-method", reader.get( isolatedInstance ) );
	}

	@Test
	@DisplayName("Multi-value field access on foreign-classloader class")
	void testMultiValueFieldAccess() throws Exception {
		Field publicField = isolatedClass.getDeclaredField( "publicField" );
		Field privateField = isolatedClass.getDeclaredField( "privateField" );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( isolatedClass, publicField, privateField );
		HibernateAccessorMultiValueReader reader = factory.multiValueReader( isolatedClass, publicField, privateField );

		writer.set( isolatedInstance, new Object[]{ "mv-pub", "mv-priv" } );
		Object[] values = reader.get( isolatedInstance );

		assertEquals( 2, values.length );
		assertEquals( "mv-pub", values[0] );
		assertEquals( "mv-priv", values[1] );
	}

	@Test
	@DisplayName("Multi-value mixed field+method access on foreign-classloader class")
	void testMultiValueMixedAccess() throws Exception {
		Field publicField = isolatedClass.getDeclaredField( "publicField" );
		Method getSetter = isolatedClass.getDeclaredMethod( "setPrivateField", String.class );
		Method getGetter = isolatedClass.getDeclaredMethod( "getPrivateField" );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( isolatedClass, publicField, getSetter );
		HibernateAccessorMultiValueReader reader = factory.multiValueReader( isolatedClass, publicField, getGetter );

		writer.set( isolatedInstance, new Object[]{ "mv-field", "mv-method" } );
		Object[] values = reader.get( isolatedInstance );

		assertEquals( 2, values.length );
		assertEquals( "mv-field", values[0] );
		assertEquals( "mv-method", values[1] );
	}
}
