/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.DynamicClassException;
import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.internal.jdk.SerialJdkClassDetails;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;

import static java.util.Collections.emptyList;
import static org.hibernate.models.internal.ModelsClassLogging.MODELS_CLASS_LOGGER;
import static org.hibernate.models.internal.util.CollectionHelper.arrayList;
import static org.hibernate.models.internal.util.CollectionHelper.isEmpty;

/**
 * @author Steve Ebersole
 */
public class JandexClassDetails extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final ClassInfo classInfo;

	private final ClassDetails superClass;
	private TypeDetails genericSuperType;
	private List<TypeDetails> implementedInterfaces;
	private List<TypeVariableDetails> typeParameters;

	private List<FieldDetails> fields;
	private List<MethodDetails> methods;
	private List<RecordComponentDetails> recordComponents;

	public JandexClassDetails(ClassInfo classInfo, SourceModelBuildingContext modelContext) {
		super( modelContext );
		this.classInfo = classInfo;

		this.superClass = determineSuperType( classInfo, modelContext );
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return classInfo;
	}

	@Override
	public String getName() {
		return getClassName();
	}

	@Override
	public String getClassName() {
		return classInfo.name().toString();
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract( classInfo.flags() );
	}

	@Override
	public boolean isInterface() {
		return classInfo.isInterface();
	}

	@Override
	public boolean isEnum() {
		return classInfo.isEnum();
	}

	@Override
	public boolean isRecord() {
		return classInfo.isRecord();
	}

	@Override
	public ClassDetails getSuperClass() {
		return superClass;
	}

	@Override
	public TypeDetails getGenericSuperType() {
		if ( genericSuperType == null && classInfo.superClassType() != null ) {
			genericSuperType = determineGenericSuperType( classInfo, getModelContext() );
		}
		return genericSuperType;
	}

	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		if ( implementedInterfaces == null ) {
			implementedInterfaces = determineInterfaces( classInfo, getModelContext() );
		}
		return implementedInterfaces;
	}

	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		if ( typeParameters == null ) {
			this.typeParameters = determineTypeParameters( classInfo, this, getModelContext() );
		}
		return typeParameters;
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		if ( getClassName().equals( checkType.getName() ) ) {
			return true;
		}

		if ( superClass != null && superClass.isImplementor( checkType ) ) {
			return true;
		}

		for ( TypeDetails intf : getImplementedInterfaces() ) {
			if ( intf.isImplementor( checkType ) ) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<FieldDetails> getFields() {
		if ( fields == null ) {
			fields = resolveFields();
		}
		return fields;
	}

	private List<FieldDetails> resolveFields() {
		final List<FieldInfo> fieldsInfoList = classInfo.fields();
		final List<FieldDetails> result = new ArrayList<>( fieldsInfoList.size() );
		for ( FieldInfo fieldInfo : fieldsInfoList ) {
			result.add( new JandexFieldDetails( fieldInfo, this, getModelContext() ) );
		}
		return result;
	}

	@Override
	public void addField(FieldDetails fieldDetails) {
		getFields().add( fieldDetails );
	}

	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		if ( recordComponents == null ) {
			recordComponents = resolveRecordComponents();
		}
		return recordComponents;
	}

	private List<RecordComponentDetails> resolveRecordComponents() {
		final List<RecordComponentInfo> componentInfoList = classInfo.recordComponents();
		final List<RecordComponentDetails> result = arrayList( componentInfoList.size() );
		for ( RecordComponentInfo componentInfo : componentInfoList ) {
			result.add( new JandexRecordComponentDetails( componentInfo, this, getModelContext() ) );
		}
		return result;
	}

	@Override
	public List<MethodDetails> getMethods() {
		if ( methods == null ) {
			methods = resolveMethods();
		}
		return methods;
	}

	private List<MethodDetails> resolveMethods() {
		final List<MethodInfo> methodInfoList = classInfo.methods();
		final List<MethodDetails> result = new ArrayList<>( methodInfoList.size() );
		for ( MethodInfo methodInfo : methodInfoList ) {
			if ( methodInfo.isConstructor() || "<clinit>".equals( methodInfo.name() ) ) {
				continue;
			}
			result.add( JandexBuilders.buildMethodDetails( methodInfo, this, getModelContext() ) );
		}
		return result;
	}

	@Override
	public void addMethod(MethodDetails methodDetails) {
		getMethods().add( methodDetails );
	}

	private Class<?> javaClass;

	@Override
	public <X> Class<X> toJavaClass() {
		if ( javaClass == null ) {
			javaClass = toJavaClass( getModelContext().getClassLoading(), getModelContext() );
		}
		//noinspection unchecked
		return (Class<X>) javaClass;
	}

	@Override
	public <X> Class<X> toJavaClass(ClassLoading classLoading, SourceModelContext modelContext) {
		if ( getClassName() == null ) {
			throw new DynamicClassException( "ClassDetails (name=" + getName() + ") did not specify a class-name" );
		}
		MODELS_CLASS_LOGGER.debugf( "Loading `%s` on to classloader from Jandex ClassDetails", getClassName() );
		return classLoading.classForName( getClassName() );
	}

	@Override
	public String toString() {
		return "JandexClassDetails(" + classInfo.name().toString() + ")";
	}

	private static ClassDetails determineSuperType(
			ClassInfo classInfo,
			SourceModelBuildingContext buildingContext) {
		if ( classInfo.superClassType() == null ) {
			return null;
		}

		return buildingContext
				.getClassDetailsRegistry()
				.resolveClassDetails( classInfo.superClassType().name().toString() );
	}

	private static TypeDetails determineGenericSuperType(ClassInfo classInfo, SourceModelBuildingContext buildingContext) {
		if ( classInfo.superClassType() == null ) {
			return null;
		}

		return JandexTypeSwitchStandard.switchType( classInfo.superClassType(), buildingContext );
	}

	private static List<TypeDetails> determineInterfaces(
			ClassInfo classInfo,
			SourceModelBuildingContext buildingContext) {
		final List<Type> interfaceTypes = classInfo.interfaceTypes();
		if ( isEmpty( interfaceTypes ) ) {
			return emptyList();
		}

		final List<TypeDetails> result = arrayList( interfaceTypes.size() );
		for ( Type interfaceType : interfaceTypes ) {
			final TypeDetails switchedType = JandexTypeSwitchStandard.switchType(
					interfaceType,
					buildingContext
			);
			result.add( switchedType );
		}
		return result;
	}

	private static List<TypeVariableDetails> determineTypeParameters(ClassInfo classInfo, JandexClassDetails current, SourceModelBuildingContext buildingContext) {
		final List<TypeVariable> jandexTypeVariables = classInfo.typeParameters();
		if ( CollectionHelper.isEmpty( jandexTypeVariables ) ) {
			return emptyList();
		}

		final ArrayList<TypeVariableDetails> result = arrayList( jandexTypeVariables.size() );
		for ( TypeVariable jandexTypeVariable : jandexTypeVariables ) {
			result.add( (TypeVariableDetails) JandexTypeSwitchStandard.switchType( jandexTypeVariable, current, buildingContext ) );
		}
		return result;
	}

	@Override
	public SerialClassDetails toStorableForm() {
		return new SerialJdkClassDetails( getName(), toJavaClass() );
	}
}
