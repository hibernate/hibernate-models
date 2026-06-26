package org.hibernate.models.accessor.tck.methodhandle;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.methodhandle.impl.HibernateAccessorMethodHandleFactory;
import org.hibernate.models.accessor.tck.util.TckAccessorConfiguration;

import java.lang.invoke.MethodHandles;

public class MethodhandleTckAccessorConfiguration implements TckAccessorConfiguration {
    @Override
    public HibernateAccessorFactory factory() {
        return new HibernateAccessorMethodHandleFactory(MethodHandles.lookup());
    }

}
