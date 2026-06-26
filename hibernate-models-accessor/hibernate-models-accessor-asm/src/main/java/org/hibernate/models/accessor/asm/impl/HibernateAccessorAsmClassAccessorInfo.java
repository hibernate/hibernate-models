/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorException;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final class HibernateAccessorAsmClassAccessorInfo {

	private final HibernateAccessorAsmBulkAccessor bulkAccessor;
	private final Map<String, Integer> fieldIndices;
	private final Map<String, Integer> methodIndices;
	private final Map<String, Integer> constructorIndices;

	private HibernateAccessorAsmClassAccessorInfo(HibernateAccessorAsmBulkAccessor bulkAccessor,
												Map<String, Integer> fieldIndices,
												Map<String, Integer> methodIndices,
												Map<String, Integer> constructorIndices) {
		this.bulkAccessor = bulkAccessor;
		this.fieldIndices = fieldIndices;
		this.methodIndices = methodIndices;
		this.constructorIndices = constructorIndices;
	}

	static HibernateAccessorAsmClassAccessorInfo create(Class<?> declaringClass, MethodHandles.Lookup lookup) {
		Field[] fields = declaringClass.getDeclaredFields();
		Method[] methods = declaringClass.getDeclaredMethods();
		Constructor<?>[] constructors = declaringClass.getDeclaredConstructors();

		Map<String, Integer> fieldIndices = new HashMap<>();
		for (int i = 0; i < fields.length; i++) {
			fieldIndices.put(fields[i].getName(), i);
		}

		Map<String, Integer> methodIndices = new HashMap<>();
		for (int i = 0; i < methods.length; i++) {
			methodIndices.put(methodKey(methods[i]), i);
		}

		Map<String, Integer> constructorIndices = new HashMap<>();
		for (int i = 0; i < constructors.length; i++) {
			constructorIndices.put(Type.getConstructorDescriptor(constructors[i]), i);
		}

		byte[] bytecode = HibernateAccessorAsmBulkAccessorClassGenerator.generate(declaringClass, fields, methods, constructors);

		try {
			MethodHandles.Lookup targetLookup = MethodHandles.privateLookupIn(declaringClass, lookup);
			MethodHandles.Lookup hiddenClassLookup = targetLookup.defineHiddenClass(
					bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE);
			HibernateAccessorAsmBulkAccessor instance = (HibernateAccessorAsmBulkAccessor) hiddenClassLookup.lookupClass()
					.getDeclaredConstructor().newInstance();
			return new HibernateAccessorAsmClassAccessorInfo(instance, fieldIndices, methodIndices, constructorIndices);
		}
		catch (Exception e) {
			throw new HibernateAccessorException("Failed to create bulk accessor for " + declaringClass.getName(), e);
		}
	}

	HibernateAccessorAsmBulkAccessor bulkAccessor() {
		return bulkAccessor;
	}

	int fieldIndex(Field field) {
		Integer index = fieldIndices.get(field.getName());
		if (index == null) {
			throw new HibernateAccessorException("Unknown field: " + field);
		}
		return index;
	}

	int methodIndex(Method method) {
		Integer index = methodIndices.get(methodKey(method));
		if (index == null) {
			throw new HibernateAccessorException("Unknown method: " + method);
		}
		return index;
	}

	int constructorIndex(Constructor<?> constructor) {
		Integer index = constructorIndices.get(Type.getConstructorDescriptor(constructor));
		if (index == null) {
			throw new HibernateAccessorException("Unknown constructor: " + constructor);
		}
		return index;
	}

	private static String methodKey(Method method) {
		return method.getName() + Type.getMethodDescriptor(method);
	}
}
