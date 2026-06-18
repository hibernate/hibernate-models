/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.*;
import org.hibernate.models.accessor.spi.MemberValidation;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HibernateAccessorAsmFactory implements HibernateAccessorFactory {

	// we only need it to create hidden classes for generated multi readers/writers
	private static final MethodHandles.Lookup ACCESSOR_MODULE_LOOKUP = MethodHandles.lookup();
	private final ConcurrentHashMap<Class<?>, HibernateAccessorAsmClassAccessorInfo> cache = new ConcurrentHashMap<>();
	private final MethodHandles.Lookup lookup;

	public HibernateAccessorAsmFactory(MethodHandles.Lookup lookup) {
		this.lookup = lookup;
	}

	@Override
	public <T> HibernateAccessorInstantiator<T> instantiator(Constructor<T> constructor) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(constructor.getDeclaringClass());
		return new HibernateAccessorAsmInstantiator<>(info.bulkAccessor(), info.constructorIndex(constructor));
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Field field) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
		return new HibernateAccessorAsmFieldValueReader<>(info.bulkAccessor(), info.fieldIndex(field));
	}

	@Override
	public HibernateAccessorValueReader<?> valueReader(Method method) {
		MemberValidation.validateReaderMethod(method);
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(method.getDeclaringClass());
		return new HibernateAccessorAsmMethodValueReader<>(info.bulkAccessor(), info.methodIndex(method));
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Field field) {
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(field.getDeclaringClass());
		return new HibernateAccessorAsmFieldValueWriter(info.bulkAccessor(), info.fieldIndex(field));
	}

	@Override
	public HibernateAccessorValueWriter valueWriter(Method setter) {
		MemberValidation.validateWriterMethod(setter);
		HibernateAccessorAsmClassAccessorInfo info = getOrCreate(setter.getDeclaringClass());
		return new HibernateAccessorAsmMethodValueWriter(info.bulkAccessor(), info.methodIndex(setter));
	}

	@Override
	public HibernateAccessorMultiValueReader multiValueReader(Class<?> declaringClass, Member... members) {
		for ( Member member : members ) {
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateReaderMember( member );
		}
		if (allSameDeclaringClass(declaringClass, members)) {
			return generateDirectReader(members);
		}
		return generateBulkBasedReader(members);
	}

	@Override
	public HibernateAccessorMultiValueWriter multiValueWriter(Class<?> declaringClass, Member... members) {
		for ( Member member : members ) {
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateWriterMember( member );
		}
		if (allSameDeclaringClass(declaringClass, members)) {
			return generateDirectWriter(members);
		}
		return generateBulkBasedWriter(members);
	}

	private HibernateAccessorMultiValueReader generateDirectReader(Member[] members) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateReader(targetClass, members);
		try {
			MethodHandles.Lookup targetLookup = MethodHandles.privateLookupIn(targetClass, lookup);
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass(bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE);
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass().getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {
			throw new HibernateAccessorException("Failed to create direct multi-value reader for " + targetClass.getName(), e);
		}
	}

	private HibernateAccessorMultiValueWriter generateDirectWriter(Member[] members) {
		final Class<?> targetClass = members[0].getDeclaringClass();
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateWriter(targetClass, members);
		try {
			MethodHandles.Lookup targetLookup = MethodHandles.privateLookupIn(targetClass, lookup);
			MethodHandles.Lookup hiddenLookup = targetLookup.defineHiddenClass(bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE);
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass().getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {
			throw new HibernateAccessorException("Failed to create direct multi-value writer for " + targetClass.getName(), e);
		}
	}

	private HibernateAccessorMultiValueReader generateBulkBasedReader(Member[] members) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout(members);
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateBulkReader(layout.accesses, layout.accessors.length);
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass(bytecode, true);
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill(paramTypes, HibernateAccessorAsmBulkAccessor.class);
			return (HibernateAccessorMultiValueReader) hiddenLookup.lookupClass().getDeclaredConstructor(paramTypes).newInstance((Object[]) layout.accessors);
		}
		catch (Exception e) {
			throw new HibernateAccessorException("Failed to create bulk-based multi-value reader", e);
		}
	}

	private HibernateAccessorMultiValueWriter generateBulkBasedWriter(Member[] members) {
		final BulkAccessorLayout layout = buildBulkAccessorLayout(members);
		final byte[] bytecode = HibernateAccessorAsmMultiValueClassGenerator.generateBulkWriter(layout.accesses, layout.accessors.length);
		try {
			final MethodHandles.Lookup hiddenLookup = ACCESSOR_MODULE_LOOKUP.defineHiddenClass(bytecode, true);
			final Class<?>[] paramTypes = new Class<?>[layout.accessors.length];
			Arrays.fill(paramTypes, HibernateAccessorAsmBulkAccessor.class);
			return (HibernateAccessorMultiValueWriter) hiddenLookup.lookupClass().getDeclaredConstructor(paramTypes).newInstance((Object[]) layout.accessors);
		}
		catch (Exception e) {
			throw new HibernateAccessorException("Failed to create bulk-based multi-value writer", e);
		}
	}

	private BulkAccessorLayout buildBulkAccessorLayout(Member[] members) {
		final Map<Class<?>, Integer> classToFieldIndex = new LinkedHashMap<>();
		for (Member member : members) {
			classToFieldIndex.computeIfAbsent(member.getDeclaringClass(), cls -> classToFieldIndex.size());
		}

		final HibernateAccessorAsmBulkAccessor[] accessors = new HibernateAccessorAsmBulkAccessor[classToFieldIndex.size()];
		final HibernateAccessorAsmClassAccessorInfo[] infos = new HibernateAccessorAsmClassAccessorInfo[classToFieldIndex.size()];
		for (var entry : classToFieldIndex.entrySet()) {
			final HibernateAccessorAsmClassAccessorInfo info = getOrCreate(entry.getKey());
			accessors[entry.getValue()] = info.bulkAccessor();
			infos[entry.getValue()] = info;
		}

		final HibernateAccessorBulkMemberAccess[] accesses = new HibernateAccessorBulkMemberAccess[members.length];
		for (int i = 0; i < members.length; i++) {
			final int fieldIdx = classToFieldIndex.get(members[i].getDeclaringClass());
			final HibernateAccessorAsmClassAccessorInfo info = infos[fieldIdx];
			final boolean isField = members[i] instanceof Field;
			final int memberIdx = isField ? info.fieldIndex((Field) members[i]) : info.methodIndex((Method) members[i]);
			accesses[i] = new HibernateAccessorBulkMemberAccess(fieldIdx, memberIdx, isField);
		}

		return new BulkAccessorLayout(accesses, accessors);
	}

	private record BulkAccessorLayout(HibernateAccessorBulkMemberAccess[] accesses, HibernateAccessorAsmBulkAccessor[] accessors) {
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

	private HibernateAccessorAsmClassAccessorInfo getOrCreate(Class<?> declaringClass) {
		return cache.computeIfAbsent(declaringClass, cls -> HibernateAccessorAsmClassAccessorInfo.create(cls, lookup));
	}
}
