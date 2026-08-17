package org.hibernate.models.accessor.tck.tests.record;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.tck.tests.beans.SimpleRecord;
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
@DisplayName("Record access")
public class RecordAccessTest {

    private HibernateAccessorFactory factory;

    @BeforeAll
    void setup() {
        factory = TckHelper.factory();
    }

    @Test
    void testRecordInstantiationViaCanonicalConstructor() throws Exception {
        Constructor<SimpleRecord> ctor = SimpleRecord.class.getDeclaredConstructor(String.class, int.class);
        ctor.setAccessible(true);
        HibernateAccessorInstantiator<SimpleRecord> instantiator = factory.instantiator(ctor);

        SimpleRecord record = instantiator.create("test", 99);
        assertNotNull(record);
        assertEquals("test", record.name());
        assertEquals(99, record.value());
    }

    @Test
    void testRecordStringComponentMethodReader() throws Exception {
        SimpleRecord record = new SimpleRecord("hello", 42);

        Method nameGetter = SimpleRecord.class.getDeclaredMethod("name");
        nameGetter.setAccessible(true);
        HibernateAccessorValueReader<?> nameReader = factory.valueReader(nameGetter);

        assertEquals("hello", nameReader.get(record));
    }

    @Test
    void testRecordIntComponentMethodReader() throws Exception {
        SimpleRecord record = new SimpleRecord("hello", 42);

        Method valueGetter = SimpleRecord.class.getDeclaredMethod("value");
        valueGetter.setAccessible(true);
        HibernateAccessorValueReader<?> valueReader = factory.valueReader(valueGetter);

        assertEquals(42, valueReader.get(record));
    }

    @Test
    void testRecordStringFieldReader() throws Exception {
        SimpleRecord record = new SimpleRecord("hello", 42);

        Field nameField = SimpleRecord.class.getDeclaredField("name");
        nameField.setAccessible(true);
        HibernateAccessorValueReader<?> nameReader = factory.valueReader(nameField);

        assertEquals("hello", nameReader.get(record));
    }

    @Test
    void testRecordIntFieldReader() throws Exception {
        SimpleRecord record = new SimpleRecord("hello", 42);

        Field valueField = SimpleRecord.class.getDeclaredField("value");
        valueField.setAccessible(true);
        HibernateAccessorValueReader<?> valueReader = factory.valueReader(valueField);

        assertEquals(42, valueReader.get(record));
    }
}
