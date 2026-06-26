/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static org.hibernate.models.accessor.asm.impl.HibernateAccessorAsmUtils.*;

/**
 * For the single-class case ({@link #generateReader}/{@link #generateWriter}),
 * the generated class is defined as a nestmate of the target class and uses
 * direct field/method access.
 * <p>
 * For the cross-class case ({@link #generateBulkReader}/{@link #generateBulkWriter}),
 * the generated class holds {@link HibernateAccessorAsmBulkAccessor} fields and
 * calls their read/write methods with hardcoded indices.
 * <p>
 * Since at runtime we cannot redefine the already loaded "super class",
 * the only way to access those inherited nonvisible members is through the
 * already-generated-elsewhere-nestmate that goes through the switch dispatch
 * (see single member access).
 */
final class HibernateAccessorAsmMultiValueClassGenerator implements Opcodes {

	private static final String READER_INTERNAL = Type.getInternalName(HibernateAccessorMultiValueReader.class);
	private static final String WRITER_INTERNAL = Type.getInternalName(HibernateAccessorMultiValueWriter.class);
	private static final String BULK_ACCESSOR_INTERNAL = Type.getInternalName(HibernateAccessorAsmBulkAccessor.class);
	private static final String BULK_ACCESSOR_DESC = Type.getDescriptor(HibernateAccessorAsmBulkAccessor.class);

	// get(Object instance) -> Object[]
	// locals: [this=0, instance=1, result=2]
	static byte[] generateReader(Class<?> targetClass, Member[] members) {
		String targetInternal = Type.getInternalName(targetClass);
		String generatedName = targetInternal + "$$HibernateAccessorMultiReader";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, generatedName, null, "java/lang/Object", new String[]{READER_INTERNAL});

