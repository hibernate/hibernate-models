/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Map;

final class HibernateAccessorAsmUtils {
	private static final Map<Class<?>, BoxingInfo> BOXING = Map.of(
			boolean.class, new BoxingInfo("java/lang/Boolean", "Z", "booleanValue", "()Z"),
			byte.class, new BoxingInfo("java/lang/Byte", "B", "byteValue", "()B"),
			char.class, new BoxingInfo("java/lang/Character", "C", "charValue", "()C"),
			short.class, new BoxingInfo("java/lang/Short", "S", "shortValue", "()S"),
			int.class, new BoxingInfo("java/lang/Integer", "I", "intValue", "()I"),
			long.class, new BoxingInfo("java/lang/Long", "J", "longValue", "()J"),
			float.class, new BoxingInfo("java/lang/Float", "F", "floatValue", "()F"),
			double.class, new BoxingInfo("java/lang/Double", "D", "doubleValue", "()D")
	);

	private HibernateAccessorAsmUtils() {
	}

	static void emitBox(MethodVisitor mv, Class<?> type) {
		BoxingInfo info = BOXING.get(type);
		if (info != null) {
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, info.wrapperInternal, "valueOf", info.valueOfDesc(), false);
		}
	}

	static void emitUnboxOrCast(MethodVisitor mv, Class<?> type) {
		BoxingInfo info = BOXING.get(type);
		if (info != null) {
			mv.visitTypeInsn(Opcodes.CHECKCAST, info.wrapperInternal);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, info.wrapperInternal, info.unboxMethod, info.unboxDesc, false);
		}
		else {
			mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(type));
		}
	}

	static void emitIntConstant(MethodVisitor mv, int value) {
		if (value >= -1 && value <= 5) {
			mv.visitInsn(Opcodes.ICONST_0 + value);
		}
		else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
			mv.visitIntInsn(Opcodes.BIPUSH, value);
		}
		else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
			mv.visitIntInsn(Opcodes.SIPUSH, value);
		}
		else {
			mv.visitLdcInsn(value);
		}
	}


	private record BoxingInfo(String wrapperInternal, String primitiveDesc, String unboxMethod, String unboxDesc) {
		String valueOfDesc() {
			return "(" + primitiveDesc + ")L" + wrapperInternal + ";";
		}
	}
}
