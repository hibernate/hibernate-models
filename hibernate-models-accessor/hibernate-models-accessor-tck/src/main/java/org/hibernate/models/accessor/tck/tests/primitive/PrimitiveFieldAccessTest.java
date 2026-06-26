package org.hibernate.models.accessor.tck.tests.primitive;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.PrimitiveFieldBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Primitive type field and method access")
public class PrimitiveFieldAccessTest {

    private HibernateAccessorFactory factory;

    @BeforeAll
    void setup() {
        factory = TckHelper.factory();
    }

    @Test
    void testIntFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("intField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, 42);
        assertEquals(42, reader.get(bean));
    }

    @Test
    void testLongFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("longField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, 123456789L);
        assertEquals(123456789L, reader.get(bean));
    }

    @Test
    void testBooleanFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("booleanField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, true);
        assertEquals(true, reader.get(bean));
    }

    @Test
    void testDoubleFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("doubleField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, 3.14);
        assertEquals(3.14, reader.get(bean));
    }

    @Test
    void testFloatFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("floatField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, 2.5f);
        assertEquals(2.5f, reader.get(bean));
    }

    @Test
    void testShortFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("shortField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, (short) 7);
        assertEquals((short) 7, reader.get(bean));
    }

    @Test
    void testByteFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("byteField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, (byte) 3);
        assertEquals((byte) 3, reader.get(bean));
    }

    @Test
    void testCharFieldAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Field field = PrimitiveFieldBean.class.getDeclaredField("charField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        writer.set(bean, 'Z');
        assertEquals('Z', reader.get(bean));
    }

    @Test
    void testIntMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setIntField", int.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getIntField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, 99);
        assertEquals(99, reader.get(bean));
    }

    @Test
    void testLongMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setLongField", long.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getLongField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, 987654321L);
        assertEquals(987654321L, reader.get(bean));
    }

    @Test
    void testBooleanMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setBooleanField", boolean.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("isBooleanField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, true);
        assertEquals(true, reader.get(bean));
    }

    @Test
    void testDoubleMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setDoubleField", double.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getDoubleField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, 2.718);
        assertEquals(2.718, reader.get(bean));
    }

    @Test
    void testFloatMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setFloatField", float.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getFloatField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, 1.5f);
        assertEquals(1.5f, reader.get(bean));
    }

    @Test
    void testShortMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setShortField", short.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getShortField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, (short) 11);
        assertEquals((short) 11, reader.get(bean));
    }

    @Test
    void testByteMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setByteField", byte.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getByteField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, (byte) 5);
        assertEquals((byte) 5, reader.get(bean));
    }

    @Test
    void testCharMethodAccess() throws Exception {
        PrimitiveFieldBean bean = new PrimitiveFieldBean();
        Method setter = PrimitiveFieldBean.class.getDeclaredMethod("setCharField", char.class);
        Method getter = PrimitiveFieldBean.class.getDeclaredMethod("getCharField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        writer.set(bean, 'A');
        assertEquals('A', reader.get(bean));
    }
}
