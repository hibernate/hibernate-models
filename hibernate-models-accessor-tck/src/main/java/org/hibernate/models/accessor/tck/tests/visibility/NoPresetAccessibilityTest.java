package org.hibernate.models.accessor.tck.tests.visibility;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.visibility.PropertyVisibilityBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Access without caller pre-setting setAccessible")
public class NoPresetAccessibilityTest {

    private HibernateAccessorFactory factory;

    @BeforeAll
    void setup() {
        factory = TckHelper.factory();
    }

    @Test
    void testPrivateFieldAccessWithoutSetAccessible() throws Exception {
        Field field = PropertyVisibilityBean.class.getDeclaredField("privateField");

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        PropertyVisibilityBean bean = new PropertyVisibilityBean();
        writer.set(bean, "no-preset");
        assertEquals("no-preset", reader.get(bean));
    }

    @Test
    void testPrivateMethodAccessWithoutSetAccessible() throws Exception {
        Method setter = PropertyVisibilityBean.class.getDeclaredMethod("setPrivateField", String.class);
        Method getter = PropertyVisibilityBean.class.getDeclaredMethod("getPrivateField");

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        PropertyVisibilityBean bean = new PropertyVisibilityBean();
        writer.set(bean, "no-preset");
        assertEquals("no-preset", reader.get(bean));
    }

    @Test
    void testPackagePrivateFieldAccessWithoutSetAccessible() throws Exception {
        Field field = PropertyVisibilityBean.class.getDeclaredField("defaultField");

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        PropertyVisibilityBean bean = new PropertyVisibilityBean();
        writer.set(bean, "pkg-private");
        assertEquals("pkg-private", reader.get(bean));
    }

    @Test
    void testProtectedFieldAccessWithoutSetAccessible() throws Exception {
        Field field = PropertyVisibilityBean.class.getDeclaredField("protectedField");

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        PropertyVisibilityBean bean = new PropertyVisibilityBean();
        writer.set(bean, "protected-val");
        assertEquals("protected-val", reader.get(bean));
    }

    @Test
    void testConstructorWithoutSetAccessible() throws Exception {
        Constructor<?> constructor = PropertyVisibilityBean.class.getDeclaredConstructor();

        HibernateAccessorInstantiator<?> instantiator = factory.instantiator(constructor);
        Object instance = instantiator.create();
        assertNotNull(instance);
        assertEquals(PropertyVisibilityBean.class, instance.getClass());
    }
}
