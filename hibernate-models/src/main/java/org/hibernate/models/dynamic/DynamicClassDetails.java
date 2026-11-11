/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.DynamicClassException;
import org.hibernate.models.dynamic.internal.AbstractAnnotationTarget;
import org.hibernate.models.support.ClassDetailsSupport;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.serial.internal.SerialDynamicClassDetails;
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

	public DynamicClassDetails(@Nonnull String name, @Nonnull ModelsContext modelsContext) {
		this( name, null, null, modelsContext );
	}

	public DynamicClassDetails(
			@Nonnull String name,
			@Nullable ClassDetails superClass,
			@Nullable TypeDetails genericSuperType,
			@Nonnull ModelsContext modelsContext) {
		this( name, null, false, superClass, genericSuperType, modelsContext );
	}

	public DynamicClassDetails(
			@Nonnull String name,
			@Nullable String className,
			boolean isAbstract,
			@Nullable ClassDetails superClass,
			@Nullable TypeDetails genericSuperType,
			@Nonnull ModelsContext modelsContext) {
		this( name, className, null, isAbstract, superClass, genericSuperType, modelsContext );
	}

	public DynamicClassDetails(
			@Nonnull String name,
			@Nullable String className,
			@Nullable Class<?> javaType,
			boolean isAbstract,
			@Nullable ClassDetails superClass,
			@Nullable TypeDetails genericSuperType,
			@Nonnull ModelsContext modelsContext) {
		super( modelsContext );
		this.name = name;
		this.className = className;
		this.isAbstract = isAbstract;
		this.superClass = superClass;
		this.genericSuperType = genericSuperType;
		this.javaType = javaType;
	}

	@Nonnull
	@Override
	public String getName() {
		return name;
	}

	@Nullable
	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public boolean isRealClass() {
		return false;
	}

	@Override
	public boolean wasBuiltFromReflection() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
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

	@Nullable
	@Override
	public ClassDetails getSuperClass() {
		return superClass;
	}

	@Nullable
	@Override
	public TypeDetails getGenericSuperType() {
		return genericSuperType;
	}

	@Nullable
	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		// todo : do we need these for dynamic classes?
		return null;
	}

	@Nonnull
	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		return Collections.emptyList();
	}

	@Override
	public boolean isImplementor(@Nonnull Class<?> checkType) {
		return !isEmpty( className ) && checkType.isAssignableFrom( javaType );
	}

	@Nonnull
	@Override
	public List<FieldDetails> getFields() {
		if ( fields == null ) {
			return Collections.emptyList();
		}

		return fields;
	}

	@Override
	public void addField(@Nonnull FieldDetails fieldDetails) {
		if ( fields == null ) {
			this.fields = new ArrayList<>();
		}
		this.fields.add( fieldDetails );
	}

	@Nonnull
	@Override
	public List<MethodDetails> getMethods() {
		if ( methods == null ) {
			return Collections.emptyList();
		}

		return methods;
	}

	@Nonnull
	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		return Collections.emptyList();
	}

	public void addMethod(@Nonnull MethodDetails methodDetails) {
		if ( methods == null ) {
			this.methods = new ArrayList<>();
		}
		this.methods.add( methodDetails );
	}

	/**
	 * Creates a field representing an attribute and adds it to this class.
	 */
	@Nonnull
	public DynamicFieldDetails applyAttribute(
			@Nonnull String name,
			@Nonnull ClassDetails type,
			boolean isArray,
			boolean isPlural,
			@Nonnull ModelsContext context) {
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
	@Nonnull
	public DynamicFieldDetails applyAttribute(
			@Nonnull String name,
			@Nonnull TypeDetails type,
			boolean isArray,
			boolean isPlural,
			@Nonnull ModelsContext context) {
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

	@Nonnull
	@Override
	public <X> Class<X> toJavaClass() {
		return toJavaClass( getModelContext().getClassLoading(), getModelContext() );
	}

	@Nonnull
	@Override
	public <X> Class<X> toJavaClass(@Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext) {
		if ( javaType == null ) {
			if ( className == null ) {
				throw new DynamicClassException( "ClassDetails (name=" + name + ") did not specify a class-name" );
			}
			javaType = getModelContext().getClassLoading().classForName( className );
		}
		//noinspection unchecked
		return (Class<X>) javaType;
	}

	@Nonnull
	@Override
	public String toString() {
		return "DynamicClassDetails(" + name + " (" + className + "))";
	}

	@Nonnull
	@Override
	public SerialClassDetails toSerialForm() {
		return new SerialDynamicClassDetails( this );
	}
}
