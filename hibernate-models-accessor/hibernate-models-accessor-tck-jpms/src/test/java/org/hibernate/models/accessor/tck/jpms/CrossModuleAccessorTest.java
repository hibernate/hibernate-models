/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.jpms;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.asm.HibernateAccessorAsmFactory;
import org.hibernate.models.accessor.bytebuddy.HibernateAccessorByteBuddyFactory;
import org.hibernate.models.accessor.tck.jpms.entities.SimpleEntity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CrossModuleAccessorTest {

	static Stream<HibernateAccessorFactory> factories() {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		return Stream.of(
				HibernateAccessorFactory.reflection(),
				HibernateAccessorFactory.lambda( lookup ),
				HibernateAccessorAsmFactory.factory( lookup ),
				HibernateAccessorByteBuddyFactory.factory( lookup )
		);
	}

	@org.junit.jupiter.api.Test
	void entityClassIsInNamedModule() {
		assertThat( SimpleEntity.class.getModule().isNamed() )
				.as( "SimpleEntity must be in a named JPMS module" )
				.isTrue();
		assertThat( SimpleEntity.class.getModule().getName() )
				.isEqualTo( "hibernate.models.accessor.tck.jpms.entities" );
	}

	@ParameterizedTest
	@MethodSource("factories")
	void testFieldAccess(HibernateAccessorFactory factory) throws Exception {
		Field nameField = SimpleEntity.class.getDeclaredField( "name" );
		nameField.setAccessible( true );

		HibernateAccessorValueReader<?> reader = factory.valueReader( nameField );
		HibernateAccessorValueWriter writer = factory.valueWriter( nameField );

		SimpleEntity entity = new SimpleEntity( 1, "original" );
		assertThat( reader.get( entity ) ).isEqualTo( "original" );

		writer.set( entity, "updated" );
		assertThat( reader.get( entity ) ).isEqualTo( "updated" );
	}

	@ParameterizedTest
	@MethodSource("factories")
	void testMethodAccess(HibernateAccessorFactory factory) throws Exception {
		Method getter = SimpleEntity.class.getDeclaredMethod( "getName" );
		Method setter = SimpleEntity.class.getDeclaredMethod( "setName", String.class );

		HibernateAccessorValueReader<?> reader = factory.valueReader( getter );
		HibernateAccessorValueWriter writer = factory.valueWriter( setter );

		SimpleEntity entity = new SimpleEntity( 1, "original" );
		assertThat( reader.get( entity ) ).isEqualTo( "original" );

		writer.set( entity, "updated" );
		assertThat( reader.get( entity ) ).isEqualTo( "updated" );
	}

	@ParameterizedTest
	@MethodSource("factories")
	void testInstantiator(HibernateAccessorFactory factory) throws Exception {
		HibernateAccessorInstantiator<SimpleEntity> instantiator =
				factory.instantiator( SimpleEntity.class.getDeclaredConstructor() );

		SimpleEntity entity = instantiator.create();
		assertThat( entity ).isNotNull();
	}

	@ParameterizedTest
	@MethodSource("factories")
	void testMultiValueAccess(HibernateAccessorFactory factory) throws Exception {
		Field idField = SimpleEntity.class.getDeclaredField( "id" );
		idField.setAccessible( true );
		Field nameField = SimpleEntity.class.getDeclaredField( "name" );
		nameField.setAccessible( true );

		HibernateAccessorMultiValueReader reader = factory.multiValueReader(
				SimpleEntity.class, idField, nameField
		);
		HibernateAccessorMultiValueWriter writer = factory.multiValueWriter(
				SimpleEntity.class, idField, nameField
		);

		SimpleEntity entity = new SimpleEntity( 1, "original" );
		Object[] values = reader.get( entity );
		assertThat( values ).containsExactly( 1, "original" );

		writer.set( entity, new Object[]{ 2, "updated" } );
		values = reader.get( entity );
		assertThat( values ).containsExactly( 2, "updated" );
	}
}
