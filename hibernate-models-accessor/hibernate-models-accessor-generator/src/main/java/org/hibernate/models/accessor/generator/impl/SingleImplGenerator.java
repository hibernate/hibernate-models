/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import static org.hibernate.models.accessor.generator.impl.GenerationUtil.SWITCH_CHUNK_SIZE;
import static org.hibernate.models.accessor.generator.impl.GenerationUtil.fqcnToName;
import static org.hibernate.models.accessor.generator.impl.GenerationUtil.pushIntConst;

import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.hibernate.models.accessor.generator.AccessorClassMetadata.TypeMetadata;

public class SingleImplGenerator implements Opcodes, GeneratorConstants {

	public byte[] generateReaderImpl(List<ProcessedHostData> hostClasses) {
		String className = fqcnToName( GENERATED_READER_IMPL );
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit( V17, ACC_PUBLIC | ACC_SUPER, className,
				"Ljava/lang/Object;L" + READER_INTERFACE_INTERNAL + "<Ljava/lang/Object;>;",
				"java/lang/Object", new String[] { READER_INTERFACE_INTERNAL } );

		generateIndexFields( cw );
		generateIndexConstructor( cw, className );

		String methodDesc = "(Ljava/lang/Object;)Ljava/lang/Object;";
		if ( hostClasses.size() <= SWITCH_CHUNK_SIZE ) {
			MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "get", methodDesc, null, null );
			mv.visitCode();
			generateDispatchSwitch( mv, className, hostClasses, PREFIX_READ_METHOD,
					"(ILjava/lang/Object;)Ljava/lang/Object;", 1 );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		else {
			generateChunkedDispatch( cw, className, "get", methodDesc, hostClasses, PREFIX_READ_METHOD,
					"(ILjava/lang/Object;)Ljava/lang/Object;", 1, false );
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	public byte[] generateWriterImpl(List<ProcessedHostData> hostClasses) {
		String className = fqcnToName( GENERATED_WRITER_IMPL );
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit( V17, ACC_PUBLIC | ACC_SUPER, className,
				null,
				"java/lang/Object", new String[] { WRITER_INTERFACE_INTERNAL } );

		generateIndexFields( cw );
		generateIndexConstructor( cw, className );

		if ( hostClasses.size() <= SWITCH_CHUNK_SIZE ) {
			MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "set",
					"(Ljava/lang/Object;Ljava/lang/Object;)V", null, null );
			mv.visitCode();
			generateWriteDispatchSwitch( mv, className, hostClasses );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		else {
			generateChunkedWriteDispatch( cw, className, hostClasses );
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	public byte[] generateInstantiatorImpl(List<ProcessedHostData> hostClasses) {
		String className = fqcnToName( GENERATED_INSTANTIATOR_IMPL );
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit( V17, ACC_PUBLIC | ACC_SUPER, className,
				"Ljava/lang/Object;L" + INSTANTIATOR_INTERFACE_INTERNAL + "<Ljava/lang/Object;>;",
				"java/lang/Object", new String[] { INSTANTIATOR_INTERFACE_INTERNAL } );

		generateIndexFields( cw );
		generateIndexConstructor( cw, className );

		String methodDesc = "([Ljava/lang/Object;)Ljava/lang/Object;";
		if ( hostClasses.size() <= SWITCH_CHUNK_SIZE ) {
			MethodVisitor mv = cw.visitMethod( ACC_PUBLIC | ACC_VARARGS, "create", methodDesc, null, null );
			mv.visitCode();
			generateDispatchSwitch( mv, className, hostClasses, PREFIX_CREATE_METHOD,
					"(I[Ljava/lang/Object;)Ljava/lang/Object;", 1 );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		else {
			generateChunkedDispatch( cw, className, "create", methodDesc, hostClasses, PREFIX_CREATE_METHOD,
					"(I[Ljava/lang/Object;)Ljava/lang/Object;", 1, false );
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	private static void generateIndexFields(ClassWriter cw) {
		cw.visitField( ACC_PRIVATE | ACC_FINAL, "classIndex", "I", null, null ).visitEnd();
		cw.visitField( ACC_PRIVATE | ACC_FINAL, "memberIndex", "I", null, null ).visitEnd();
	}

	private static void generateIndexConstructor(ClassWriter cw, String className) {
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "<init>", "(II)V", null, null );
		mv.visitCode();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ILOAD, 1 );
		mv.visitFieldInsn( PUTFIELD, className, "classIndex", "I" );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ILOAD, 2 );
		mv.visitFieldInsn( PUTFIELD, className, "memberIndex", "I" );

		mv.visitInsn( RETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private static void generateDispatchSwitch(MethodVisitor mv, String className,
			List<ProcessedHostData> hostClasses,
			String staticMethodName, String staticMethodDesc, int targetArgSlot) {
		int count = hostClasses.size();
		if ( count == 0 ) {
			throwIllegalArgumentWithClassIndex( mv, className );
			return;
		}
		Label[] labels = new Label[count];
		for ( int i = 0; i < count; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "classIndex", "I" );
		mv.visitTableSwitchInsn( 0, count - 1, defaultLabel, labels );

		for ( int i = 0; i < count; i++ ) {
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );
			TypeMetadata type = hostClasses.get( i ).type();

			String hostClass = fqcnToName( type.dispatchTarget() );
			boolean isInterface = type.isInterface();

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, className, "memberIndex", "I" );
			mv.visitVarInsn( ALOAD, targetArgSlot );
			mv.visitMethodInsn( INVOKESTATIC, hostClass, staticMethodName, staticMethodDesc, isInterface );
			mv.visitInsn( ARETURN );
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithClassIndex( mv, className );
	}

	private static void generateWriteDispatchSwitch(MethodVisitor mv, String className,
			List<ProcessedHostData> hostClasses) {
		int count = hostClasses.size();
		if ( count == 0 ) {
			throwIllegalArgumentWithClassIndex( mv, className );
			return;
		}
		Label[] labels = new Label[count];
		for ( int i = 0; i < count; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "classIndex", "I" );
		mv.visitTableSwitchInsn( 0, count - 1, defaultLabel, labels );

		for ( int i = 0; i < count; i++ ) {
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			TypeMetadata type = hostClasses.get( i ).type();
			String hostClass = fqcnToName( type.dispatchTarget() );
			boolean isInterface = type.isInterface();

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, className, "memberIndex", "I" );
			mv.visitVarInsn( ALOAD, 1 );
			mv.visitVarInsn( ALOAD, 2 );
			mv.visitMethodInsn( INVOKESTATIC, hostClass, PREFIX_WRITE_METHOD,
					"(ILjava/lang/Object;Ljava/lang/Object;)V", isInterface );
			mv.visitInsn( RETURN );
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithClassIndex( mv, className );
	}

	private void generateChunkedDispatch(ClassWriter cw, String className, String publicMethodName,
			String publicMethodDesc,
			List<ProcessedHostData> hostClasses, String staticMethodName, String staticMethodDesc, int targetArgSlot,
			boolean returnsVoid) {
		int total = hostClasses.size();
		int chunkCount = (total + SWITCH_CHUNK_SIZE - 1) / SWITCH_CHUNK_SIZE;

		for ( int chunk = 0; chunk < chunkCount; chunk++ ) {
			int start = chunk * SWITCH_CHUNK_SIZE;
			int end = Math.min( start + SWITCH_CHUNK_SIZE, total );
			List<ProcessedHostData> chunkHosts = hostClasses.subList( start, end );

			String chunkMethodName = publicMethodName + "$" + chunk;
			MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, chunkMethodName, publicMethodDesc, null, null );
			mv.visitCode();
			generateDispatchSwitchWithOffset( mv, className, chunkHosts, staticMethodName, staticMethodDesc,
					targetArgSlot, start );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		generateImplChunkDispatcher( cw, className, publicMethodName, publicMethodDesc,
				chunkCount, returnsVoid );
	}

	private void generateChunkedWriteDispatch(ClassWriter cw, String className,
			List<ProcessedHostData> hostClasses) {
		int total = hostClasses.size();
		int chunkCount = (total + SWITCH_CHUNK_SIZE - 1) / SWITCH_CHUNK_SIZE;
		String publicMethodDesc = "(Ljava/lang/Object;Ljava/lang/Object;)V";

		for ( int chunk = 0; chunk < chunkCount; chunk++ ) {
			int start = chunk * SWITCH_CHUNK_SIZE;
			int end = Math.min( start + SWITCH_CHUNK_SIZE, total );
			List<ProcessedHostData> chunkHosts = hostClasses.subList( start, end );

			String chunkMethodName = "set$" + chunk;
			MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, chunkMethodName, publicMethodDesc, null, null );
			mv.visitCode();
			generateWriteDispatchSwitchWithOffset( mv, className, chunkHosts, start );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		generateImplChunkDispatcher( cw, className, "set", publicMethodDesc, chunkCount, true );
	}

	private static void generateDispatchSwitchWithOffset(MethodVisitor mv, String className,
			List<ProcessedHostData> hostClasses,
			String staticMethodName, String staticMethodDesc, int targetArgSlot, int indexOffset) {
		int count = hostClasses.size();
		Label[] labels = new Label[count];
		for ( int i = 0; i < count; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "classIndex", "I" );
		mv.visitTableSwitchInsn( indexOffset, indexOffset + count - 1, defaultLabel, labels );

		for ( int i = 0; i < count; i++ ) {
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			TypeMetadata type = hostClasses.get( i ).type();
			String hostClass = fqcnToName( type.dispatchTarget() );
			boolean isInterface = type.isInterface();

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, className, "memberIndex", "I" );
			mv.visitVarInsn( ALOAD, targetArgSlot );
			mv.visitMethodInsn( INVOKESTATIC, hostClass, staticMethodName, staticMethodDesc, isInterface );
			mv.visitInsn( ARETURN );
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithClassIndex( mv, className );
	}

	private static void generateWriteDispatchSwitchWithOffset(MethodVisitor mv, String className,
			List<ProcessedHostData> hostClasses,
			int indexOffset) {
		int count = hostClasses.size();
		Label[] labels = new Label[count];
		for ( int i = 0; i < count; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "classIndex", "I" );
		mv.visitTableSwitchInsn( indexOffset, indexOffset + count - 1, defaultLabel, labels );

		for ( int i = 0; i < count; i++ ) {
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			TypeMetadata type = hostClasses.get( i ).type();
			String hostClass = fqcnToName( type.dispatchTarget() );
			boolean isInterface = type.isInterface();

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitFieldInsn( GETFIELD, className, "memberIndex", "I" );
			mv.visitVarInsn( ALOAD, 1 );
			mv.visitVarInsn( ALOAD, 2 );
			mv.visitMethodInsn( INVOKESTATIC, hostClass, PREFIX_WRITE_METHOD,
					"(ILjava/lang/Object;Ljava/lang/Object;)V", isInterface );
			mv.visitInsn( RETURN );
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithClassIndex( mv, className );
	}

	private static void generateImplChunkDispatcher(ClassWriter cw, String className, String publicMethodName,
			String publicMethodDesc, int chunkCount, boolean returnsVoid) {
		int accessFlags = ACC_PUBLIC;
		if ( publicMethodDesc.startsWith( "([" ) ) {
			accessFlags |= ACC_VARARGS;
		}
		MethodVisitor mv = cw.visitMethod( accessFlags, publicMethodName, publicMethodDesc, null, null );
		mv.visitCode();

		Label[] labels = new Label[chunkCount];
		for ( int i = 0; i < chunkCount; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "classIndex", "I" );
		pushIntConst( mv, SWITCH_CHUNK_SIZE );
		mv.visitInsn( IDIV );
		mv.visitTableSwitchInsn( 0, chunkCount - 1, defaultLabel, labels );

		Type[] argTypes = Type.getArgumentTypes( publicMethodDesc );

		for ( int i = 0; i < chunkCount; i++ ) {
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			mv.visitVarInsn( ALOAD, 0 );
			for ( int a = 0, slot = 1; a < argTypes.length; a++ ) {
				mv.visitVarInsn( argTypes[a].getOpcode( ILOAD ), slot );
				slot += argTypes[a].getSize();
			}

			mv.visitMethodInsn( INVOKEVIRTUAL, className, publicMethodName + "$" + i, publicMethodDesc, false );

			if ( returnsVoid ) {
				mv.visitInsn( RETURN );
			}
			else {
				mv.visitInsn( ARETURN );
			}
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithClassIndex( mv, className );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private static void throwIllegalArgumentWithClassIndex(MethodVisitor mv, String implClassName) {
		mv.visitTypeInsn( NEW, "java/lang/IllegalArgumentException" );
		mv.visitInsn( DUP );
		mv.visitTypeInsn( NEW, "java/lang/StringBuilder" );
		mv.visitInsn( DUP );
		mv.visitLdcInsn( "Unknown class index " );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, implClassName, "classIndex", "I" );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;",
				false );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V",
				false );
		mv.visitInsn( ATHROW );
	}
}
