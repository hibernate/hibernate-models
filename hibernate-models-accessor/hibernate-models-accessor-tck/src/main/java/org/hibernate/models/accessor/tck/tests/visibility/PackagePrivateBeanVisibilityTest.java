package org.hibernate.models.accessor.tck.tests.visibility;

import org.hibernate.models.accessor.tck.tests.beans.visibility.Util;

public class PackagePrivateBeanVisibilityTest extends AbstractVisibilityBeanTest {
    @Override
    protected Class<?> bean() {
        try {
            return Class.forName("org.hibernate.models.accessor.tck.tests.beans.visibility.PackagePrivateBean");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object instance() {
        return Util.packagePrivateBeanInstance();
    }
}
