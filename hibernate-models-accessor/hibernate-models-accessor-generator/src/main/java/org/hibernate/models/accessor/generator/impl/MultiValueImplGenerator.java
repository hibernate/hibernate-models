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

public class MultiValueImplGenerator implements Opcodes, GeneratorConstants {

	public record ResolvedMemberAccess(
			String dispatchTargetInternal,
			boolean isInterface,
			int memberIndex) {
	}

	public record ProcessedMultiValueGroup(
			String descriptor,
			int groupIndex,
			List<ResolvedMemberAccess> members) {
	}

	public byte[] generateMultiValueReaderImpl(List<ProcessedMultiValueGroup> groups) {
		String className = fqcnToName( GENERATED_MULTI_VALUE_READER_IMPL );
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit( V17, ACC_PUBLIC | ACC_SUPER, className,
				null,
				"java/lang/Object", new String[] { MULTI_VALUE_READER_INTERFACE_INTERNAL } );

		generateGroupIndexField( cw );
		generateGroupIndexConstructor( cw, className );

		if ( groups.size() <= SWITCH_CHUNK_SIZE ) {
			MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "get",
					"(Ljava/lang/Object;)[Ljava/lang/Object;", null, null );
			mv.visitCode();
			generateReaderDispatch( mv, className, groups, 0 );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		else {
			generateChunkedReaderDispatch( cw, className, groups );
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	public byte[] generateMultiValueWriterImpl(List<ProcessedMultiValueGroup> groups) {
		String className = fqcnToName( GENERATED_MULTI_VALUE_WRITER_IMPL );
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit( V17, ACC_PUBLIC | ACC_SUPER, className,
				null,
				"java/lang/Object", new String[] { MULTI_VALUE_WRITER_INTERFACE_INTERNAL } );

		generateGroupIndexField( cw );
		generateGroupIndexConstructor( cw, className );

		if ( groups.size() <= SWITCH_CHUNK_SIZE ) {
			MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "set",
					"(Ljava/lang/Object;[Ljava/lang/Object;)V", null, null );
			mv.visitCode();
			generateWriterDispatch( mv, className, groups, 0 );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}
		else {
			generateChunkedWriterDispatch( cw, className, groups );
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	private static void generateGroupIndexField(ClassWriter cw) {
		cw.visitField( ACC_PRIVATE | ACC_FINAL, "groupIndex", "I", null, null ).visitEnd();
	}

	private static void generateGroupIndexConstructor(ClassWriter cw, String className) {
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "<init>", "(I)V", null, null );
		mv.visitCode();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false );

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ILOAD, 1 );
		mv.visitFieldInsn( PUTFIELD, className, "groupIndex", "I" );

		mv.visitInsn( RETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	// get(Object instance) -> Object[]
	// locals: [this=0, instance=1, result=2]
	private static void generateReaderDispatch(MethodVisitor mv, String className,
			List<ProcessedMultiValueGroup> groups, int indexOffset) {
		int count = groups.size();
		if ( count == 0 ) {
			throwIllegalArgumentWithGroupIndex( mv, className );
			return;
		}

		Label[] labels = new Label[count];
		for ( int i = 0; i < count; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "groupIndex", "I" );
		mv.visitTableSwitchInsn( indexOffset, indexOffset + count - 1, defaultLabel, labels );

		for ( int i = 0; i < count; i++ ) {
			ProcessedMultiValueGroup group = groups.get( i );
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			int memberCount = group.members().size();
			pushIntConst( mv, memberCount );
			mv.visitTypeInsn( ANEWARRAY, "java/lang/Object" );
			mv.visitVarInsn( ASTORE, 2 );

			for ( int m = 0; m < memberCount; m++ ) {
				ResolvedMemberAccess access = group.members().get( m );

				mv.visitVarInsn( ALOAD, 2 );
				pushIntConst( mv, m );

				pushIntConst( mv, access.memberIndex() );
				mv.visitVarInsn( ALOAD, 1 );
				mv.visitMethodInsn( INVOKESTATIC, access.dispatchTargetInternal(),
						PREFIX_READ_METHOD, "(ILjava/lang/Object;)Ljava/lang/Object;",
						access.isInterface() );

				mv.visitInsn( AASTORE );
			}

			mv.visitVarInsn( ALOAD, 2 );
			mv.visitInsn( ARETURN );
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithGroupIndex( mv, className );
	}

	// set(Object instance, Object[] values) -> void
	// locals: [this=0, instance=1, values=2]
	private static void generateWriterDispatch(MethodVisitor mv, String className,
			List<ProcessedMultiValueGroup> groups, int indexOffset) {
		int count = groups.size();
		if ( count == 0 ) {
			throwIllegalArgumentWithGroupIndex( mv, className );
			return;
		}

		Label[] labels = new Label[count];
		for ( int i = 0; i < count; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "groupIndex", "I" );
		mv.visitTableSwitchInsn( indexOffset, indexOffset + count - 1, defaultLabel, labels );

		for ( int i = 0; i < count; i++ ) {
			ProcessedMultiValueGroup group = groups.get( i );
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			for ( int m = 0; m < group.members().size(); m++ ) {
				ResolvedMemberAccess access = group.members().get( m );

				pushIntConst( mv, access.memberIndex() );
				mv.visitVarInsn( ALOAD, 1 );
				mv.visitVarInsn( ALOAD, 2 );
				pushIntConst( mv, m );
				mv.visitInsn( AALOAD );
				mv.visitMethodInsn( INVOKESTATIC, access.dispatchTargetInternal(),
						PREFIX_WRITE_METHOD, "(ILjava/lang/Object;Ljava/lang/Object;)V",
						access.isInterface() );
			}

			mv.visitInsn( RETURN );
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithGroupIndex( mv, className );
	}

	private void generateChunkedReaderDispatch(ClassWriter cw, String className,
			List<ProcessedMultiValueGroup> groups) {
		int total = groups.size();
		int chunkCount = (total + SWITCH_CHUNK_SIZE - 1) / SWITCH_CHUNK_SIZE;
		String methodDesc = "(Ljava/lang/Object;)[Ljava/lang/Object;";

		for ( int chunk = 0; chunk < chunkCount; chunk++ ) {
			int start = chunk * SWITCH_CHUNK_SIZE;
			int end = Math.min( start + SWITCH_CHUNK_SIZE, total );

			MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "get$" + chunk, methodDesc, null, null );
			mv.visitCode();
			generateReaderDispatch( mv, className, groups.subList( start, end ), start );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		generateImplChunkDispatcher( cw, className, "get", methodDesc, chunkCount, false );
	}

	private void generateChunkedWriterDispatch(ClassWriter cw, String className,
			List<ProcessedMultiValueGroup> groups) {
		int total = groups.size();
		int chunkCount = (total + SWITCH_CHUNK_SIZE - 1) / SWITCH_CHUNK_SIZE;
		String methodDesc = "(Ljava/lang/Object;[Ljava/lang/Object;)V";

		for ( int chunk = 0; chunk < chunkCount; chunk++ ) {
			int start = chunk * SWITCH_CHUNK_SIZE;
			int end = Math.min( start + SWITCH_CHUNK_SIZE, total );

			MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "set$" + chunk, methodDesc, null, null );
			mv.visitCode();
			generateWriterDispatch( mv, className, groups.subList( start, end ), start );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
		}

		generateImplChunkDispatcher( cw, className, "set", methodDesc, chunkCount, true );
	}

	private static void generateImplChunkDispatcher(ClassWriter cw, String className,
			String publicMethodName, String publicMethodDesc, int chunkCount, boolean returnsVoid) {
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, publicMethodName, publicMethodDesc, null, null );
		mv.visitCode();

		Label[] labels = new Label[chunkCount];
		for ( int i = 0; i < chunkCount; i++ ) {
			labels[i] = new Label();
		}
		Label defaultLabel = new Label();

		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, className, "groupIndex", "I" );
		pushIntConst( mv, SWITCH_CHUNK_SIZE );
		mv.visitInsn( IDIV );
		mv.visitTableSwitchInsn( 0, chunkCount - 1, defaultLabel, labels );

		for ( int i = 0; i < chunkCount; i++ ) {
			mv.visitLabel( labels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			mv.visitVarInsn( ALOAD, 0 );
			mv.visitVarInsn( ALOAD, 1 );
			if ( !returnsVoid ) {
				mv.visitMethodInsn( INVOKEVIRTUAL, className, publicMethodName + "$" + i,
						publicMethodDesc, false );
				mv.visitInsn( ARETURN );
			}
			else {
				mv.visitVarInsn( ALOAD, 2 );
				mv.visitMethodInsn( INVOKEVIRTUAL, className, publicMethodName + "$" + i,
						publicMethodDesc, false );
				mv.visitInsn( RETURN );
			}
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		throwIllegalArgumentWithGroupIndex( mv, className );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private static void throwIllegalArgumentWithGroupIndex(MethodVisitor mv, String implClassName) {
		mv.visitTypeInsn( NEW, "java/lang/IllegalArgumentException" );
		mv.visitInsn( DUP );
		mv.visitTypeInsn( NEW, "java/lang/StringBuilder" );
		mv.visitInsn( DUP );
		mv.visitLdcInsn( "Unknown group index " );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
				"(Ljava/lang/String;)V", false );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( GETFIELD, implClassName, "groupIndex", "I" );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
				"(I)Ljava/lang/StringBuilder;", false );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
				"()Ljava/lang/String;", false );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>",
				"(Ljava/lang/String;)V", false );
		mv.visitInsn( ATHROW );
	}
}
