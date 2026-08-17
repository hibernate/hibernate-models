/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.overloaded;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.OverloadedSetterBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Overloaded setter methods")
public class OverloadedSetterTest {

	private HibernateAccessorFactory factory;

	@BeforeAll
	void setup() {
		factory = TckHelper.factory();
	}

	@Test
	@DisplayName("Write via setValue(String)")
	void testStringOverload() throws Exception {
		OverloadedSetterBean bean = new OverloadedSetterBean();
		Method setter = OverloadedSetterBean.class.getDeclaredMethod( "setValue", String.class );

		HibernateAccessorValueWriter writer = factory.valueWriter( setter );
		writer.set( bean, "hello" );
		assertEquals( "hello", bean.getValue() );
	}

	@Test
	@DisplayName("Write via setValue(int)")
	void testIntOverload() throws Exception {
		OverloadedSetterBean bean = new OverloadedSetterBean();
		Method setter = OverloadedSetterBean.class.getDeclaredMethod( "setValue", int.class );

		HibernateAccessorValueWriter writer = factory.valueWriter( setter );
		writer.set( bean, 42 );
		assertEquals( "42", bean.getValue() );
	}
}
