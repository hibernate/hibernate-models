package org.hibernate.models.accessor.tck.tests.serialization;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.reflection.impl.HibernateAccessorReflectionFactory;
import org.hibernate.models.accessor.tck.tests.beans.visibility.PropertyVisibilityBean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("HibernateAccessorReflectionFactory serialization")
public class HibernateAccessorReflectionFactorySerializationTest {

	@Test
	@DisplayName("Deserialized factory resolves to the singleton INSTANCE")
	void testDeserializedFactoryIsSingleton() throws Exception {
		HibernateAccessorReflectionFactory original = HibernateAccessorReflectionFactory.INSTANCE;

		HibernateAccessorReflectionFactory deserialized = serializeAndDeserialize( original );

		assertNotNull( deserialized );
		assertSame( original, deserialized );
	}

	@Test
	@DisplayName("Multiple deserializations all resolve to the same singleton INSTANCE")
	void testMultipleDeserializationsReturnSameInstance() throws Exception {
		HibernateAccessorReflectionFactory original = HibernateAccessorReflectionFactory.INSTANCE;

		HibernateAccessorReflectionFactory deserialized1 = serializeAndDeserialize( original );
		HibernateAccessorReflectionFactory deserialized2 = serializeAndDeserialize( original );
		HibernateAccessorReflectionFactory deserialized3 = serializeAndDeserialize( deserialized1 );

		assertSame( original, deserialized1 );
		assertSame( original, deserialized2 );
		assertSame( original, deserialized3 );
	}

	@Test
	@DisplayName("Deserialized factory can read and write via method accessors")
	void testDeserializedFactoryMethodAccess() throws Exception {
		HibernateAccessorFactory deserialized = serializeAndDeserialize( HibernateAccessorReflectionFactory.INSTANCE );

		PropertyVisibilityBean bean = new PropertyVisibilityBean();
		Method setter = PropertyVisibilityBean.class.getDeclaredMethod( "setPublicField", String.class );
		Method getter = PropertyVisibilityBean.class.getDeclaredMethod( "getPublicField" );

		HibernateAccessorValueWriter writer = deserialized.valueWriter( setter );
		HibernateAccessorValueReader<?> reader = deserialized.valueReader( getter );

		writer.set( bean, "method-value" );
		assertEquals( "method-value", reader.get( bean ) );
	}

	@Test
	@DisplayName("Deserialized factory can create instances via instantiator")
	void testDeserializedFactoryInstantiator() throws Exception {
		HibernateAccessorFactory deserialized = serializeAndDeserialize( HibernateAccessorReflectionFactory.INSTANCE );

		Constructor<PropertyVisibilityBean> constructor = PropertyVisibilityBean.class.getDeclaredConstructor();

		Object instance = deserialized.instantiator( constructor ).create();
		assertNotNull( instance );
		assertEquals( PropertyVisibilityBean.class, instance.getClass() );
	}

	@SuppressWarnings("unchecked")
	private static <T> T serializeAndDeserialize(T object) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream( baos )) {
			oos.writeObject( object );
		}
		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		try (ObjectInputStream ois = new ObjectInputStream( bais )) {
			return (T) ois.readObject();
		}
	}
}
