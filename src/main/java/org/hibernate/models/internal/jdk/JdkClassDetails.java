/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.internal.util.ArrayHelper;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import static org.hibernate.models.internal.jdk.JdkBuilders.buildMethodDetails;

/**
 * ClassDetails implementation based on a {@link Class} reference
 *
 * @author Steve Ebersole
 */
public class JdkClassDetails extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final String name;
	private final Class<?> managedClass;

	private final ClassDetails superType;
	private List<ClassDetails> interfaces;

	private List<FieldDetails> fields;
	private List<MethodDetails> methods;
	private List<RecordComponentDetails> recordComponents;

	public JdkClassDetails(
			Class<?> managedClass,
			SourceModelBuildingContext buildingContext) {
		this( managedClass.getName(), managedClass, buildingContext );
	}

	public JdkClassDetails(
			String name,
			Class<?> managedClass,
			SourceModelBuildingContext buildingContext) {
		super( managedClass::getAnnotations, buildingContext );
		this.name = name;
		this.managedClass = managedClass;

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final Class<?> superclass = managedClass.getSuperclass();
		if ( superclass == null ) {
			superType = null;
		}
		else {
			superType = classDetailsRegistry.resolveClassDetails(
					superclass.getName(),
					(n) -> JdkBuilders.buildClassDetailsStatic( superclass, buildingContext )
			);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassName() {
		return managedClass.getName();
	}

	@Override
	public <X> Class<X> toJavaClass() {
		//noinspection unchecked
		return (Class<X>) managedClass;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract( managedClass.getModifiers() );
	}

	@Override
	public boolean isRecord() {
		return managedClass.isRecord();
	}

	@Override
	public ClassDetails getSuperType() {
		return superType;
	}

	@Override
	public List<ClassDetails> getImplementedInterfaceTypes() {
		if ( interfaces == null ) {
			interfaces = collectInterfaces();
		}
		return interfaces;
	}

	private List<ClassDetails> collectInterfaces() {
		final Class<?>[] interfaceClasses = managedClass.getInterfaces();
		if ( ArrayHelper.isEmpty( interfaceClasses ) ) {
			return Collections.emptyList();
		}

		final ArrayList<ClassDetails> result = CollectionHelper.arrayList( interfaceClasses.length );
		for ( int i = 0; i < interfaceClasses.length; i++ ) {
			final Class<?> interfaceClass = interfaceClasses[ i ];
			final ClassDetails interfaceDetails = getBuildingContext().getClassDetailsRegistry().resolveClassDetails(
					interfaceClass.getName(),
					(n) -> JdkBuilders.buildClassDetailsStatic( interfaceClass, getBuildingContext() )
			);
			result.add( interfaceDetails );
		}
		return result;
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return checkType.isAssignableFrom( managedClass );
	}

	@Override
	public List<FieldDetails> getFields() {
		if ( fields == null ) {
			final Field[] reflectionFields = managedClass.getDeclaredFields();
			this.fields = CollectionHelper.arrayList( reflectionFields.length );
			for ( int i = 0; i < reflectionFields.length; i++ ) {
				final Field reflectionField = reflectionFields[i];
				fields.add( new JdkFieldDetails( reflectionField, getBuildingContext() ) );
			}
		}
		return fields;
	}

	@Override
	public void addField(FieldDetails fieldDetails) {
		getFields().add( fieldDetails );
	}

	@Override
	public List<MethodDetails> getMethods() {
		if ( methods == null ) {
			final Method[] reflectionMethods = managedClass.getDeclaredMethods();
			this.methods = CollectionHelper.arrayList( reflectionMethods.length );
			for ( int i = 0; i < reflectionMethods.length; i++ ) {
				this.methods.add( buildMethodDetails( reflectionMethods[i], getBuildingContext() ) );
			}
		}
		return methods;
	}

	@Override
	public void addMethod(MethodDetails methodDetails) {
		getMethods().add( methodDetails );
	}

	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		if ( !isRecord() ) {
			return Collections.emptyList();
		}
		if ( recordComponents == null ) {
			final RecordComponent[] jdkRecordComponents = managedClass.getRecordComponents();
			recordComponents = CollectionHelper.arrayList( jdkRecordComponents.length );
			for ( int i = 0; i < jdkRecordComponents.length; i++ ) {
				recordComponents.add( new JdkRecordComponentDetails( jdkRecordComponents[i], getBuildingContext() ) );
			}
		}
		return recordComponents;
	}

	@Override
	public String toString() {
		return "JdkClassDetails(" + name + ")";
	}
}
