/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import static org.hibernate.models.accessor.generator.impl.GenerationUtil.STRING_SWITCH_CHUNK_SIZE;
import static org.hibernate.models.accessor.generator.impl.GenerationUtil.emitStringSwitch;
import static org.hibernate.models.accessor.generator.impl.GenerationUtil.pushIntConst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FactoryGenerator implements Opcodes, GeneratorConstants {

	private final Map<String, String> dispatchTargets = new LinkedHashMap<>();
	private final Set<String> interfaceTargets = new HashSet<>();

	private final Set<String> fieldReaderClasses = new LinkedHashSet<>();
	private final Set<String> methodReaderClasses = new LinkedHashSet<>();
	private final Set<String> fieldWriterClasses = new LinkedHashSet<>();
	private final Set<String> methodWriterClasses = new LinkedHashSet<>();
	private final Set<String> instantiatorClasses = new LinkedHashSet<>();

	public void registerDispatchTarget(String declaringClassFqcn, String dispatchTargetInternal, boolean isInterface) {
		dispatchTargets.put( declaringClassFqcn, dispatchTargetInternal );
		if ( isInterface ) {
			interfaceTargets.add( dispatchTargetInternal );
		}
	}

	public void registerFieldReader(String declaringClassFqcn) {
		fieldReaderClasses.add( declaringClassFqcn );
	}

	public void registerMethodReader(String declaringClassFqcn) {
		methodReaderClasses.add( declaringClassFqcn );
	}

	public void registerFieldWriter(String declaringClassFqcn) {
		fieldWriterClasses.add( declaringClassFqcn );
	}

	public void registerMethodWriter(String declaringClassFqcn) {
		methodWriterClasses.add( declaringClassFqcn );
	}

	public void registerInstantiator(String declaringClassFqcn) {
		instantiatorClasses.add( declaringClassFqcn );
	}

	public byte[] generate() {
		ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES );

		cw.visit( V17, ACC_PUBLIC | ACC_SUPER, FACTORY_IMPLEMENTATION_INTERNAL, null,
				"java/lang/Object", new String[] { FACTORY_INTERFACE_INTERNAL } );

		cw.visitField( ACC_PRIVATE | ACC_STATIC | ACC_VOLATILE, "INSTANCE",
				"L" + FACTORY_INTERFACE_INTERNAL + ";", null, null ).visitEnd();

		generateConstructor( cw );
		generateReadResolve( cw );

		generateValueAccessor( cw, "valueReader", "java/lang/reflect/Field", "getName",
				METHOD_NAME_FIELD_READER_ACCESSOR, READER_INTERFACE_INTERNAL, fieldReaderClasses, null );
		generateValueAccessor( cw, "valueReader", "java/lang/reflect/Method", "getName",
				METHOD_NAME_METHOD_READER_ACCESSOR, READER_INTERFACE_INTERNAL, methodReaderClasses,
				"validateReaderMethod" );
		generateValueAccessor( cw, "valueWriter", "java/lang/reflect/Field", "getName",
				METHOD_NAME_FIELD_WRITER_ACCESSOR, WRITER_INTERFACE_INTERNAL, fieldWriterClasses, null );
		generateValueAccessor( cw, "valueWriter", "java/lang/reflect/Method", "getName",
				METHOD_NAME_METHOD_WRITER_ACCESSOR, WRITER_INTERFACE_INTERNAL, methodWriterClasses,
				"validateWriterMethod" );
		generateInstantiatorMethod( cw );

		generateMultiValueMethod( cw, "multiValueReader", MULTI_VALUE_READER_INTERNAL,
				"createMultiValueReader" );
		generateMultiValueMethod( cw, "multiValueWriter", MULTI_VALUE_WRITER_INTERNAL,
				"createMultiValueWriter" );

		cw.visitEnd();
		return cw.toByteArray();
	}

	private void generateConstructor(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "<init>", "()V", null, null );
		mv.visitCode();
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false );
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitFieldInsn( PUTSTATIC, FACTORY_IMPLEMENTATION_INTERNAL, "INSTANCE",
				"L" + FACTORY_INTERFACE_INTERNAL + ";" );
		mv.visitInsn( RETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private void generateReadResolve(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod( ACC_PRIVATE, "readResolve",
				"()Ljava/lang/Object;", null, null );
		mv.visitCode();
		mv.visitFieldInsn( GETSTATIC, FACTORY_IMPLEMENTATION_INTERNAL, "INSTANCE",
				"L" + FACTORY_INTERFACE_INTERNAL + ";" );
		mv.visitInsn( ARETURN );
		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private static final String MEMBER_VALIDATION_INTERNAL = GenerationUtil.fqcnToName(
			"org.hibernate.models.accessor.spi.MemberValidation" );

	private void generateValueAccessor(ClassWriter cw, String factoryMethodName,
			String reflectType, String memberNameMethod,
			String hostMethodName, String returnInterface,
			Set<String> classes, String validationMethod) {
		String returnDesc = "L" + returnInterface + ";";
		String hostMethodDesc = "(Ljava/lang/String;)" + returnDesc;

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, factoryMethodName,
				"(L" + reflectType + ";)L" + returnInterface + ";", null, null );
		mv.visitCode();

		if ( validationMethod != null ) {
			mv.visitVarInsn( ALOAD, 1 );
			mv.visitMethodInsn( INVOKESTATIC, MEMBER_VALIDATION_INTERNAL, validationMethod,
					"(L" + reflectType + ";)V", false );
		}

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKEVIRTUAL, reflectType, "getDeclaringClass",
				"()Ljava/lang/Class;", false );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/Class", "getName",
				"()Ljava/lang/String;", false );
		mv.visitVarInsn( ASTORE, 2 );

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKEVIRTUAL, reflectType, memberNameMethod,
				"()Ljava/lang/String;", false );
		mv.visitVarInsn( ASTORE, 3 );

		if ( classes.isEmpty() ) {
			emitThrow( mv );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
			return;
		}

		List<String> classNames = new ArrayList<>( classes );
		Label throwLabel = new Label();

		if ( classNames.size() <= STRING_SWITCH_CHUNK_SIZE ) {
			generateValueAccessorSwitch( mv, classNames, hostMethodName, hostMethodDesc,
					returnInterface, throwLabel );
		}
		else {
			generateValueAccessorChunked( cw, mv, factoryMethodName + "_" + hostMethodName,
					classNames, hostMethodName, hostMethodDesc, returnInterface, throwLabel );
		}

		mv.visitLabel( throwLabel );
		mv.visitFrame( F_FULL, 4,
				new Object[] { FACTORY_IMPLEMENTATION_INTERNAL, reflectType, "java/lang/String",
						"java/lang/String" },
				0, null );
		emitThrow( mv );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private void generateValueAccessorSwitch(MethodVisitor mv, List<String> classNames,
			String hostMethodName, String hostMethodDesc,
			String implInternal, Label throwLabel) {
		Label defaultLabel = new Label();

		emitStringSwitch( mv, 2, 4, classNames, defaultLabel, (caseMv, classIdx) -> {
			String className = classNames.get( classIdx );
			String target = dispatchTargets.get( className );
			boolean isIface = interfaceTargets.contains( target );

			caseMv.visitVarInsn( ALOAD, 3 );
			caseMv.visitMethodInsn( INVOKESTATIC, target, hostMethodName,
					hostMethodDesc, isIface );
			caseMv.visitInsn( DUP );
			Label notNull = new Label();
			caseMv.visitJumpInsn( IFNONNULL, notNull );
			caseMv.visitInsn( POP );
			caseMv.visitJumpInsn( GOTO, throwLabel );
			caseMv.visitLabel( notNull );
			caseMv.visitInsn( ARETURN );
		} );

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		mv.visitJumpInsn( GOTO, throwLabel );
	}

	private void generateValueAccessorChunked(ClassWriter cw, MethodVisitor mv,
			String chunkBaseName,
			List<String> classNames, String hostMethodName, String hostMethodDesc,
			String implInternal, Label throwLabel) {
		int numChunks = (classNames.size() + STRING_SWITCH_CHUNK_SIZE - 1) / STRING_SWITCH_CHUNK_SIZE;

		List<List<String>> chunks = new ArrayList<>();
		for ( int i = 0; i < numChunks; i++ ) {
			chunks.add( new ArrayList<>() );
		}
		for ( String className : classNames ) {
			int bucket = (className.hashCode() & 0x7FFFFFFF) % numChunks;
			chunks.get( bucket ).add( className );
		}

		String chunkMethodDesc = "(Ljava/lang/String;Ljava/lang/String;)" + "L" + implInternal + ";";

		for ( int i = 0; i < numChunks; i++ ) {
			if ( !chunks.get( i ).isEmpty() ) {
				generateValueAccessorChunkMethod( cw, chunkBaseName + "$" + i,
						chunkMethodDesc, chunks.get( i ), hostMethodName, hostMethodDesc, implInternal );
			}
		}

		Label defaultLabel = new Label();
		Label[] labels = new Label[numChunks];
		for ( int i = 0; i < numChunks; i++ ) {
			labels[i] = chunks.get( i ).isEmpty() ? defaultLabel : new Label();
		}

		mv.visitVarInsn( ALOAD, 2 );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false );
		mv.visitLdcInsn( 0x7FFFFFFF );
		mv.visitInsn( IAND );
		pushIntConst( mv, numChunks );
		mv.visitInsn( IREM );
		mv.visitTableSwitchInsn( 0, numChunks - 1, defaultLabel, labels );

		for ( int i = 0; i < numChunks; i++ ) {
			if ( !chunks.get( i ).isEmpty() ) {
				mv.visitLabel( labels[i] );
				mv.visitFrame( F_SAME, 0, null, 0, null );
				mv.visitVarInsn( ALOAD, 2 );
				mv.visitVarInsn( ALOAD, 3 );
				mv.visitMethodInsn( INVOKESTATIC, FACTORY_IMPLEMENTATION_INTERNAL, chunkBaseName + "$" + i,
						chunkMethodDesc, false );
				mv.visitInsn( DUP );
				Label notNull = new Label();
				mv.visitJumpInsn( IFNONNULL, notNull );
				mv.visitInsn( POP );
				mv.visitJumpInsn( GOTO, throwLabel );
				mv.visitLabel( notNull );
				mv.visitInsn( ARETURN );
			}
		}

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		mv.visitJumpInsn( GOTO, throwLabel );
	}

	private void generateValueAccessorChunkMethod(ClassWriter cw, String methodName,
			String methodDesc, List<String> classNames,
			String hostMethodName, String hostMethodDesc, String implInternal) {
		MethodVisitor mv = cw.visitMethod( ACC_PRIVATE | ACC_STATIC, methodName,
				methodDesc, null, null );
		mv.visitCode();

		Label defaultLabel = new Label();

		emitStringSwitch( mv, 0, 2, classNames, defaultLabel, (caseMv, classIdx) -> {
			String className = classNames.get( classIdx );
			String target = dispatchTargets.get( className );
			boolean isIface = interfaceTargets.contains( target );

			caseMv.visitVarInsn( ALOAD, 1 );
			caseMv.visitMethodInsn( INVOKESTATIC, target, hostMethodName,
					hostMethodDesc, isIface );
			caseMv.visitInsn( ARETURN );
		} );

		mv.visitLabel( defaultLabel );
		mv.visitFrame( F_SAME, 0, null, 0, null );
		mv.visitInsn( ACONST_NULL );
		mv.visitInsn( ARETURN );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private void generateInstantiatorMethod(ClassWriter cw) {
		String hostMethodDesc = "(Ljava/lang/String;)L" + INSTANTIATOR_INTERFACE_INTERNAL + ";";

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC, "instantiator",
				"(Ljava/lang/reflect/Constructor;)L" + INSTANTIATOR_INTERFACE_INTERNAL + ";",
				null, null );
		mv.visitCode();

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/reflect/Constructor", "getDeclaringClass",
				"()Ljava/lang/Class;", false );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/Class", "getName",
				"()Ljava/lang/String;", false );
		mv.visitVarInsn( ASTORE, 2 );

		mv.visitVarInsn( ALOAD, 1 );
		mv.visitMethodInsn( INVOKESTATIC, NAMING_UTIL_INTERNAL, "constructorDescriptor",
				"(Ljava/lang/reflect/Constructor;)Ljava/lang/String;", false );
		mv.visitVarInsn( ASTORE, 3 );

		if ( instantiatorClasses.isEmpty() ) {
			emitThrow( mv );
			mv.visitMaxs( 0, 0 );
			mv.visitEnd();
			return;
		}

		List<String> classNames = new ArrayList<>( instantiatorClasses );
		Label throwLabel = new Label();

		if ( classNames.size() <= STRING_SWITCH_CHUNK_SIZE ) {
			Label defaultLabel = new Label();

			emitStringSwitch( mv, 2, 4, classNames, defaultLabel, (caseMv, classIdx) -> {
				String className = classNames.get( classIdx );
				String target = dispatchTargets.get( className );
				boolean isIface = interfaceTargets.contains( target );

				caseMv.visitVarInsn( ALOAD, 3 );
				caseMv.visitMethodInsn( INVOKESTATIC, target, METHOD_NAME_INSTANTIATOR_ACCESSOR,
						hostMethodDesc, isIface );
				caseMv.visitInsn( DUP );
				Label notNull = new Label();
				caseMv.visitJumpInsn( IFNONNULL, notNull );
				caseMv.visitInsn( POP );
				caseMv.visitJumpInsn( GOTO, throwLabel );
				caseMv.visitLabel( notNull );
				caseMv.visitInsn( ARETURN );
			} );

			mv.visitLabel( defaultLabel );
			mv.visitFrame( F_SAME, 0, null, 0, null );
			mv.visitJumpInsn( GOTO, throwLabel );
		}
		else {
			generateValueAccessorChunked( cw, mv, "instantiator_" + METHOD_NAME_INSTANTIATOR_ACCESSOR,
					classNames, METHOD_NAME_INSTANTIATOR_ACCESSOR, hostMethodDesc,
					INSTANTIATOR_INTERFACE_INTERNAL, throwLabel );
		}

		mv.visitLabel( throwLabel );
		mv.visitFrame( F_FULL, 4,
				new Object[] { FACTORY_IMPLEMENTATION_INTERNAL, "java/lang/reflect/Constructor", "java/lang/String",
						"java/lang/String" },
				0, null );
		emitThrow( mv );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private static final String MULTI_VALUE_HELPER_INTERNAL = GenerationUtil.fqcnToName(
			"org.hibernate.models.accessor.generator.runtime.MultiValueHelper" );
	private static final String MULTI_VALUE_READER_INTERNAL = GenerationUtil.fqcnToName(
			"org.hibernate.models.accessor.HibernateAccessorMultiValueReader" );
	private static final String MULTI_VALUE_WRITER_INTERNAL = GenerationUtil.fqcnToName(
			"org.hibernate.models.accessor.HibernateAccessorMultiValueWriter" );

	private void generateMultiValueMethod(ClassWriter cw, String methodName, String returnInternal,
			String helperMethodName) {
		String descriptor = "(Ljava/lang/Class;[Ljava/lang/reflect/Member;)L" + returnInternal + ";";

		MethodVisitor mv = cw.visitMethod( ACC_PUBLIC | ACC_VARARGS, methodName, descriptor, null, null );
		mv.visitCode();

		// Call MultiValueHelper.createMultiValueReader/Writer(this, declaringClass, members)
		mv.visitVarInsn( ALOAD, 0 );
		mv.visitVarInsn( ALOAD, 1 );
		mv.visitVarInsn( ALOAD, 2 );
		mv.visitMethodInsn( INVOKESTATIC, MULTI_VALUE_HELPER_INTERNAL, helperMethodName,
				"(L" + FACTORY_INTERFACE_INTERNAL + ";Ljava/lang/Class;[Ljava/lang/reflect/Member;)L"
						+ returnInternal + ";",
				false );
		mv.visitInsn( ARETURN );

		mv.visitMaxs( 0, 0 );
		mv.visitEnd();
	}

	private static void emitThrow(MethodVisitor mv) {
		mv.visitTypeInsn( NEW, "java/lang/UnsupportedOperationException" );
		mv.visitInsn( DUP );
		mv.visitVarInsn( ALOAD, 2 );
		mv.visitLdcInsn( "." );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "concat",
				"(Ljava/lang/String;)Ljava/lang/String;", false );
		mv.visitVarInsn( ALOAD, 3 );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "concat",
				"(Ljava/lang/String;)Ljava/lang/String;", false );
		mv.visitMethodInsn( INVOKESPECIAL, "java/lang/UnsupportedOperationException",
				"<init>", "(Ljava/lang/String;)V", false );
		mv.visitInsn( ATHROW );
	}
}
