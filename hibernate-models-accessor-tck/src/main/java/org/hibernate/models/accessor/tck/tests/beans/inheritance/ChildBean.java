package org.hibernate.models.accessor.tck.tests.beans.inheritance;

public class ChildBean extends ParentBean {

    private String childField;

    public ChildBean() {
    }

    public String getChildField() {
        return childField;
    }

    public void setChildField(String childField) {
        this.childField = childField;
    }
}
