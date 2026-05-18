package org.hibernate.models.accessor.tck.util;

import org.hibernate.models.accessor.HibernateAccessorFactory;

public interface TckAccessorConfiguration {
    /**
     * Returns the factory implementation to be tested.
     */
    HibernateAccessorFactory factory();
}
