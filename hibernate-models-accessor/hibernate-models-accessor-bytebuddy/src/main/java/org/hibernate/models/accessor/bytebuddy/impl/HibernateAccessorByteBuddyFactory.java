/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.bytebuddy.impl;

import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.MultiValueAccessorGenerationException;
import org.hibernate.models.accessor.bytebuddy.spi.HibernateAccessorByteBuddyBulkAccessor;
import org.hibernate.models.accessor.spi.HibernateAccessorBytecodeDumper;
import org.hibernate.models.accessor.spi.HibernateAccessorConfiguration;
import org.hibernate.models.accessor.spi.CrossClassLoaderLookupBridge;
import org.hibernate.models.accessor.spi.MemberValidation;

import org.jboss.logging.Logger;

import net.bytebuddy.jar.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class HibernateAccessorByteBuddyFactory implements org.hibernate.models.accessor.bytebuddy.HibernateAccessorByteBuddyFactory {

	// we only need it to create hidden classes for generated multi readers/writers
	private static final MethodHandles.Lookup ACCESSOR_MODULE_LOOKUP = MethodHandles.lookup();
	private static final Logger LOG = Logger.getLogger( HibernateAccessorByteBuddyFactory.class );
	private final ClassValue<HibernateAccessorByteBuddyClassAccessorInfo> cache;
	private final CrossClassLoaderLookupBridge lookupBridge;
	private final HibernateAccessorBytecodeDumper bytecodeDumper;
	private final org.hibernate.models.accessor.HibernateAccessorFactory reflectionFallback = org.hibernate.models.accessor.HibernateAccessorFactory.reflection();

	public HibernateAccessorByteBuddyFactory(MethodHandles.Lookup lookup) {
		this( new HibernateAccessorConfiguration( lookup ) );
	}

	public HibernateAccessorByteBuddyFactory(HibernateAccessorConfiguration configuration) {
		this.lookupBridge = new CrossClassLoaderLookupBridge( configuration.lookup(), BridgeClassGenerator::generate );
		this.bytecodeDumper = new HibernateAccessorBytecodeDumper( configuration );
		this.cache = new ClassValue<>() {
			@Override
			protected HibernateAccessorByteBuddyClassAccessorInfo computeValue(Class<?> type) {
				return HibernateAccessorByteBuddyClassAccessorInfo.create( type, lookupBridge, bytecodeDumper );
			}
		};
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		try {
			HibernateAccessorByteBuddyClassAccessorInfo info = getOrCreate(constructor.getDeclaringClass());
			return new HibernateAccessorByteBuddyInstantiator<>(info.bulkAccessor(), info.constructorIndex(constructor));
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy instantiator for %s, falling back to reflection", constructor.getDeclaringClass() );
			return reflectionFallback.instantiator( constructor );
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		MemberValidation.validateInstanceMember( field );
		try {
			HibernateAccessorByteBuddyClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
			return new HibernateAccessorByteBuddyFieldValueReader<>(info.bulkAccessor(), info.fieldIndex(field));
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy value reader for %s, falling back to reflection", field );
			return reflectionFallback.valueReader( field );
		}
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		MemberValidation.validateReaderMethod(method);
		try {
			HibernateAccessorByteBuddyClassAccessorInfo info = getOrCreate(method.getDeclaringClass());
			return new HibernateAccessorByteBuddyMethodValueReader<>(info.bulkAccessor(), info.methodIndex(method));
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy value reader for %s, falling back to reflection", method );
			return reflectionFallback.valueReader( method );
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		MemberValidation.validateInstanceMember( field );
		if ( Modifier.isFinal( field.getModifiers() ) ) {
			return reflectionFallback.valueWriter( field );
		}
		try {
			HibernateAccessorByteBuddyClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
			return new HibernateAccessorByteBuddyFieldValueWriter(info.bulkAccessor(), info.fieldIndex(field));
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy value writer for %s, falling back to reflection", field );
			return reflectionFallback.valueWriter( field );
		}
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		MemberValidation.validateWriterMethod(setter);
		try {
			HibernateAccessorByteBuddyClassAccessorInfo info = getOrCreate(setter.getDeclaringClass());
			return new HibernateAccessorByteBuddyMethodValueWriter(info.bulkAccessor(), info.methodIndex(setter));
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy value writer for %s, falling back to reflection", setter );
			return reflectionFallback.valueWriter( setter );
		}
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
		try {
			if (allSameDeclaringClass(declaringClass, members)) {
				return generateDirectReader(members);
			}
			return generateBulkBasedReader(members);
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy multi-value reader for %s, falling back to reflection", declaringClass );
			return reflectionFallback.multiValueReader( declaringClass, members );
		}
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
		try {
			if (allSameDeclaringClass(declaringClass, members)) {
				return generateDirectWriter(members);
			}
			return generateBulkBasedWriter(members);
		}
		catch (RuntimeException e) {
			LOG.debugf( e, "Failed to create ByteBuddy multi-value writer for %s, falling back to reflection", declaringClass );
			return reflectionFallback.multiValueWriter( declaringClass, members );
		}
	}

	private HibernateAccessorMultiValueReader generateDirectReader(Member[] members) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorByteBuddyMultiValueClassGenerator.generateReader( targetClass, members );
		bytecodeDumper.dump( Type.getInternalName( targetClass ) + "$$HibernateAccessorMultiReader_" + java.util.UUID.randomUUID(), bytecode );
		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( targetClass );
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass( bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass().getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException( "Failed to create direct multi-value reader for " + targetClass.getName(), e );
		}
	}

	private HibernateAccessorMultiValueWriter generateDirectWriter(Member[] members) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorByteBuddyMultiValueClassGenerator.generateWriter( targetClass, members );
		bytecodeDumper.dump( Type.getInternalName( targetClass ) + "$$HibernateAccessorMultiWriter_" + java.util.UUID.randomUUID(), bytecode );
		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( targetClass );
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass( bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass().getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException( "Failed to create direct multi-value writer for " + targetClass.getName(), e );
		}
	}

	private HibernateAccessorMultiValueReader generateBulkBasedReader(Member[] members) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout(members);
		final byte[] bytecode = HibernateAccessorByteBuddyMultiValueClassGenerator.generateBulkReader(layout.accesses, layout.accessors.length);
		bytecodeDumper.dump( Type.getInternalName( members[0].getDeclaringClass() ) + "$$HibernateAccessorMultiBulkReader_" + java.util.UUID.randomUUID(), bytecode );
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass(bytecode, true);
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill(paramTypes, HibernateAccessorByteBuddyBulkAccessor.class);
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass().getDeclaredConstructor(paramTypes).newInstance((Object[]) layout.accessors);
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException("Failed to create bulk-based multi-value reader", e);
		}
	}

	private HibernateAccessorMultiValueWriter generateBulkBasedWriter(Member[] members) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout(members);
		final byte[] bytecode = HibernateAccessorByteBuddyMultiValueClassGenerator.generateBulkWriter(layout.accesses, layout.accessors.length);
		bytecodeDumper.dump( Type.getInternalName( members[0].getDeclaringClass() ) + "$$HibernateAccessorMultiBulkWriter_" + java.util.UUID.randomUUID(), bytecode );
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass(bytecode, true);
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill(paramTypes, HibernateAccessorByteBuddyBulkAccessor.class);
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass().getDeclaredConstructor(paramTypes).newInstance((Object[]) layout.accessors);
		}
		catch (Exception e) {
			throw new MultiValueAccessorGenerationException("Failed to create bulk-based multi-value writer", e);
		}
	}

	private BulkAccessorLayout buildBulkAccessorLayout(Member[] members) {
		final Map<Class<?>, Integer> classToFieldIndex = new LinkedHashMap<>();
		for (Member member : members) {
			classToFieldIndex.computeIfAbsent(member.getDeclaringClass(), cls -> classToFieldIndex.size());
		}

		final HibernateAccessorByteBuddyBulkAccessor[] accessors = new HibernateAccessorByteBuddyBulkAccessor[classToFieldIndex.size()];
		final HibernateAccessorByteBuddyClassAccessorInfo[] infos = new HibernateAccessorByteBuddyClassAccessorInfo[classToFieldIndex.size()];
		for (var entry : classToFieldIndex.entrySet()) {
			final HibernateAccessorByteBuddyClassAccessorInfo info = getOrCreate(entry.getKey());
			accessors[entry.getValue()] = info.bulkAccessor();
			infos[entry.getValue()] = info;
		}

		final HibernateAccessorBulkMemberAccess[] accesses = new HibernateAccessorBulkMemberAccess[members.length];
		for (int i = 0; i < members.length; i++) {
			final int fieldIdx = classToFieldIndex.get(members[i].getDeclaringClass());
			final HibernateAccessorByteBuddyClassAccessorInfo info = infos[fieldIdx];
			final boolean isField = members[i] instanceof Field;
			final int memberIdx = isField ? info.fieldIndex((Field) members[i]) : info.methodIndex((Method) members[i]);
			accesses[i] = new HibernateAccessorBulkMemberAccess(fieldIdx, memberIdx, isField);
		}

		return new BulkAccessorLayout(accesses, accessors);
	}

	private record BulkAccessorLayout(HibernateAccessorBulkMemberAccess[] accesses, HibernateAccessorByteBuddyBulkAccessor[] accessors) {
	}

	private static boolean allSameDeclaringClass(Class<?> declaringClass, Member[] members) {
		if (members.length == 0) {
			return true;
		}
		for (int i = 0; i < members.length; i++) {
			if (members[i].getDeclaringClass() != declaringClass) {
				return false;
			}
		}
		return true;
	}

	private HibernateAccessorByteBuddyClassAccessorInfo getOrCreate(Class<?> declaringClass) {
		return cache.get( declaringClass );
	}

}
