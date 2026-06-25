package org.hibernate.models.accessor.tck.util;

import org.hibernate.models.accessor.HibernateAccessorFactory;

import java.util.ServiceLoader;

public class TckHelper {

    private static final TckAccessorConfiguration CONFIGURATION;

    static {
        ServiceLoader<TckAccessorConfiguration> loader =
                ServiceLoader.load(TckAccessorConfiguration.class);

        // Find the configuration and extract the factory

        CONFIGURATION = loader.findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No TckAccessorConfiguration implementation found! " +
                                "Check META-INF/services/org.hibernate.models.accessor.tck.util.TckAccessorConfiguration"
                ));
    }

    public static HibernateAccessorFactory factory() {
        return CONFIGURATION.factory();
    }
}
