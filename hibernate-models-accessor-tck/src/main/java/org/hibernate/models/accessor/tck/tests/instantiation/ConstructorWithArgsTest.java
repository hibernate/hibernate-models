package org.hibernate.models.accessor.tck.tests.instantiation;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.tck.tests.beans.SimpleRecord;
import org.hibernate.models.accessor.tck.util.TckHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Constructor with arguments")
public class ConstructorWithArgsTest {

    private HibernateAccessorFactory factory;

    @BeforeAll
    void setup() {
        factory = TckHelper.factory();
    }

    @Test
    void testTwoArgConstructor() throws Exception {
        Constructor<SimpleRecord> ctor = SimpleRecord.class.getDeclaredConstructor(String.class, int.class);
        ctor.setAccessible(true);
        HibernateAccessorInstantiator<SimpleRecord> instantiator = factory.instantiator(ctor);

        SimpleRecord record = instantiator.create("hello", 42);
        assertNotNull(record);
        assertEquals("hello", record.name());
        assertEquals(42, record.value());
    }

    @Test
    void testConstructorWithNullStringArg() throws Exception {
        Constructor<SimpleRecord> ctor = SimpleRecord.class.getDeclaredConstructor(String.class, int.class);
        ctor.setAccessible(true);
        HibernateAccessorInstantiator<SimpleRecord> instantiator = factory.instantiator(ctor);

        SimpleRecord record = instantiator.create(null, 0);
        assertNotNull(record);
        assertNull(record.name());
        assertEquals(0, record.value());
    }

    @Test
    void testConstructorWithDifferentValues() throws Exception {
        Constructor<SimpleRecord> ctor = SimpleRecord.class.getDeclaredConstructor(String.class, int.class);
        ctor.setAccessible(true);
        HibernateAccessorInstantiator<SimpleRecord> instantiator = factory.instantiator(ctor);

        SimpleRecord r1 = instantiator.create("first", 1);
        SimpleRecord r2 = instantiator.create("second", 2);

        assertEquals("first", r1.name());
        assertEquals(1, r1.value());
        assertEquals("second", r2.name());
        assertEquals(2, r2.value());
    }
}
