package org.hibernate.models.accessor.tck.tests.inheritance;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.tck.tests.beans.inheritance.ChildBean;
import org.hibernate.models.accessor.tck.tests.beans.inheritance.ParentBean;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Inheritance field and method access")
public class InheritanceAccessTest {

    private HibernateAccessorFactory factory;

    @BeforeAll
    void setup() {
        factory = TckHelper.factory();
    }

    @Test
    void testAccessInheritedFieldOnChildInstance() throws Exception {
        Field field = ParentBean.class.getDeclaredField("parentField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        ChildBean child = new ChildBean();
        writer.set(child, "inherited-value");
        assertEquals("inherited-value", reader.get(child));
    }

    @Test
    void testAccessInheritedMethodOnChildInstance() throws Exception {
        Method setter = ParentBean.class.getDeclaredMethod("setParentField", String.class);
        Method getter = ParentBean.class.getDeclaredMethod("getParentField");
        setter.setAccessible(true);
        getter.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(setter);
        HibernateAccessorValueReader<?> reader = factory.valueReader(getter);

        ChildBean child = new ChildBean();
        writer.set(child, "method-inherited");
        assertEquals("method-inherited", reader.get(child));
    }

    @Test
    void testAccessChildFieldOnChildInstance() throws Exception {
        Field field = ChildBean.class.getDeclaredField("childField");
        field.setAccessible(true);

        HibernateAccessorValueWriter writer = factory.valueWriter(field);
        HibernateAccessorValueReader<?> reader = factory.valueReader(field);

        ChildBean child = new ChildBean();
        writer.set(child, "child-value");
        assertEquals("child-value", reader.get(child));
    }

    @Test
    void testAccessBothParentAndChildFields() throws Exception {
        Field parentField = ParentBean.class.getDeclaredField("parentField");
        parentField.setAccessible(true);
        Field childField = ChildBean.class.getDeclaredField("childField");
        childField.setAccessible(true);

        HibernateAccessorValueWriter parentWriter = factory.valueWriter(parentField);
        HibernateAccessorValueReader<?> parentReader = factory.valueReader(parentField);
        HibernateAccessorValueWriter childWriter = factory.valueWriter(childField);
        HibernateAccessorValueReader<?> childReader = factory.valueReader(childField);

        ChildBean child = new ChildBean();
        parentWriter.set(child, "parent-val");
        childWriter.set(child, "child-val");

        assertEquals("parent-val", parentReader.get(child));
        assertEquals("child-val", childReader.get(child));
    }
}
