/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.SourceModelLogging;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.MethodInfo;

/**
 * @author Steve Ebersole
 */
public class JandexClassDetails extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final ClassInfo classInfo;

	private final ClassDetails superType;
	private final List<ClassDetails> implementedInterfaces;

	private List<FieldDetails> fields;
	private List<MethodDetails> methods;

	public JandexClassDetails(ClassInfo classInfo, SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.classInfo = classInfo;

		this.superType = determineSuperType( classInfo, buildingContext );
		this.implementedInterfaces = determineInterfaces( classInfo, buildingContext );

		buildingContext.getClassDetailsRegistry().addClassDetails( this );
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

	private static List<ClassDetails> determineInterfaces(
			ClassInfo classInfo,
			SourceModelBuildingContext buildingContext) {
		final List<DotName> interfaceNames = classInfo.interfaceNames();
		if ( CollectionHelper.isEmpty( interfaceNames ) ) {
			return Collections.emptyList();
		}

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final List<ClassDetails> result = new ArrayList<>( interfaceNames.size() );
		for ( DotName interfaceName : interfaceNames ) {
			final ClassDetails interfaceDetails = classDetailsRegistry.resolveClassDetails( interfaceName.toString() );
			result.add( interfaceDetails );
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
	public boolean isAbstract() {
		return Modifier.isAbstract( classInfo.flags() );
	}

	@Override
	public ClassDetails getSuperType() {
		return superType;
	}

	@Override
	public List<ClassDetails> getImplementedInterfaceTypes() {
		return implementedInterfaces;
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
			result.add( new JandexFieldDetails( fieldInfo, getBuildingContext() ) );
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
		final List<MethodInfo> methodInfoList = classInfo.methods();
		final List<MethodDetails> result = new ArrayList<>( methodInfoList.size() );
		for ( MethodInfo methodInfo : methodInfoList ) {
			if ( methodInfo.isConstructor() || "<clinit>".equals( methodInfo.name() ) ) {
				continue;
			}
			result.add( JandexBuilders.buildMethodDetails( methodInfo, getBuildingContext() ) );
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
			SourceModelLogging.SOURCE_MODEL_LOGGER.debugf( "Loading `%s` on to classloader from Jandex ClassDetails" );
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
