package org.hibernate.models.accessor.tck.reflection;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.reflection.impl.HibernateAccessorReflectionFactory;
import org.hibernate.models.accessor.tck.util.TckAccessorConfiguration;

public class ReflectionTckAccessorConfiguration implements TckAccessorConfiguration {
    @Override
    public HibernateAccessorFactory factory() {
        return HibernateAccessorReflectionFactory.INSTANCE;
    }
}
