/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm.impl;

import org.hibernate.models.accessor.HibernateAccessorException;
import org.hibernate.models.accessor.asm.spi.HibernateAccessorAsmBulkAccessor;
import org.hibernate.models.accessor.spi.HibernateAccessorBytecodeDumper;
import org.hibernate.models.accessor.spi.CrossClassLoaderLookupBridge;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class HibernateAccessorAsmClassAccessorInfo {

	private final HibernateAccessorAsmBulkAccessor bulkAccessor;
	private final Map<String, Integer> fieldIndices;
	private final Map<String, Integer> getterMethodIndices;
	private final Map<String, Integer> setterMethodIndices;
	private final Map<String, Integer> constructorIndices;

	private HibernateAccessorAsmClassAccessorInfo(HibernateAccessorAsmBulkAccessor bulkAccessor,
												Map<String, Integer> fieldIndices,
												Map<String, Integer> getterMethodIndices,
												Map<String, Integer> setterMethodIndices,
												Map<String, Integer> constructorIndices) {
		this.bulkAccessor = bulkAccessor;
		this.fieldIndices = fieldIndices;
		this.getterMethodIndices = getterMethodIndices;
		this.setterMethodIndices = setterMethodIndices;
		this.constructorIndices = constructorIndices;
	}

	static HibernateAccessorAsmClassAccessorInfo create(Class<?> declaringClass, CrossClassLoaderLookupBridge lookupBridge, HibernateAccessorBytecodeDumper bytecodeDumper) {
		Field[] fields = Arrays.stream( declaringClass.getDeclaredFields() )
				.filter( f -> !Modifier.isStatic( f.getModifiers() ) )
				.toArray( Field[]::new );
		Method[] getterMethods = Arrays.stream( declaringClass.getDeclaredMethods() )
				.filter( m -> !Modifier.isStatic( m.getModifiers() ) )
				.filter( m -> m.getParameterCount() == 0 && m.getReturnType() != void.class )
				.toArray( Method[]::new );
		Method[] setterMethods = Arrays.stream( declaringClass.getDeclaredMethods() )
				.filter( m -> !Modifier.isStatic( m.getModifiers() ) )
				.filter( m -> m.getParameterCount() == 1 )
				.toArray( Method[]::new );
		Constructor<?>[] constructors = declaringClass.getDeclaredConstructors();

		Map<String, Integer> fieldIndices = new HashMap<>();
		for (int i = 0; i < fields.length; i++) {
			fieldIndices.put(fields[i].getName(), i);
		}

		Map<String, Integer> getterMethodIndices = new HashMap<>();
		for (int i = 0; i < getterMethods.length; i++) {
			getterMethodIndices.put(methodKey(getterMethods[i]), i);
		}

		Map<String, Integer> setterMethodIndices = new HashMap<>();
		for (int i = 0; i < setterMethods.length; i++) {
			setterMethodIndices.put(methodKey(setterMethods[i]), i);
		}

		Map<String, Integer> constructorIndices = new HashMap<>();
		for (int i = 0; i < constructors.length; i++) {
			constructorIndices.put(Type.getConstructorDescriptor(constructors[i]), i);
		}

		byte[] bytecode = HibernateAccessorAsmBulkAccessorClassGenerator.generate(declaringClass, fields, getterMethods, setterMethods, constructors);
		bytecodeDumper.dump( Type.getInternalName( declaringClass ) + "$$HibernateAccessor", bytecode );

		try {
			MethodHandles.Lookup targetLookup = lookupBridge.resolve( declaringClass );
			MethodHandles.Lookup hiddenClassLookup = targetLookup.defineHiddenClass(
					bytecode, true, MethodHandles.Lookup.ClassOption.NESTMATE );
			HibernateAccessorAsmBulkAccessor instance = (HibernateAccessorAsmBulkAccessor) hiddenClassLookup.lookupClass()
					.getDeclaredConstructor().newInstance();
			return new HibernateAccessorAsmClassAccessorInfo( instance, fieldIndices, getterMethodIndices, setterMethodIndices, constructorIndices );
		}
		catch (Exception e) {
			throw new HibernateAccessorException( "Failed to create bulk accessor for " + declaringClass.getName(), e );
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
		String key = methodKey(method);
		if (method.getParameterCount() == 0 && method.getReturnType() != void.class) {
			Integer index = getterMethodIndices.get(key);
			if (index != null) {
				return index;
			}
		}
		else if (method.getParameterCount() == 1) {
			Integer index = setterMethodIndices.get(key);
			if (index != null) {
				return index;
			}
		}
		throw new HibernateAccessorException("Unknown method: " + method);
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
