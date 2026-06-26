/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import static org.hibernate.models.accessor.generator.impl.GenerationUtil.fqcnToName;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.hibernate.models.accessor.generator.AccessorClassMetadata;

public class BridgeGenerator implements Opcodes, GeneratorConstants {

	public static String bridgeFqcn(String hostFqcn) {
		return hostFqcn + AccessorClassMetadata.BRIDGE_SUFFIX;
	}

	public byte[] generate(String hostFqcn) {
		String bridgeName = fqcnToName( bridgeFqcn( hostFqcn ) );
		String hostName = fqcnToName( hostFqcn );

		ClassWriter cw = new ClassWriter( 0 );
		cw.visit( V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, bridgeName,
				null, "java/lang/Object", null );

		generateForward( cw, PREFIX_READ_METHOD, "(ILjava/lang/Object;)Ljava/lang/Object;", hostName );
		generateForward( cw, PREFIX_WRITE_METHOD, "(ILjava/lang/Object;Ljava/lang/Object;)V", hostName );
		generateForward( cw, PREFIX_CREATE_METHOD, "(I[Ljava/lang/Object;)Ljava/lang/Object;", hostName );

		generateForward( cw, METHOD_NAME_FIELD_READER_ACCESSOR, accessorMethodDescriptor( READER_INTERFACE_INTERNAL ),
				hostName );
		generateForward( cw, METHOD_NAME_METHOD_READER_ACCESSOR, accessorMethodDescriptor( READER_INTERFACE_INTERNAL ),
				hostName );
		generateForward( cw, METHOD_NAME_FIELD_WRITER_ACCESSOR, accessorMethodDescriptor( WRITER_INTERFACE_INTERNAL ),
				hostName );
		generateForward( cw, METHOD_NAME_METHOD_WRITER_ACCESSOR, accessorMethodDescriptor( WRITER_INTERFACE_INTERNAL ),
				hostName );
		generateForward( cw, METHOD_NAME_INSTANTIATOR_ACCESSOR,
				accessorMethodDescriptor( INSTANTIATOR_INTERFACE_INTERNAL ), hostName );

		cw.visitEnd();
		return cw.toByteArray();
	}

	private static String accessorMethodDescriptor(String name) {
		return "(Ljava/lang/String;)L" + name + ";";
	}

	private static void generateForward(ClassWriter cw, String methodName, String descriptor, String hostName) {
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, methodName, descriptor, null,
				null );
		mv.visitCode();

		Type[] argTypes = Type.getArgumentTypes( descriptor );
		for ( int i = 0, slot = 0; i < argTypes.length; i++ ) {
			mv.visitVarInsn( argTypes[i].getOpcode( ILOAD ), slot );
			slot += argTypes[i].getSize();
		}

		mv.visitMethodInsn( INVOKESTATIC, hostName, methodName, descriptor, false );

		Type returnType = Type.getReturnType( descriptor );
		mv.visitInsn( returnType.getOpcode( IRETURN ) );

		mv.visitMaxs( argTypes.length, argTypes.length );
		mv.visitEnd();
	}
}
