/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.multivalue;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.inheritance.ChildBean;
import org.hibernate.models.accessor.tck.tests.beans.inheritance.ParentBean;
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
@DisplayName("Multi-value accessor access across class hierarchy")
public class MultiValueInheritanceTest {

	private HibernateAccessorFactory factory;

	@BeforeAll
	void setup() {
		factory = TckHelper.factory();
	}

	@Test
	@DisplayName("Read fields from parent and child classes")
	void testCrossClassFieldRead() throws Exception {
		ChildBean bean = new ChildBean();
		bean.setParentField( "parent-value" );
		bean.setChildField( "child-value" );

		Field parentField = ParentBean.class.getDeclaredField( "parentField" );
		Field childField = ChildBean.class.getDeclaredField( "childField" );
		parentField.setAccessible( true );
		childField.setAccessible( true );

		HibernateAccessorMultiValueReader reader = factory.multiValueReader( ChildBean.class, parentField, childField );
		Object[] values = reader.get( bean );

		assertEquals( 2, values.length );
		assertEquals( "parent-value", values[0] );
		assertEquals( "child-value", values[1] );
	}

	@Test
	@DisplayName("Write fields across parent and child classes")
	void testCrossClassFieldWrite() throws Exception {
		ChildBean bean = new ChildBean();

		Field parentField = ParentBean.class.getDeclaredField( "parentField" );
		Field childField = ChildBean.class.getDeclaredField( "childField" );
		parentField.setAccessible( true );
		childField.setAccessible( true );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( ChildBean.class, parentField, childField );
		writer.set( bean, new Object[]{ "written-parent", "written-child" } );

		assertEquals( "written-parent", bean.getParentField() );
		assertEquals( "written-child", bean.getChildField() );
	}

	@Test
	@DisplayName("Read via mixed fields and methods across hierarchy")
	void testCrossClassMixedRead() throws Exception {
		ChildBean bean = new ChildBean();
		bean.setParentField( "p" );
		bean.setChildField( "c" );

		Field parentField = ParentBean.class.getDeclaredField( "parentField" );
		parentField.setAccessible( true );
		Method getChild = ChildBean.class.getDeclaredMethod( "getChildField" );

		HibernateAccessorMultiValueReader reader = factory.multiValueReader( ChildBean.class, parentField, getChild );
		Object[] values = reader.get( bean );

		assertEquals( 2, values.length );
		assertEquals( "p", values[0] );
		assertEquals( "c", values[1] );
	}

	@Test
	@DisplayName("Write via mixed fields and methods across hierarchy")
	void testCrossClassMixedWrite() throws Exception {
		ChildBean bean = new ChildBean();

		Method setParent = ParentBean.class.getDeclaredMethod( "setParentField", String.class );
		Field childField = ChildBean.class.getDeclaredField( "childField" );
		childField.setAccessible( true );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( ChildBean.class, setParent, childField );
		writer.set( bean, new Object[]{ "via-method", "via-field" } );

		assertEquals( "via-method", bean.getParentField() );
		assertEquals( "via-field", bean.getChildField() );
	}

	@Test
	@DisplayName("Round-trip: write then read across hierarchy")
	void testCrossClassRoundTrip() throws Exception {
		ChildBean bean = new ChildBean();

		Field parentField = ParentBean.class.getDeclaredField( "parentField" );
		Field childField = ChildBean.class.getDeclaredField( "childField" );
		parentField.setAccessible( true );
		childField.setAccessible( true );

		Member[] members = { parentField, childField };

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter( ChildBean.class, members );
		HibernateAccessorMultiValueReader reader = factory.multiValueReader( ChildBean.class, members );

		Object[] input = { "hello", "world" };
		writer.set( bean, input );
		Object[] output = reader.get( bean );

		assertArrayEquals( input, output );
	}
}
