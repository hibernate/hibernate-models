package org.hibernate.models.accessor.tck.tests.beans;

public class OverloadedSetterBean {

	private String value;

	public OverloadedSetterBean() {
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(int value) {
		this.value = String.valueOf( value );
	}
}
