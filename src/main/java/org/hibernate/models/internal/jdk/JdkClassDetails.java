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
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.internal.util.TypeHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import static org.hibernate.models.internal.jdk.JdkBuilders.buildMethodDetails;
import static org.hibernate.models.internal.util.CollectionHelper.arrayList;

/**
 * ClassDetails implementation based on a {@link Class} reference
 *
 * @author Steve Ebersole
 */
public class JdkClassDetails extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final String name;
	private final Class<?> managedClass;

	private final ClassDetails superClass;
	private TypeDetails genericSuperType;
	private List<TypeDetails> interfaces;
	private List<TypeVariableDetails> typeParameters;

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
			superClass = null;
		}
		else {
			superClass = classDetailsRegistry.resolveClassDetails(
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
	public boolean isResolved() {
		return TypeHelper.isResolved( managedClass );
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
	public ClassDetails getSuperClass() {
		return superClass;
	}

	@Override
	public TypeDetails getGenericSuperType() {
		if ( genericSuperType == null && managedClass.getGenericSuperclass() != null ) {
			genericSuperType = new JdkTrackingTypeSwitcher( getBuildingContext() ).switchType( managedClass.getGenericSuperclass() );
		}
		return genericSuperType;
	}

	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		if ( interfaces == null ) {
			interfaces = collectInterfaces();
		}
		return interfaces;
	}

	private List<TypeDetails> collectInterfaces() {
		final Type[] jdkInterfaces = managedClass.getGenericInterfaces();
		if ( CollectionHelper.isEmpty( jdkInterfaces ) ) {
			return Collections.emptyList();
		}

		final ArrayList<TypeDetails> result = arrayList( jdkInterfaces.length );
		final JdkTrackingTypeSwitcher typeSwitcher = new JdkTrackingTypeSwitcher( getBuildingContext() );
		for ( Type jdkInterface : jdkInterfaces ) {
			final TypeDetails switchedInterfaceType = typeSwitcher.switchType( jdkInterface );
			result.add( switchedInterfaceType );
		}
		return result;
	}

	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		if ( typeParameters == null ) {
			typeParameters = collectTypeParameters();
		}
		return typeParameters;
	}

	private List<TypeVariableDetails> collectTypeParameters() {
		final TypeVariable<? extends Class<?>>[] jdkTypeParameters = managedClass.getTypeParameters();
		if ( CollectionHelper.isEmpty( jdkTypeParameters ) ) {
			return Collections.emptyList();
		}

		final ArrayList<TypeVariableDetails> result = arrayList( jdkTypeParameters.length );
		final JdkTrackingTypeSwitcher typeSwitcher = new JdkTrackingTypeSwitcher( getBuildingContext() );
		for ( TypeVariable<? extends Class<?>> jdkTypeParameter : jdkTypeParameters ) {
			result.add( (TypeVariableDetails) typeSwitcher.switchType( jdkTypeParameter ) );
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
			this.fields = arrayList( reflectionFields.length );
			for ( int i = 0; i < reflectionFields.length; i++ ) {
				final Field reflectionField = reflectionFields[i];
				fields.add( new JdkFieldDetails( reflectionField, this, getBuildingContext() ) );
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
			this.methods = arrayList( reflectionMethods.length );
			for ( int i = 0; i < reflectionMethods.length; i++ ) {
				this.methods.add( buildMethodDetails( reflectionMethods[i], this, getBuildingContext() ) );
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
			recordComponents = arrayList( jdkRecordComponents.length );
			for ( int i = 0; i < jdkRecordComponents.length; i++ ) {
				recordComponents.add( new JdkRecordComponentDetails( jdkRecordComponents[i], this, getBuildingContext() ) );
			}
		}
		return recordComponents;
	}

	@Override
	public String toString() {
		return "JdkClassDetails(" + name + ")";
	}
}
