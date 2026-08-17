package org.hibernate.models.accessor.tck.bytebuddy;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.bytebuddy.HibernateAccessorByteBuddyFactory;
import org.hibernate.models.accessor.tck.util.TckAccessorConfiguration;

import java.lang.invoke.MethodHandles;

public class ByteBuddyTckAccessorConfiguration implements TckAccessorConfiguration {
    @Override
    public HibernateAccessorFactory factory() {
        return HibernateAccessorByteBuddyFactory.factory(MethodHandles.lookup());
    }
}
