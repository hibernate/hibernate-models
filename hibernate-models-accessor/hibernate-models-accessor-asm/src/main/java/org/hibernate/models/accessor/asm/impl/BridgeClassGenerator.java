/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.spi.CrossClassLoaderLookupBridge;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Generates the bridge class bytecode used by {@link CrossClassLoaderLookupBridge}
 * to obtain a full-privilege {@link java.lang.invoke.MethodHandles.Lookup} in a
 * foreign classloader. Uses standalone ASM.
 *
 * @see CrossClassLoaderLookupBridge
 */
final class BridgeClassGenerator {

	private BridgeClassGenerator() {
	}

	static byte[] generate(String className) {
		String internalName = className.replace( '.', '/' );
		ClassWriter cw = new ClassWriter( 0 );
		cw.visit( Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalName,
				null, "java/lang/Object", null );

		MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
				CrossClassLoaderLookupBridge.BRIDGE_METHOD_NAME,
				"()Ljava/lang/invoke/MethodHandles$Lookup;", null, null );
		mv.visitCode();
		mv.visitMethodInsn( Opcodes.INVOKESTATIC, "java/lang/invoke/MethodHandles",
				"lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false );
		mv.visitInsn( Opcodes.ARETURN );
		mv.visitMaxs( 1, 0 );
		mv.visitEnd();

		cw.visitEnd();
		return cw.toByteArray();
	}
}
