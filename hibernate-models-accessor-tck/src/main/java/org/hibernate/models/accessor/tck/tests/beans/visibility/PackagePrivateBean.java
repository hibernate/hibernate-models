package org.hibernate.models.accessor.tck.tests.beans.visibility;

class PackagePrivateBean {

    public String publicField;
    protected String protectedField;
    String defaultField;
    private String privateField;

    PackagePrivateBean() {
    }

    public String getPublicField() {
        return publicField;
    }

    private String getPrivateField() {
        return privateField;
    }

    String getDefaultField() {
        return defaultField;
    }

    public String getProtectedField() {
        return protectedField;
    }

    public void setPublicField(String publicField) {
        this.publicField = publicField;
    }

    protected void setProtectedField(String protectedField) {
        this.protectedField = protectedField;
    }

    void setDefaultField(String defaultField) {
        this.defaultField = defaultField;
    }

    private void setPrivateField(String val) {
        this.privateField = val;
    }
}
