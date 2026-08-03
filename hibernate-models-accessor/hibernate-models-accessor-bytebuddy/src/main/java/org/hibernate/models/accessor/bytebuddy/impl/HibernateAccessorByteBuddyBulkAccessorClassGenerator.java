/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.bytebuddy.impl;

import org.hibernate.models.accessor.bytebuddy.spi.HibernateAccessorByteBuddyBulkAccessor;

import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.hibernate.models.accessor.bytebuddy.impl.HibernateAccessorByteBuddyUtils.emitBox;
import static org.hibernate.models.accessor.bytebuddy.impl.HibernateAccessorByteBuddyUtils.emitIntConstant;
import static org.hibernate.models.accessor.bytebuddy.impl.HibernateAccessorByteBuddyUtils.emitUnboxOrCast;

final class HibernateAccessorByteBuddyBulkAccessorClassGenerator implements Opcodes {

	private static final String BULK_ACCESSOR_INTERNAL = Type.getInternalName(HibernateAccessorByteBuddyBulkAccessor.class);
	private static final String EXCEPTION_INTERNAL = "org/hibernate/models/accessor/HibernateAccessorException";

	static byte[] generate(Class<?> targetClass, Field[] fields, Method[] getterMethods, Method[] setterMethods, Constructor<?>[] constructors) {
		String targetInternal = Type.getInternalName(targetClass);
		boolean isInterface = targetClass.isInterface();
		String generatedName = targetInternal + "$$HibernateAccessor";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, generatedName, null,
				"java/lang/Object", new String[]{BULK_ACCESSOR_INTERNAL});

		generateConstructor(cw);
		generateReadByField(cw, targetInternal, fields);
		generateWriteByField(cw, targetInternal, fields);
		generateReadByMethod(cw, targetInternal, isInterface, getterMethods);
		generateWriteByMethod(cw, targetInternal, isInterface, setterMethods);
		generateNewInstance(cw, targetInternal, constructors);

