/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.Type;

import static java.util.Collections.emptyList;
import static org.hibernate.models.internal.ModelsClassLogging.MODELS_CLASS_LOGGER;
import static org.hibernate.models.internal.jandex.JandexTypeSwitchStandard.TYPE_SWITCH_STANDARD;
import static org.hibernate.models.internal.util.CollectionHelper.arrayList;
import static org.hibernate.models.internal.util.CollectionHelper.isEmpty;

/**
 * @author Steve Ebersole
 */
public class JandexClassDetails extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final ClassInfo classInfo;

	private final ClassDetails superType;
	private final List<TypeDetails> implementedInterfaces;

	private List<FieldDetails> fields;
	private List<MethodDetails> methods;
	private List<RecordComponentDetails> recordComponents;

	public JandexClassDetails(ClassInfo classInfo, SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.classInfo = classInfo;

		this.superType = determineSuperType( classInfo, buildingContext );
		this.implementedInterfaces = determineInterfaces( classInfo, buildingContext );
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

	private static List<TypeDetails> determineInterfaces(
			ClassInfo classInfo,
			SourceModelBuildingContext buildingContext) {
		final List<Type> interfaceTypes = classInfo.interfaceTypes();
		if ( isEmpty( interfaceTypes ) ) {
			return emptyList();
		}

		final List<TypeDetails> result = arrayList( interfaceTypes.size() );
		for ( Type interfaceType : interfaceTypes ) {
			final TypeDetails switchedType = JandexTypeSwitcher.switchType(
					interfaceType,
					TYPE_SWITCH_STANDARD,
					buildingContext
			);
			result.add( switchedType );
		}
		return result;
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
	public boolean isRecord() {
		return classInfo.isRecord();
	}

	@Override
	public ClassDetails getSuperType() {
		return superType;
	}

	@Override
	public List<TypeDetails> getImplementedInterfaceTypes() {
		return implementedInterfaces;
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		if ( getClassName().equals( checkType.getName() ) ) {
			return true;
		}

		if ( superType != null && superType.isImplementor( checkType ) ) {
			return true;
		}

		for ( TypeDetails intf : implementedInterfaces ) {
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
			result.add( new JandexFieldDetails( fieldInfo, this, getBuildingContext() ) );
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
			result.add( new JandexRecordComponentDetails( componentInfo, this, getBuildingContext() ) );
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
			result.add( JandexBuilders.buildMethodDetails( methodInfo, this, getBuildingContext() ) );
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
			if ( getClassName() == null ) {
				throw new UnsupportedOperationException( "Not supported" );
			}
			MODELS_CLASS_LOGGER.debugf( "Loading `%s` on to classloader from Jandex ClassDetails", getClassName() );
			javaClass = getBuildingContext().getClassLoading().classForName( getClassName() );
		}
		//noinspection unchecked
		return (Class<X>) javaClass;
	}

	@Override
	public String toString() {
		return "JandexClassDetails(" + classInfo.name().toString() + ")";
	}
}
