/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.internal.jdk.SerialJdkClassDetails;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.RecordComponentList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import static java.util.Collections.emptyList;
import static org.hibernate.models.internal.util.CollectionHelper.arrayList;
import static org.hibernate.models.internal.util.CollectionHelper.isEmpty;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsImpl extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final TypeDescription typeDescription;

	private final ClassDetails superClassDetails;
	private TypeDetails genericSuperType;
	private List<TypeDetails> implementedInterfaces;
	private List<TypeVariableDetails> typeParameters;

	private List<FieldDetails> fields;
	private List<MethodDetails> methods;
	private List<RecordComponentDetails> recordComponents;

	public ClassDetailsImpl(TypeDescription typeDescription, ByteBuddyModelsContext modelContext) {
		super( modelContext );
		assert !typeDescription.isPrimitive();
		this.typeDescription = typeDescription;
		this.superClassDetails = determineSuperType( typeDescription, modelContext );
	}

	@Override
	protected AnnotationSource getAnnotationSource() {
		return typeDescription;
	}

	@Override
	public String getName() {
		return getClassName();
	}

	@Override
	public String getClassName() {
		return typeDescription.getTypeName();
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public boolean isAbstract() {
		return typeDescription.isAbstract();
	}

	@Override
	public boolean isInterface() {
		return typeDescription.isInterface();
	}

	@Override
	public boolean isEnum() {
		return typeDescription.isEnum();
	}

	@Override
	public boolean isRecord() {
		return typeDescription.isRecord();
	}

	@Override
	public ClassDetails getSuperClass() {
		return superClassDetails;
	}

	@Override
	public TypeDetails getGenericSuperType() {
		if ( genericSuperType == null && typeDescription.getSuperClass() != null ) {
			genericSuperType = determineGenericSuperType( typeDescription, getModelContext() );
		}
		return genericSuperType;
	}

	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		if ( implementedInterfaces == null ) {
			implementedInterfaces = determineInterfaces( typeDescription, getModelContext() );
		}
		return implementedInterfaces;
	}

	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		if ( typeParameters == null ) {
			this.typeParameters = determineTypeParameters( typeDescription, this, getModelContext() );
		}
		return typeParameters;
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return typeDescription.isAssignableTo( checkType );
	}

	@Override
	public List<FieldDetails> getFields() {
		if ( fields == null ) {
			fields = resolveFields();
		}
		return fields;
	}

	private List<FieldDetails> resolveFields() {
		final FieldList<FieldDescription.InDefinedShape> declaredFields = typeDescription.getDeclaredFields();
		final List<FieldDetails> result = new ArrayList<>( declaredFields.size() );
		for ( FieldDescription.InDefinedShape declaredField : declaredFields ) {
			result.add( new FieldDetailsImpl( declaredField, this, getModelContext() ) );
		}
		return result;
	}

	@Override
	public void addField(FieldDetails fieldDetails) {
		getFields().add( fieldDetails );
	}

	@Override
	public List<MethodDetails> getMethods() {
		if ( methods == null ) {
			methods = resolveMethods();
		}
		return methods;
	}

	private List<MethodDetails> resolveMethods() {
		final MethodList<MethodDescription.InDefinedShape> declaredMethods = typeDescription.getDeclaredMethods();
		final List<MethodDetails> result = new ArrayList<>( declaredMethods.size() );
		for ( MethodDescription.InDefinedShape declaredMethod : declaredMethods ) {
			if ( declaredMethod.isConstructor() || declaredMethod.isTypeInitializer() ) {
				continue;
			}
			result.add( ByteBuddyBuilders.buildMethodDetails( declaredMethod, this, getModelContext() ) );
		}
		return result;
	}

	@Override
	public void addMethod(MethodDetails methodDetails) {
		getMethods().add( methodDetails );
	}

	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		if ( recordComponents == null ) {
			recordComponents = resolveRecordComponents();
		}
		return recordComponents;
	}

	private List<RecordComponentDetails> resolveRecordComponents() {
		final RecordComponentList<RecordComponentDescription.InDefinedShape> recordComponents = typeDescription.getRecordComponents();
		final List<RecordComponentDetails> result = arrayList( recordComponents.size() );
		for ( RecordComponentDescription.InDefinedShape component : recordComponents ) {
			result.add( new RecordComponentDetailsImpl( component, this, getModelContext() ) );
		}
		return result;
	}

	@Override
	public <X> Class<X> toJavaClass() {
		return toJavaClass( getModelContext().getClassLoading(), getModelContext() );
	}

	@Override
	public <X> Class<X> toJavaClass(ClassLoading classLoading, ModelsContext modelContext) {
		return classLoading.classForName( getClassName() );
	}

	@Override
	public SerialClassDetails toStorableForm() {
		return new SerialJdkClassDetails( getName(), toJavaClass() );
	}

	@Override
	public String toString() {
		return "ClassDetails(" + typeDescription.getName() + ")";
	}

	private static ClassDetails determineSuperType(
			TypeDescription typeDescription,
			ModelsContext modelsContext) {
		if ( typeDescription.getSuperClass() == null ) {
			return null;
		}

		return modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( typeDescription.getSuperClass().asRawType().getTypeName() );
	}

	private static TypeDetails determineGenericSuperType(TypeDescription typeDescription, ModelsContext modelsContext) {
		if ( typeDescription.getSuperClass() == null ) {
			return null;
		}

		return TypeSwitchStandard.switchType( typeDescription.getSuperClass(), modelsContext );
	}

	private static List<TypeDetails> determineInterfaces(
			TypeDescription typeDescription,
			ModelsContext modelsContext) {
		final TypeList.Generic interfaceTypes = typeDescription.getInterfaces();
		if ( isEmpty( interfaceTypes ) ) {
			return emptyList();
		}

		final List<TypeDetails> result = arrayList( interfaceTypes.size() );
		for ( TypeDescription.Generic interfaceType : interfaceTypes ) {
			final TypeDetails switchedType = TypeSwitchStandard.switchType(
					interfaceType,
					modelsContext
			);
			result.add( switchedType );
		}
		return result;
	}

	private static List<TypeVariableDetails> determineTypeParameters(
			TypeDescription typeDescription,
			ClassDetailsImpl current,
			ModelsContext modelsContext) {
		final TypeList.Generic typeArguments = typeDescription.getTypeVariables();
		if ( CollectionHelper.isEmpty( typeArguments ) ) {
			return emptyList();
		}

		final ArrayList<TypeVariableDetails> result = arrayList( typeArguments.size() );
		for ( TypeDescription.Generic typeArgument : typeArguments ) {
			result.add( (TypeVariableDetails) TypeSwitchStandard.switchType( typeArgument, current, modelsContext ) );
		}
		return result;
	}
}
