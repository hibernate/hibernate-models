/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.hibernate.models.accessor.asm.impl.HibernateAccessorAsmUtils.*;

final class HibernateAccessorAsmBulkAccessorClassGenerator implements Opcodes {

	private static final String BULK_ACCESSOR_INTERNAL = Type.getInternalName(HibernateAccessorAsmBulkAccessor.class);
	private static final String EXCEPTION_INTERNAL = "org/hibernate/models/accessor/HibernateAccessorException";

	static byte[] generate(Class<?> targetClass, Field[] fields, Method[] methods, Constructor<?>[] constructors) {
		String targetInternal = Type.getInternalName(targetClass);
		boolean isInterface = targetClass.isInterface();
		String generatedName = targetInternal + "$$HibernateAccessor";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, generatedName, null,
				"java/lang/Object", new String[]{BULK_ACCESSOR_INTERNAL});

		generateConstructor(cw);
		generateReadByField(cw, generatedName, targetInternal, fields);
		generateWriteByField(cw, targetInternal, fields);
		generateReadByMethod(cw, targetInternal, isInterface, methods);
		generateWriteByMethod(cw, targetInternal, isInterface, methods);
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

	// readByField(Object instance, int index) -> Object
	// locals: [this=0, instance=1, index=2]
	private static void generateReadByField(ClassWriter cw, String generatedName,
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

	// writeByField(Object instance, int index, Object value) -> void
	// locals: [this=0, instance=1, index=2, value=3]
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

	// readByMethod(Object instance, int index) -> Object
	// locals: [this=0, instance=1, index=2]
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
				if (m.getParameterCount() != 0 || m.getReturnType() == void.class) {
					emitThrow(mv, "Method " + m.getName() + " is not a zero-arg getter");
				}
				else {
					mv.visitVarInsn(ALOAD, 1);
					mv.visitTypeInsn(CHECKCAST, targetInternal);
					int opcode = isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL;
					mv.visitMethodInsn(opcode, targetInternal, m.getName(),
							Type.getMethodDescriptor(m), isInterface);
					emitBox(mv, m.getReturnType());
					mv.visitInsn(ARETURN);
				}
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid method index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	// writeByMethod(Object instance, int index, Object value) -> void
	// locals: [this=0, instance=1, index=2, value=3]
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
				if (m.getParameterCount() != 1) {
					emitThrow(mv, "Method " + m.getName() + " is not a single-arg setter");
				}
				else {
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
			}

			mv.visitLabel(defaultLabel);
			emitThrow(mv, "Invalid method index");
		}

		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	// newInstance(int index, Object[] args) -> Object
	// locals: [this=0, index=1, args=2]
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
