/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.beans.nested;

public class NestedClassBean {

	public static class PublicStaticNested {
		public String publicField;
		private String privateField;

		public PublicStaticNested() {
		}

		public String getPublicField() {
			return publicField;
		}

		public void setPublicField(String publicField) {
			this.publicField = publicField;
		}

		private String getPrivateField() {
			return privateField;
		}

		private void setPrivateField(String privateField) {
			this.privateField = privateField;
		}
	}

	protected static class ProtectedStaticNested {
		public String publicField;
		private String privateField;

		protected ProtectedStaticNested() {
		}

		public String getPublicField() {
			return publicField;
		}

		public void setPublicField(String publicField) {
			this.publicField = publicField;
		}

		private String getPrivateField() {
			return privateField;
		}

		private void setPrivateField(String privateField) {
			this.privateField = privateField;
		}
	}

	static class DefaultStaticNested {
		public String publicField;
		private String privateField;

		DefaultStaticNested() {
		}

		public String getPublicField() {
			return publicField;
		}

		public void setPublicField(String publicField) {
			this.publicField = publicField;
		}

		private String getPrivateField() {
			return privateField;
		}

		private void setPrivateField(String privateField) {
			this.privateField = privateField;
		}
	}

	private static class PrivateStaticNested {
		public String publicField;
		private String privateField;

		private PrivateStaticNested() {
		}

		public String getPublicField() {
			return publicField;
		}

		public void setPublicField(String publicField) {
			this.publicField = publicField;
		}

		private String getPrivateField() {
			return privateField;
		}

		private void setPrivateField(String privateField) {
			this.privateField = privateField;
		}
	}

	public static Object createProtectedNested() {
		return new ProtectedStaticNested();
	}

	public static Object createDefaultNested() {
		return new DefaultStaticNested();
	}

	public static Object createPrivateNested() {
		return new PrivateStaticNested();
	}

	public static Class<?> protectedNestedClass() {
		return ProtectedStaticNested.class;
	}

	public static Class<?> defaultNestedClass() {
		return DefaultStaticNested.class;
	}

	public static Class<?> privateNestedClass() {
		return PrivateStaticNested.class;
	}
}
