/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.DynamicClassException;
import org.hibernate.models.internal.ClassDetailsSupport;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import static org.hibernate.models.internal.ModifierUtils.DYNAMIC_ATTRIBUTE_MODIFIERS;
import static org.hibernate.models.internal.util.StringHelper.isEmpty;

/**
 * ClassDetails which does not necessarily map to a physical Class (dynamic models)
 *
 * @author Steve Ebersole
 */
public class DynamicClassDetails extends AbstractAnnotationTarget implements ClassDetailsSupport {
	private final String name;
	private final String className;
	private final boolean isAbstract;
	private final ClassDetails superClass;
	private final TypeDetails genericSuperType;

	private List<FieldDetails> fields;
	private List<MethodDetails> methods;

	private Class<?> javaType;

	public DynamicClassDetails(String name, ModelsContext modelsContext) {
		this( name, null, null, modelsContext );
	}

	public DynamicClassDetails(
			String name,
			ClassDetails superClass,
			TypeDetails genericSuperType,
			ModelsContext modelsContext) {
		this( name, null, false, superClass, genericSuperType, modelsContext );
	}

	public DynamicClassDetails(
			String name,
			String className,
			boolean isAbstract,
			ClassDetails superClass,
			TypeDetails genericSuperType,
			ModelsContext modelsContext) {
		this( name, className, null, isAbstract, superClass, genericSuperType, modelsContext );
	}

	public DynamicClassDetails(
			String name,
			String className,
			Class<?> javaType,
			boolean isAbstract,
			ClassDetails superClass,
			TypeDetails genericSuperType,
			ModelsContext modelsContext) {
		super( modelsContext );
		this.name = name;
		this.className = className;
		this.isAbstract = isAbstract;
		this.superClass = superClass;
		this.genericSuperType = genericSuperType;
		this.javaType = javaType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean isInterface() {
		// we never consider dynamic classes as interfaces
		return false;
	}

	@Override
	public boolean isEnum() {
		// a dynamic enum makes no sense
		return false;
	}

	@Override
	public boolean isRecord() {
		return false;
	}

	@Override
	public ClassDetails getSuperClass() {
		return superClass;
	}

	@Override
	public TypeDetails getGenericSuperType() {
		return genericSuperType;
	}

	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		// todo : do we need these for dynamic classes?
		return null;
	}

	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		return Collections.emptyList();
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return !isEmpty( className ) && checkType.isAssignableFrom( javaType );
	}

	@Override
	public List<FieldDetails> getFields() {
		if ( fields == null ) {
			return Collections.emptyList();
		}

		return fields;
	}

	@Override
	public void addField(FieldDetails fieldDetails) {
		if ( fields == null ) {
			this.fields = new ArrayList<>();
		}
		this.fields.add( fieldDetails );
	}

	@Override
	public List<MethodDetails> getMethods() {
		if ( methods == null ) {
			return Collections.emptyList();
		}

		return methods;
	}

	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		return Collections.emptyList();
	}

	public void addMethod(MethodDetails methodDetails) {
		if ( methods == null ) {
			this.methods = new ArrayList<>();
		}
		this.methods.add( methodDetails );
	}

	/**
	 * Creates a field representing an attribute and adds it to this class.
	 */
	public DynamicFieldDetails applyAttribute(
			String name,
			ClassDetails type,
			boolean isArray,
			boolean isPlural,
			ModelsContext context) {
		return applyAttribute(
				name,
				new ClassTypeDetailsImpl( type, TypeDetails.Kind.CLASS ),
				isArray,
				isPlural,
				context
		);
	}

	/**
	 * Creates a field representing an attribute and adds it to this class
	 */
	public DynamicFieldDetails applyAttribute(
			String name,
			TypeDetails type,
			boolean isArray,
			boolean isPlural,
			ModelsContext context) {
		final DynamicFieldDetails attribute = new DynamicFieldDetails(
				name,
				type,
				this,
				DYNAMIC_ATTRIBUTE_MODIFIERS,
				isArray,
				isPlural,
				context
		);
		addField( attribute );
		return attribute;
	}

	@Override
	public <X> Class<X> toJavaClass() {
		return toJavaClass( getModelContext().getClassLoading(), getModelContext() );
	}

	@Override
	public <X> Class<X> toJavaClass(ClassLoading classLoading, ModelsContext modelContext) {
		if ( javaType == null ) {
			if ( className == null ) {
				throw new DynamicClassException( "ClassDetails (name=" + name + ") did not specify a class-name" );
			}
			javaType = getModelContext().getClassLoading().classForName( className );
		}
		//noinspection unchecked
		return (Class<X>) javaType;
	}

	@Override
	public String toString() {
		return "DynamicClassDetails(" + name + " (" + className + "))";
	}

	@Override
	public SerialClassDetails toStorableForm() {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}
}
