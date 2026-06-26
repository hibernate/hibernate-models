/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class AsmBoxingUtil {

	private static final Map<Integer, BoxingInfo> BOXING = Map.of(
			Type.BOOLEAN, new BoxingInfo( "java/lang/Boolean", "Z", "booleanValue", "()Z" ),
			Type.BYTE, new BoxingInfo( "java/lang/Byte", "B", "byteValue", "()B" ),
			Type.CHAR, new BoxingInfo( "java/lang/Character", "C", "charValue", "()C" ),
			Type.SHORT, new BoxingInfo( "java/lang/Short", "S", "shortValue", "()S" ),
			Type.INT, new BoxingInfo( "java/lang/Integer", "I", "intValue", "()I" ),
			Type.LONG, new BoxingInfo( "java/lang/Long", "J", "longValue", "()J" ),
			Type.FLOAT, new BoxingInfo( "java/lang/Float", "F", "floatValue", "()F" ),
			Type.DOUBLE, new BoxingInfo( "java/lang/Double", "D", "doubleValue", "()D" )
	);

	private AsmBoxingUtil() {
	}

	static Type autobox(Type primitiveType) {
		BoxingInfo info = BOXING.get( primitiveType.getSort() );
		if ( info == null ) {
			throw new IllegalArgumentException( "Not a primitive type: " + primitiveType );
		}
		return Type.getObjectType( info.wrapperInternal );
	}

	static void unboxIfRequired(MethodVisitor mv, Type type) {
		BoxingInfo info = BOXING.get( type.getSort() );
		if ( info != null ) {
			mv.visitTypeInsn( Opcodes.CHECKCAST, info.wrapperInternal );
			mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, info.wrapperInternal, info.unboxMethod, info.unboxDesc, false );
		}
		else {
			mv.visitTypeInsn( Opcodes.CHECKCAST, type.getInternalName() );
		}
	}

	static void boxPrimitive(MethodVisitor mv, String descriptor) {
		Type primitiveType = Type.getType( descriptor );
		Type wrapperType = autobox( primitiveType );
		mv.visitMethodInsn( Opcodes.INVOKESTATIC, wrapperType.getInternalName(), "valueOf",
				Type.getMethodDescriptor( wrapperType, primitiveType ), false );
	}

	private record BoxingInfo(String wrapperInternal, String primitiveDesc, String unboxMethod, String unboxDesc) {
	}
}