		cw.visitEnd();
		return cw.toByteArray();
	}

	private static void generateConstructor(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void generateReadByField(ClassWriter cw,
											String targetInternal, Field[] fields) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "readByField",
				"(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
		mv.visitCode();

		if (fields.length == 0) {
			emitThrow(mv, "No fields declared");
		}
		else {
			Label[] labels = createLabels(fields.length);
			Label defaultLabel = new Label();

			mv.visitVarInsn(ILOAD, 2);
			mv.visitTableSwitchInsn(0, fields.length - 1, defaultLabel, labels);

			for (int i = 0; i < fields.length; i++) {
				mv.visitLabel(labels[i]);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, targetInternal);
				Field f = fields[i];
				mv.visitFieldInsn(GETFIELD, targetInternal, f.getName(), Type.getDescriptor(f.getType()));
				emitBox(mv, f.getType());
				mv.visitInsn(ARETURN);
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid field index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void generateWriteByField(ClassWriter cw, String targetInternal, Field[] fields) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "writeByField",
				"(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
		mv.visitCode();

		if (fields.length == 0) {
			emitThrow(mv, "No fields declared");
		}
		else {
			Label[] labels = createLabels(fields.length);
			Label defaultLabel = new Label();

			mv.visitVarInsn(ILOAD, 2);
			mv.visitTableSwitchInsn(0, fields.length - 1, defaultLabel, labels);

			for (int i = 0; i < fields.length; i++) {
				mv.visitLabel(labels[i]);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, targetInternal);
				Field f = fields[i];
				mv.visitVarInsn(ALOAD, 3);
				emitUnboxOrCast(mv, f.getType());
				mv.visitFieldInsn(PUTFIELD, targetInternal, f.getName(), Type.getDescriptor(f.getType()));
				mv.visitInsn(RETURN);
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid field index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void generateReadByMethod(ClassWriter cw, String targetInternal, boolean isInterface, Method[] methods) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "readByMethod",
				"(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
		mv.visitCode();

		if (methods.length == 0) {
			emitThrow(mv, "No methods declared");
		}
		else {
			Label[] labels = createLabels(methods.length);
			Label defaultLabel = new Label();

			mv.visitVarInsn(ILOAD, 2);
			mv.visitTableSwitchInsn(0, methods.length - 1, defaultLabel, labels);

			for (int i = 0; i < methods.length; i++) {
				mv.visitLabel(labels[i]);
				Method m = methods[i];
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, targetInternal);
				int opcode = isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL;
				mv.visitMethodInsn(opcode, targetInternal, m.getName(),
						Type.getMethodDescriptor(m), isInterface);
				emitBox(mv, m.getReturnType());
				mv.visitInsn(ARETURN);
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid method index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void generateWriteByMethod(ClassWriter cw, String targetInternal, boolean isInterface, Method[] methods) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "writeByMethod",
				"(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
		mv.visitCode();

		if (methods.length == 0) {
			emitThrow(mv, "No methods declared");
		}
		else {
			Label[] labels = createLabels(methods.length);
			Label defaultLabel = new Label();

			mv.visitVarInsn(ILOAD, 2);
			mv.visitTableSwitchInsn(0, methods.length - 1, defaultLabel, labels);

			for (int i = 0; i < methods.length; i++) {
				mv.visitLabel(labels[i]);
				Method m = methods[i];
				mv.visitVarInsn(ALOAD, 1);
				mv.visitTypeInsn(CHECKCAST, targetInternal);
				mv.visitVarInsn(ALOAD, 3);
				emitUnboxOrCast(mv, m.getParameterTypes()[0]);
				int opcode = isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL;
				mv.visitMethodInsn(opcode, targetInternal, m.getName(),
						Type.getMethodDescriptor(m), isInterface);
				if (m.getReturnType() != void.class) {
					Type retType = Type.getType(m.getReturnType());
					mv.visitInsn(retType.getSize() == 2 ? POP2 : POP);
				}
				mv.visitInsn(RETURN);
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid method index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void generateNewInstance(ClassWriter cw, String targetInternal, Constructor<?>[] constructors) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "newInstance",
				"(I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		mv.visitCode();

		if (constructors.length == 0) {
			emitThrow(mv, "No constructors declared");
		}
		else {
			Label[] labels = createLabels(constructors.length);
			Label defaultLabel = new Label();

			mv.visitVarInsn(ILOAD, 1);
			mv.visitTableSwitchInsn(0, constructors.length - 1, defaultLabel, labels);

			for (int i = 0; i < constructors.length; i++) {
				mv.visitLabel(labels[i]);
				Constructor<?> ctor = constructors[i];
				mv.visitTypeInsn(NEW, targetInternal);
				mv.visitInsn(DUP);
				Class<?>[] paramTypes = ctor.getParameterTypes();
				for (int j = 0; j < paramTypes.length; j++) {
					mv.visitVarInsn(ALOAD, 2);
					emitIntConstant(mv, j);
					mv.visitInsn(AALOAD);
					emitUnboxOrCast(mv, paramTypes[j]);
				}
				mv.visitMethodInsn(INVOKESPECIAL, targetInternal, "<init>",
						Type.getConstructorDescriptor(ctor), false);
				mv.visitInsn(ARETURN);
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid constructor index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}


	private static void emitThrow(MethodVisitor mv, String message) {
		mv.visitTypeInsn(NEW, EXCEPTION_INTERNAL);
		mv.visitInsn(DUP);
		mv.visitLdcInsn(message);
		mv.visitMethodInsn(INVOKESPECIAL, EXCEPTION_INTERNAL, "<init>",
				"(Ljava/lang/String;)V", false);
		mv.visitInsn(ATHROW);
	}

	private static Label[] createLabels(int count) {
		Label[] labels = new Label[count];
		for (int i = 0; i < count; i++) {
			labels[i] = new Label();
		}
		return labels;
	}
}
