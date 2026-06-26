/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

final class GenerationUtil implements Opcodes {

	static final int SWITCH_CHUNK_SIZE = 1000;
	static final int STRING_SWITCH_CHUNK_SIZE = 500;

	private GenerationUtil() {
	}

	static String fqcnToName(String fqcn) {
		return fqcn.replace( '.', '/' );
	}

	static String nameToFqcn(String name) {
		return name.replace( '/', '.' );
	}

	static void pushIntConst(MethodVisitor mv, int value) {
		if ( value >= -1 && value <= 5 ) {
			mv.visitInsn( ICONST_0 + value );
		}
		else if ( value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE ) {
			mv.visitIntInsn( BIPUSH, value );
		}
		else if ( value >= Short.MIN_VALUE && value <= Short.MAX_VALUE ) {
			mv.visitIntInsn( SIPUSH, value );
		}
		else {
			mv.visitLdcInsn( value );
		}
	}

	@FunctionalInterface
	interface CaseBodyEmitter {
		void emit(MethodVisitor mv, int caseIndex);
	}

	static void emitStringSwitch(
			MethodVisitor mv,
			int stringSlot,
			int tempSlot,
			List<String> cases,
			Label defaultLabel,
			CaseBodyEmitter bodyEmitter) {
		if ( cases.isEmpty() ) {
			mv.visitJumpInsn( GOTO, defaultLabel );
			return;
		}

		TreeMap<Integer, List<Integer>> hashToCaseIndices = new TreeMap<>();
		for ( int i = 0; i < cases.size(); i++ ) {
			int hash = cases.get( i ).hashCode();
			hashToCaseIndices.computeIfAbsent( hash, k -> new ArrayList<>() ).add( i );
		}

		int[] hashes = hashToCaseIndices.keySet().stream().mapToInt( Integer::intValue ).toArray();
		Label[] hashLabels = new Label[hashes.length];
		for ( int i = 0; i < hashes.length; i++ ) {
			hashLabels[i] = new Label();
		}

		Label secondSwitch = new Label();

		pushIntConst( mv, -1 );
		mv.visitVarInsn( ISTORE, tempSlot );

		mv.visitVarInsn( ALOAD, stringSlot );
		mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false );
		mv.visitLookupSwitchInsn( secondSwitch, hashes, hashLabels );

		for ( int h = 0; h < hashes.length; h++ ) {
			mv.visitLabel( hashLabels[h] );
			mv.visitFrame( F_SAME, 0, null, 0, null );

			List<Integer> caseIndices = hashToCaseIndices.get( hashes[h] );
			for ( int c = 0; c < caseIndices.size(); c++ ) {
				int ci = caseIndices.get( c );
				boolean last = (c == caseIndices.size() - 1);

				mv.visitVarInsn( ALOAD, stringSlot );
				mv.visitLdcInsn( cases.get( ci ) );
				mv.visitMethodInsn( INVOKEVIRTUAL, "java/lang/String", "equals",
						"(Ljava/lang/Object;)Z", false );

				if ( last ) {
					Label afterSet = new Label();
					mv.visitJumpInsn( IFEQ, afterSet );
					pushIntConst( mv, ci );
					mv.visitVarInsn( ISTORE, tempSlot );
					mv.visitLabel( afterSet );
					mv.visitFrame( F_SAME, 0, null, 0, null );
				}
				else {
					Label nextCheck = new Label();
					mv.visitJumpInsn( IFEQ, nextCheck );
					pushIntConst( mv, ci );
					mv.visitVarInsn( ISTORE, tempSlot );
					mv.visitJumpInsn( GOTO, secondSwitch );
					mv.visitLabel( nextCheck );
					mv.visitFrame( F_SAME, 0, null, 0, null );
				}
			}
			mv.visitJumpInsn( GOTO, secondSwitch );
		}

		mv.visitLabel( secondSwitch );
		mv.visitFrame( F_SAME, 0, null, 0, null );

		Label[] bodyLabels = new Label[cases.size()];
		for ( int i = 0; i < cases.size(); i++ ) {
			bodyLabels[i] = new Label();
		}

		mv.visitVarInsn( ILOAD, tempSlot );
		mv.visitTableSwitchInsn( 0, cases.size() - 1, defaultLabel, bodyLabels );

		for ( int i = 0; i < cases.size(); i++ ) {
			mv.visitLabel( bodyLabels[i] );
			mv.visitFrame( F_SAME, 0, null, 0, null );
			bodyEmitter.emit( mv, i );
		}
	}
}