		generateConstructor(cw);

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;)[Ljava/lang/Object;", null, null);
		mv.visitCode();

		emitIntConstant(mv, members.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		mv.visitVarInsn(ASTORE, 2);

		for (int i = 0; i < members.length; i++) {
			mv.visitVarInsn(ALOAD, 2);
			emitIntConstant(mv, i);

			mv.visitVarInsn(ALOAD, 1);
			Member member = members[i];
			if (member instanceof Field field) {
				String ownerInternal = Type.getInternalName(field.getDeclaringClass());
				mv.visitTypeInsn(CHECKCAST, ownerInternal);
				mv.visitFieldInsn(GETFIELD, ownerInternal, field.getName(), Type.getDescriptor(field.getType()));
				emitBox(mv, field.getType());
			}
			else {
				Method method = (Method) member;
				String ownerInternal = Type.getInternalName(method.getDeclaringClass());
				boolean isInterface = method.getDeclaringClass().isInterface();
				mv.visitTypeInsn(CHECKCAST, ownerInternal);
				mv.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL, ownerInternal, method.getName(), Type.getMethodDescriptor(method), isInterface);
				emitBox(mv, method.getReturnType());
			}

			mv.visitInsn(AASTORE);
		}

		mv.visitVarInsn(ALOAD, 2);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	// set(Object instance, Object[] values) -> void
	// locals: [this=0, instance=1, values=2]
	static byte[] generateWriter(Class<?> targetClass, Member[] members) {
		String targetInternal = Type.getInternalName(targetClass);
		String generatedName = targetInternal + "$$HibernateAccessorMultiWriter";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, generatedName, null, "java/lang/Object", new String[]{WRITER_INTERNAL});

		generateConstructor(cw);

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;[Ljava/lang/Object;)V", null, null);
		mv.visitCode();

		for (int i = 0; i < members.length; i++) {
			mv.visitVarInsn(ALOAD, 1);
			Member member = members[i];
			if (member instanceof Field field) {
				String ownerInternal = Type.getInternalName(field.getDeclaringClass());
				mv.visitTypeInsn(CHECKCAST, ownerInternal);
				mv.visitVarInsn(ALOAD, 2);
				emitIntConstant(mv, i);
				mv.visitInsn(AALOAD);
				emitUnboxOrCast(mv, field.getType());
				mv.visitFieldInsn(PUTFIELD, ownerInternal, field.getName(), Type.getDescriptor(field.getType()));
			}
			else {
				Method method = (Method) member;
				String ownerInternal = Type.getInternalName(method.getDeclaringClass());
				boolean isInterface = method.getDeclaringClass().isInterface();
				mv.visitTypeInsn(CHECKCAST, ownerInternal);
				mv.visitVarInsn(ALOAD, 2);
				emitIntConstant(mv, i);
				mv.visitInsn(AALOAD);
				emitUnboxOrCast(mv, method.getParameterTypes()[0]);
				mv.visitMethodInsn(isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL, ownerInternal, method.getName(), Type.getMethodDescriptor(method), isInterface);
				if (method.getReturnType() != void.class) {
					Type retType = Type.getType(method.getReturnType());
					mv.visitInsn(retType.getSize() == 2 ? POP2 : POP);
				}
			}
		}

		mv.visitInsn(RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	static byte[] generateBulkReader(HibernateAccessorBulkMemberAccess[] accesses, int accessorFieldCount) {
		String generatedName = "org/hibernate/models/accessor/asm/impl/HibernateAccessorMultiBulkReader";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, generatedName, null, "java/lang/Object", new String[]{READER_INTERNAL});

		generateBulkAccessorFields(cw, accessorFieldCount);
		generateBulkAccessorConstructor(cw, generatedName, accessorFieldCount);

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;)[Ljava/lang/Object;", null, null);
		mv.visitCode();

		emitIntConstant(mv, accesses.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		mv.visitVarInsn(ASTORE, 2);

		for (int i = 0; i < accesses.length; i++) {
			HibernateAccessorBulkMemberAccess access = accesses[i];
			mv.visitVarInsn(ALOAD, 2);
			emitIntConstant(mv, i);

			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, generatedName, "accessor_" + access.accessorFieldIndex(), BULK_ACCESSOR_DESC);
			mv.visitVarInsn(ALOAD, 1);
			emitIntConstant(mv, access.memberIndex());
			mv.visitMethodInsn(INVOKEINTERFACE, BULK_ACCESSOR_INTERNAL, access.isField() ? "readByField" : "readByMethod", "(Ljava/lang/Object;I)Ljava/lang/Object;", true);

			mv.visitInsn(AASTORE);
		}

		mv.visitVarInsn(ALOAD, 2);
		mv.visitInsn(ARETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	static byte[] generateBulkWriter(HibernateAccessorBulkMemberAccess[] accesses, int accessorFieldCount) {
		String generatedName = "org/hibernate/models/accessor/asm/impl/HibernateAccessorMultiBulkWriter";

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V17, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, generatedName, null, "java/lang/Object", new String[]{WRITER_INTERNAL});

		generateBulkAccessorFields(cw, accessorFieldCount);
		generateBulkAccessorConstructor(cw, generatedName, accessorFieldCount);

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;[Ljava/lang/Object;)V", null, null);
		mv.visitCode();

		for (int i = 0; i < accesses.length; i++) {
			HibernateAccessorBulkMemberAccess access = accesses[i];
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, generatedName, "accessor_" + access.accessorFieldIndex(), BULK_ACCESSOR_DESC);
			mv.visitVarInsn(ALOAD, 1);
			emitIntConstant(mv, access.memberIndex());
			mv.visitVarInsn(ALOAD, 2);
			emitIntConstant(mv, i);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKEINTERFACE, BULK_ACCESSOR_INTERNAL, access.isField() ? "writeByField" : "writeByMethod", "(Ljava/lang/Object;ILjava/lang/Object;)V", true);
		}

		mv.visitInsn(RETURN);

		mv.visitMaxs(0, 0);
		mv.visitEnd();
		cw.visitEnd();
		return cw.toByteArray();
	}

	private static void generateBulkAccessorFields(ClassWriter cw, int count) {
		for (int i = 0; i < count; i++) {
			cw.visitField(ACC_PRIVATE | ACC_FINAL, "accessor_" + i, BULK_ACCESSOR_DESC, null, null).visitEnd();
		}
	}

	private static void generateBulkAccessorConstructor(ClassWriter cw, String className, int fieldCount) {
		StringBuilder ctorDesc = new StringBuilder("(");
		for (int i = 0; i < fieldCount; i++) {
			ctorDesc.append(BULK_ACCESSOR_DESC);
		}
		ctorDesc.append(")V");

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", ctorDesc.toString(), null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		for (int i = 0; i < fieldCount; i++) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, i + 1);
			mv.visitFieldInsn(PUTFIELD, className, "accessor_" + i, BULK_ACCESSOR_DESC);
		}
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
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
}
