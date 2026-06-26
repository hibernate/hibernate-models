package org.hibernate.models.accessor.tck.asm;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.asm.HibernateAccessorAsmFactory;
import org.hibernate.models.accessor.tck.util.TckAccessorConfiguration;

import java.lang.invoke.MethodHandles;

public class AsmTckAccessorConfiguration implements TckAccessorConfiguration {
    @Override
    public HibernateAccessorFactory factory() {
        return HibernateAccessorAsmFactory.factory(MethodHandles.lookup());
    }
}
