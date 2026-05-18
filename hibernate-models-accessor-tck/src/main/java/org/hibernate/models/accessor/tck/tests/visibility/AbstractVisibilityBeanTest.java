package org.hibernate.models.accessor.tck.tests.visibility;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractVisibilityBeanTest {

    private HibernateAccessorFactory factory;
    private Class<?> beanClass;

    @BeforeAll
    void setup() {
        factory = TckHelper.factory();
        // Loaded via name because it's package-private in another package
        beanClass = bean();
    }

    protected abstract Class<?> bean();

    protected abstract Object instance();

    @Test
    @DisplayName("Verify instantiation of package-private class")
    void testInstantiation() throws Exception {
        Constructor<?> constructor = beanClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        HibernateAccessorInstantiator<?> instantiator = factory.instantiator(constructor);

        Object instance = instantiator.create();
        assertNotNull(instance);
        assertEquals(beanClass, instance.getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"publicField", "protectedField", "defaultField", "privateField"})
    @DisplayName("Verify field accessors for all visibility levels")
    void testFieldAccess(String fieldName) throws Exception {
        Field field = beanClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object bean = instance();
        String testValue = "val-" + fieldName;

        HibernateAccessorValueWriter writer = (HibernateAccessorValueWriter) factory.valueWriter(field);
        HibernateAccessorValueReader<String> reader = (HibernateAccessorValueReader<String>) factory.valueReader(field);

        writer.set(bean, testValue);
        assertEquals(testValue, reader.get(bean), "Failed for field: " + fieldName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PublicField", "ProtectedField", "DefaultField", "PrivateField"})
    @DisplayName("Verify method accessors: Public, Default, and Private")
    void testMethodAccess(String fieldName) throws Exception {
        Object bean = instance();

        String setterName = "set" + fieldName;
        String getterName = "get" + fieldName;
        String value = "val-" + fieldName;

        Method setter = beanClass.getDeclaredMethod(setterName, String.class);
        Method getter = beanClass.getDeclaredMethod(getterName);
        setter.setAccessible(true);
        getter.setAccessible(true);

        HibernateAccessorValueWriter writer = (HibernateAccessorValueWriter) factory.valueWriter(setter);
        HibernateAccessorValueReader<String> reader = (HibernateAccessorValueReader<String>) factory.valueReader(getter);

        writer.set(bean, value);
        assertEquals(value, reader.get(bean), "Method pair failed: " + setterName + "/" + getterName);
    }
}
