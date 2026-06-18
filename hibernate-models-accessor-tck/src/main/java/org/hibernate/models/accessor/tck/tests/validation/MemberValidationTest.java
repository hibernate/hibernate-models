/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.validation;

import org.hibernate.models.accessor.HibernateAccessorException;
import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.tck.tests.beans.PrimitiveFieldBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Member validation in factory methods")
public class MemberValidationTest {

	private HibernateAccessorFactory factory;

	@BeforeAll
	void setup() {
		factory = TckHelper.factory();
	}

	@Test
	@DisplayName("valueReader rejects a setter method")
	void testValueReaderRejectsSetter() throws Exception {
		Method setter = PrimitiveFieldBean.class.getDeclaredMethod( "setIntField", int.class );
		assertThrows( HibernateAccessorException.class, () -> factory.valueReader( setter ) );
	}

	@Test
	@DisplayName("valueWriter rejects a getter method")
	void testValueWriterRejectsGetter() throws Exception {
		Method getter = PrimitiveFieldBean.class.getDeclaredMethod( "getIntField" );
		assertThrows( HibernateAccessorException.class, () -> factory.valueWriter( getter ) );
	}

	@Test
	@DisplayName("multiValueReader rejects a setter method in mixed members")
	void testMultiReaderRejectsSetter() throws Exception {
		Method setter = PrimitiveFieldBean.class.getDeclaredMethod( "setIntField", int.class );
		assertThrows( HibernateAccessorException.class, () -> factory.multiValueReader( setter ) );
	}

	@Test
	@DisplayName("multiValueWriter rejects a getter method in mixed members")
	void testMultiWriterRejectsGetter() throws Exception {
		Method getter = PrimitiveFieldBean.class.getDeclaredMethod( "getIntField" );
		assertThrows( HibernateAccessorException.class, () -> factory.multiValueWriter( getter ) );
	}
}
