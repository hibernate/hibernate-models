/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.multivalue;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.PrimitiveFieldBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Multi-value accessor access with mixed fields and methods")
public class MultiValueAccessTest {

	private HibernateAccessorFactory factory;

	@BeforeAll
	void setup() {
		factory = TckHelper.factory();
	}

	@Test
	@DisplayName("Read multiple fields at once")
	void testMultiFieldRead() throws Exception {
		PrimitiveFieldBean bean = new PrimitiveFieldBean();
		bean.setIntField( 42 );
		bean.setLongField( 100L );
		bean.setDoubleField( 3.14 );

		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		Field longField = PrimitiveFieldBean.class.getDeclaredField( "longField" );
		Field doubleField = PrimitiveFieldBean.class.getDeclaredField( "doubleField" );
		intField.setAccessible( true );
		longField.setAccessible( true );
		doubleField.setAccessible( true );

		HibernateAccessorMultiValueReader reader = factory.multiValueReader( PrimitiveFieldBean.class, intField, longField, doubleField );
		Object[] values = reader.get( bean );

		assertEquals( 3, values.length );
		assertEquals( 42, values[0] );
		assertEquals( 100L, values[1] );
		assertEquals( 3.14, values[2] );
	}

	@Test
	@DisplayName("Write multiple fields at once")
	void testMultiFieldWrite() throws Exception {
		PrimitiveFieldBean bean = new PrimitiveFieldBean();

		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		Field longField = PrimitiveFieldBean.class.getDeclaredField( "longField" );
		intField.setAccessible( true );
		longField.setAccessible( true );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( PrimitiveFieldBean.class, intField, longField );
		writer.set( bean, new Object[]{ 99, 200L } );

		assertEquals( 99, bean.getIntField() );
		assertEquals( 200L, bean.getLongField() );
	}

	@Test
	@DisplayName("Read via mixed fields and getter methods")
	void testMixedFieldAndMethodRead() throws Exception {
		PrimitiveFieldBean bean = new PrimitiveFieldBean();
		bean.setIntField( 10 );
		bean.setLongField( 20L );
		bean.setBooleanField( true );

		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		intField.setAccessible( true );
		Method getLong = PrimitiveFieldBean.class.getDeclaredMethod( "getLongField" );
		Field booleanField = PrimitiveFieldBean.class.getDeclaredField( "booleanField" );
		booleanField.setAccessible( true );

		HibernateAccessorMultiValueReader reader = factory.multiValueReader( PrimitiveFieldBean.class, intField, getLong, booleanField );
		Object[] values = reader.get( bean );

		assertEquals( 3, values.length );
		assertEquals( 10, values[0] );
		assertEquals( 20L, values[1] );
		assertEquals( true, values[2] );
	}

	@Test
	@DisplayName("Write via mixed fields and setter methods")
	void testMixedFieldAndMethodWrite() throws Exception {
		PrimitiveFieldBean bean = new PrimitiveFieldBean();

		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		intField.setAccessible( true );
		Method setLong = PrimitiveFieldBean.class.getDeclaredMethod( "setLongField", long.class );
		Field booleanField = PrimitiveFieldBean.class.getDeclaredField( "booleanField" );
		booleanField.setAccessible( true );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( PrimitiveFieldBean.class, intField, setLong, booleanField );
		writer.set( bean, new Object[]{ 55, 77L, true } );

		assertEquals( 55, bean.getIntField() );
		assertEquals( 77L, bean.getLongField() );
		assertEquals( true, bean.isBooleanField() );
	}

	@Test
	@DisplayName("Round-trip: write then read with multi-value accessors")
	void testMultiValueRoundTrip() throws Exception {
		PrimitiveFieldBean bean = new PrimitiveFieldBean();

		Field intField = PrimitiveFieldBean.class.getDeclaredField( "intField" );
		intField.setAccessible( true );
		Method setDouble = PrimitiveFieldBean.class.getDeclaredMethod( "setDoubleField", double.class );
		Method getDouble = PrimitiveFieldBean.class.getDeclaredMethod( "getDoubleField" );
		Field charField = PrimitiveFieldBean.class.getDeclaredField( "charField" );
		charField.setAccessible( true );

		Member[] writeMembers = { intField, setDouble, charField };
		Member[] readMembers = { intField, getDouble, charField };

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( PrimitiveFieldBean.class, writeMembers );
		HibernateAccessorMultiValueReader reader = factory.multiValueReader( PrimitiveFieldBean.class, readMembers );

		Object[] input = { 123, 9.81, 'X' };
		writer.set( bean, input );
		Object[] output = reader.get( bean );

		assertArrayEquals( input, output );
	}
}
