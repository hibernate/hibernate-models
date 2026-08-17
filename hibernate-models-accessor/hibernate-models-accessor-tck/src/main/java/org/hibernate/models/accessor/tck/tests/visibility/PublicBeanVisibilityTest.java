package org.hibernate.models.accessor.tck.tests.visibility;

import org.hibernate.models.accessor.tck.tests.beans.visibility.PropertyVisibilityBean;

public class PublicBeanVisibilityTest extends AbstractVisibilityBeanTest {
    @Override
    protected Class<?> bean() {
        return PropertyVisibilityBean.class;
    }

    @Override
    protected Object instance() {
        return new PropertyVisibilityBean();
    }
}
