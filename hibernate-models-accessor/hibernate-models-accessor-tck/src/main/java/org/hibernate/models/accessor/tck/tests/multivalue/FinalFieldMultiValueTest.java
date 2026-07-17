/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.multivalue;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.FinalFieldBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Multi-value access on a class with a final field (index stability)")
public class FinalFieldMultiValueTest {

	private HibernateAccessorFactory factory;

	@BeforeAll
	void setup() {
		factory = TckHelper.factory();
	}

	@Test
	@DisplayName("Multi-value write to fields around a final field")
	void testMultiValueWriteWithFinalField() throws Exception {
		FinalFieldBean bean = new FinalFieldBean( 42 );

		Field alphaField = FinalFieldBean.class.getDeclaredField( "alpha" );
		Field gammaField = FinalFieldBean.class.getDeclaredField( "gamma" );

		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter(
				FinalFieldBean.class, alphaField, gammaField );
		writer.set( bean, new Object[] { "A", "G" } );

		assertEquals( "A", bean.getAlpha() );
		assertEquals( "G", bean.getGamma() );
	}

	@Test
	@DisplayName("Multi-value read from fields around a final field")
	void testMultiValueReadWithFinalField() throws Exception {
		FinalFieldBean bean = new FinalFieldBean( 42 );
		bean.setAlpha( "A" );
		bean.setGamma( "G" );

		Field alphaField = FinalFieldBean.class.getDeclaredField( "alpha" );
		Field gammaField = FinalFieldBean.class.getDeclaredField( "gamma" );

		HibernateAccessorMultiValueReader reader = factory.multiValueReader(
				FinalFieldBean.class, alphaField, gammaField );
		Object[] values = reader.get( bean );

		assertEquals( 2, values.length );
		assertEquals( "A", values[0] );
		assertEquals( "G", values[1] );
	}
}
