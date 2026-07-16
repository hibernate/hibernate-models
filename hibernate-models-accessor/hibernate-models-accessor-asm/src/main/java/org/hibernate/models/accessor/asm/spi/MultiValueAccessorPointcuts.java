/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.spi;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Member;

public interface MultiValueAccessorPointcuts {

	MultiValueAccessorPointcuts NOOP = new MultiValueAccessorPointcuts() {
		@Override public void emitPreamble(MethodVisitor mv, Class<?> entityClass, int instanceSlot) {}
		@Override public void emitBeforeRead(MethodVisitor mv, int index, Member member, Label skipLabel) {}
		@Override public void emitBeforeWrite(MethodVisitor mv, int index, Member member, Label skipLabel) {}
		@Override public void emitAfterWrite(MethodVisitor mv, int index, Member member) {}
	};

	void emitPreamble(MethodVisitor mv, Class<?> entityClass, int instanceSlot);
	void emitBeforeRead(MethodVisitor mv, int index, Member member, Label skipLabel);
	void emitBeforeWrite(MethodVisitor mv, int index, Member member, Label skipLabel);
	void emitAfterWrite(MethodVisitor mv, int index, Member member);
}
