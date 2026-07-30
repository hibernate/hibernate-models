/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.MultiValueAccessorGenerationException;
import org.hibernate.models.accessor.asm.spi.HibernateAccessorAsmBulkAccessor;
import org.hibernate.models.accessor.asm.spi.MultiValueAccessorPointcuts;
import org.hibernate.models.accessor.spi.HibernateAccessorBytecodeDumper;
import org.hibernate.models.accessor.spi.HibernateAccessorConfiguration;
import org.hibernate.models.accessor.spi.CrossClassLoaderLookupBridge;
import org.hibernate.models.accessor.spi.MemberValidation;

import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HibernateAccessorAsmFactory implements org.hibernate.models.accessor.asm.HibernateAccessorAsmFactory {

	// we only need it to create hidden classes for generated multi readers/writers
	private static final MethodHandles.Lookup ACCESSOR_MODULE_LOOKUP = MethodHandles.lookup();
	private final ClassValue<HibernateAccessorAsmClassAccessorInfo> cache;
	private final CrossClassLoaderLookupBridge lookupBridge;
	private final HibernateAccessorBytecodeDumper bytecodeDumper;

	public HibernateAccessorAsmFactory(MethodHandles.Lookup lookup) {
		this( new HibernateAccessorConfiguration( lookup ) );
	}

	public HibernateAccessorAsmFactory(HibernateAccessorConfiguration configuration) {
		this.lookupBridge = new CrossClassLoaderLookupBridge( configuration.lookup(), BridgeClassGenerator::generate );
		this.bytecodeDumper = new HibernateAccessorBytecodeDumper( configuration );
		this.cache = new ClassValue<>() {
			@Override
			protected HibernateAccessorAsmClassAccessorInfo computeValue(Class<?> type) {
				return HibernateAccessorAsmClassAccessorInfo.create( type, lookupBridge, bytecodeDumper );
			}
		};
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate( constructor.getDeclaringClass() );
		return new HibernateAccessorAsmInstantiator<>( info.bulkAccessor(), info.constructorIndex( constructor ) );
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		MemberValidation.validateInstanceMember( field );
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
		return new HibernateAccessorAsmFieldValueReader<>(info.bulkAccessor(), info.fieldIndex(field));
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		MemberValidation.validateReaderMethod( method );
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate( method.getDeclaringClass() );
		return new HibernateAccessorAsmMethodValueReader<>( info.bulkAccessor(), info.methodIndex( method ) );
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		MemberValidation.validateInstanceMember( field );
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
		return new HibernateAccessorAsmFieldValueWriter(info.bulkAccessor(), info.fieldIndex(field));
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		MemberValidation.validateWriterMethod( setter );
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate( setter.getDeclaringClass() );
		return new HibernateAccessorAsmMethodValueWriter( info.bulkAccessor(), info.methodIndex( setter ) );
	}

	@Override
	public HibernateAccessorMultiValueReader multiValueReader(Class<?> declaringClass, Member... members) {
		if ( members.length == 0 ) {
			throw new IllegalArgumentException( "At least one member is required" );
		}
		for ( Member member : members ) {
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateReaderMember( member );
		}
		if ( allSameDeclaringClass( declaringClass, members ) ) {
			return generateDirectReader( members );
		}
		return generateBulkBasedReader( members );
	}

	@Override
	public HibernateAccessorMultiValueWriter multiValueWriter(Class<?> declaringClass, Member... members) {
		if ( members.length == 0 ) {
			throw new IllegalArgumentException( "At least one member is required" );
		}
		for ( Member member : members ) {
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateWriterMember( member );
		}
		if ( allSameDeclaringClass( declaringClass, members ) ) {
			return generateDirectWriter( members );
		}
		return generateBulkBasedWriter( members );
	}

	public HibernateAccessorMultiValueReader multiValueReader(
			Class<?> declaringClass, Member[] members, MultiValueAccessorPointcuts pointcuts) {
		if ( members.length == 0 ) {
			throw new IllegalArgumentException( "At least one member is required" );
		}
		for ( Member member : members ) {
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateReaderMember( member );
		}
		if ( allSameDeclaringClass( declaringClass, members ) ) {
			return generateDirectReader( members, pointcuts );
		}
		return generateBulkBasedReader( members, pointcuts );
	}

	public HibernateAccessorMultiValueWriter multiValueWriter(
			Class<?> declaringClass, Member[] members, MultiValueAccessorPointcuts pointcuts) {
		if ( members.length == 0 ) {
			throw new IllegalArgumentException( "At least one member is required" );
		}
		for ( Member member : members ) {
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateWriterMember( member );
		}
		if ( allSameDeclaringClass( declaringClass, members ) ) {
			return generateDirectWriter( members, pointcuts );
		}
		return generateBulkBasedWriter( members, pointcuts );
	}

	private HibernateAccessorMultiValueReader generateDirectReader(Member[] members) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateReader( targetClass, members );
		bytecodeDumper.dump( Type.getInternalName( targetClass ) + "$$HibernateAccessorMultiReader_" + java.util.UUID.randomUUID(), bytecode );
		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( targetClass );
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass(
					bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass()
					.getDeclaredConstructor()
					.newInstance();
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException(
					"Failed to create direct multi-value reader for " + targetClass.getName(), e );
		}
	}

	private HibernateAccessorMultiValueWriter generateDirectWriter(Member[] members) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateWriter( targetClass, members );
		bytecodeDumper.dump( Type.getInternalName( targetClass ) + "$$HibernateAccessorMultiWriter_" + java.util.UUID.randomUUID(), bytecode );
		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( targetClass );
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass(
					bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass()
					.getDeclaredConstructor()
					.newInstance();
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException(
					"Failed to create direct multi-value writer for " + targetClass.getName(), e );
		}
	}

	private HibernateAccessorMultiValueReader generateBulkBasedReader(Member[] members) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout( members );
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateBulkReader(
				layout.accesses,
				layout.accessors.length
		);
		bytecodeDumper.dump( Type.getInternalName( members[0].getDeclaringClass() ) + "$$HibernateAccessorMultiBulkReader_" + java.util.UUID.randomUUID(), bytecode );
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass( bytecode, true );
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill( paramTypes, HibernateAccessorAsmBulkAccessor.class );
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass()
					.getDeclaredConstructor( paramTypes )
					.newInstance( (Object[]) layout.accessors );
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException( "Failed to create bulk-based multi-value reader", e );
		}
	}

	private HibernateAccessorMultiValueWriter generateBulkBasedWriter(Member[] members) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout( members );
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateBulkWriter(
				layout.accesses,
				layout.accessors.length
		);
		bytecodeDumper.dump( "org/hibernate/models/accessor/asm/impl/HibernateAccessorMultiBulkWriter", bytecode );
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass( bytecode, true );
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill( paramTypes, HibernateAccessorAsmBulkAccessor.class );
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass()
					.getDeclaredConstructor( paramTypes )
					.newInstance( (Object[]) layout.accessors );
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException( "Failed to create bulk-based multi-value writer", e );
		}
	}

	private HibernateAccessorMultiValueReader generateDirectReader(
			Member[] members,
			MultiValueAccessorPointcuts pointcuts) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateReader(
				targetClass, members, pointcuts );
		bytecodeDumper.dump( Type.getInternalName( targetClass ) + "$$HibernateAccessorMultiReader", bytecode );
		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( targetClass );
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass(
					bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass()
					.getDeclaredConstructor()
					.newInstance();
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException(
					"Failed to create direct multi-value reader for " + targetClass.getName(), e );
		}
	}

	private HibernateAccessorMultiValueWriter generateDirectWriter(
			Member[] members,
			MultiValueAccessorPointcuts pointcuts) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateWriter(
				targetClass, members, pointcuts );
		bytecodeDumper.dump( Type.getInternalName( targetClass ) + "$$HibernateAccessorMultiWriter", bytecode );
		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( targetClass );
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass(
					bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass()
					.getDeclaredConstructor()
					.newInstance();
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException(
					"Failed to create direct multi-value writer for " + targetClass.getName(), e );
		}
	}

	private HibernateAccessorMultiValueReader generateBulkBasedReader(
			Member[] members,
			MultiValueAccessorPointcuts pointcuts) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout( members );
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateBulkReader(
				layout.accesses,
				layout.accessors.length,
				pointcuts
		);
		bytecodeDumper.dump( "org/hibernate/models/accessor/asm/impl/HibernateAccessorMultiBulkReader", bytecode );
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass( bytecode, true );
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill( paramTypes, HibernateAccessorAsmBulkAccessor.class );
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass()
					.getDeclaredConstructor( paramTypes )
					.newInstance( (Object[]) layout.accessors );
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException( "Failed to create bulk-based multi-value reader", e );
		}
	}

	private HibernateAccessorMultiValueWriter generateBulkBasedWriter(
			Member[] members,
			MultiValueAccessorPointcuts pointcuts) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout( members );
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateBulkWriter(
				layout.accesses,
				layout.accessors.length,
				pointcuts
		);
		bytecodeDumper.dump( Type.getInternalName( members[0].getDeclaringClass() ) + "$$HibernateAccessorMultiBulkWriter_" + java.util.UUID.randomUUID(), bytecode );
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass( bytecode, true );
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill( paramTypes, HibernateAccessorAsmBulkAccessor.class );
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass()
					.getDeclaredConstructor( paramTypes )
					.newInstance( (Object[]) layout.accessors );
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException( "Failed to create bulk-based multi-value writer", e );
		}
	}

	private BulkAccessorLayout buildBulkAccessorLayout(Member[] members) {
		final Map<Class<?>, Integer> classToFieldIndex = new LinkedHashMap<>();
		for ( Member member : members ) {
			classToFieldIndex.computeIfAbsent( member.getDeclaringClass(), cls -> classToFieldIndex.size() );
		}

		final HibernateAccessorAsmBulkAccessor[] accessors = new HibernateAccessorAsmBulkAccessor[classToFieldIndex.size()];
		final HibernateAccessorAsmClassAccessorInfo[] infos = new HibernateAccessorAsmClassAccessorInfo[classToFieldIndex.size()];
		for ( var entry : classToFieldIndex.entrySet() ) {
			final HibernateAccessorAsmClassAccessorInfo info = getOrCreate( entry.getKey() );
			accessors[entry.getValue()] = info.bulkAccessor();
			infos[entry.getValue()] = info;
		}

		final HibernateAccessorBulkMemberAccess[] accesses = new HibernateAccessorBulkMemberAccess[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final int fieldIdx = classToFieldIndex.get( members[i].getDeclaringClass() );
			final HibernateAccessorAsmClassAccessorInfo info = infos[fieldIdx];
			final boolean isField = members[i] instanceof Field;
			final int memberIdx = isField ?
					info.fieldIndex( (Field) members[i] ) :
					info.methodIndex( (Method) members[i] );
			accesses[i] = new HibernateAccessorBulkMemberAccess( fieldIdx, memberIdx, isField );
		}

		return new BulkAccessorLayout( accesses, accessors );
	}

	private record BulkAccessorLayout(HibernateAccessorBulkMemberAccess[] accesses,
									HibernateAccessorAsmBulkAccessor[] accessors) {
	}

	private static boolean allSameDeclaringClass(Class<?> declaringClass, Member[] members) {
		if ( members.length == 0 ) {
			return true;
		}
		for ( int i = 0; i < members.length; i++ ) {
			if ( members[i].getDeclaringClass() != declaringClass ) {
				return false;
			}
		}
		return true;
	}

	private HibernateAccessorAsmClassAccessorInfo getOrCreate(Class<?> declaringClass) {
		return cache.get( declaringClass );
	}

}
