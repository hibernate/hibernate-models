package org.hibernate.models.accessor.tck.lambda;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.lambda.impl.HibernateAccessorLambdaFactory;
import org.hibernate.models.accessor.tck.util.TckAccessorConfiguration;

import java.lang.invoke.MethodHandles;

public class LambdaTckAccessorConfiguration implements TckAccessorConfiguration {
    @Override
    public HibernateAccessorFactory factory() {
        return new HibernateAccessorLambdaFactory(MethodHandles.lookup());
    }
}
