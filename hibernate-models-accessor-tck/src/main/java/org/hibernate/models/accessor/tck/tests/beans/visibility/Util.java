package org.hibernate.models.accessor.tck.tests.beans.visibility;

public final class Util {
    private Util() {
    }

    public static Object packagePrivateBeanInstance() {
        return new PackagePrivateBean();
    }
}
