/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.hibernate.models.DynamicClassException;
import org.hibernate.models.internal.AnnotationTargetHelper;
import org.hibernate.models.internal.SimpleClassDetails;
import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.serial.spi.Storable;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Abstraction for what Hibernate understands about a "class", generally before it has access to
 * the actual {@link Class} reference, if there is a {@code Class} at all (dynamic models).
 *
 * @author Steve Ebersole
 * @see ClassDetailsRegistry
 */
public interface ClassDetails extends AnnotationTarget, TypeVariableScope, Storable<ClassDetails, SerialClassDetails> {
	/**
	 * Details for {@code Object.class}
	 */
	@Nonnull
	ClassDetails OBJECT_CLASS_DETAILS = new SimpleClassDetails( Object.class );

	/**
	 * Details for {@code Class.class}
	 */
	@Nonnull
	ClassDetails CLASS_CLASS_DETAILS = new SimpleClassDetails( Class.class );

	/**
	 * Details for {@code void.class}
	 */
	@Nonnull
	ClassDetails VOID_CLASS_DETAILS = new SimpleClassDetails( void.class );

	/**
	 * Details for {@code Void.class}
	 */
	@Nonnull
	ClassDetails VOID_OBJECT_CLASS_DETAILS = new SimpleClassDetails( Void.class );

	@Nonnull
	@Override
	default Kind getKind() {
		return Kind.CLASS;
	}

	/**
	 * The name of the class.
	 * <p/>
	 * Generally this is the same as the {@linkplain #getClassName() class name}.
	 * But in the case of Hibernate's {@code entity-name} feature, this would
	 * be the {@code entity-name}
	 */
	@Nonnull
	String getName();

	/**
	 * The name of the {@link Class}, or {@code null} for dynamic models.
	 *
	 * @apiNote Will be {@code null} for dynamic models
	 */
	@Nullable
	String getClassName();

	/**
	 * Whether this descriptor represents a real Class reference.
	 * This is generally true except for dynamic and missing class references.
	 */
	default boolean isRealClass() {
		return true;
	}

	/**
	 * Whether this descriptor was built from its {@linkplain Class} details.
	 *
	 * @see org.hibernate.models.internal.jdk.JdkBuilders
	 */
	boolean wasBuiltFromReflection();

	/**
	 * Whether this descriptor represents one of the primitive classes.
	 */
	boolean isPrimitive();

	/**
	 * Whether the {@linkplain Class}, if one, represented by this ClassDetails is
	 * already loaded on the {@linkplain ClassLoader} used for {@linkplain ClassLoading loading}.
	 *
	 * @return {@code true} when there is a physical backing class, and it is loaded; {@code false} otherwise.
	 */
	boolean isResolved();

	/**
	 * Whether the class should be considered abstract.
	 */
	boolean isAbstract();

	/**
	 * Whether this class is an interface
	 */
	boolean isInterface();

	/**
	 * Whether this class is an enum
	 */
	boolean isEnum();

	/**
	 * Where the class is a Java record
	 */
	boolean isRecord();

	/**
	 * Details for the class that is the super type for this class.
	 */
	@Nullable
	ClassDetails getSuperClass();

	/**
	 * Generic type information for this class.
	 */
	@Nullable
	TypeDetails getGenericSuperType();

	/**
	 * Details for the interfaces this class implements.
	 *
	 * @return The implemented interfaces, or {@code null} when a dynamic model does not provide interface metadata
	 */
	@Nullable
	List<TypeDetails> getImplementedInterfaces();

	/**
	 * Access to the type parameters associated with this class.
	 */
	@Nonnull
	List<TypeVariableDetails> getTypeParameters();

	@FunctionalInterface
	interface ClassDetailsConsumer extends java.util.function.Consumer<ClassDetails> {
		@Override
		void accept(@Nonnull ClassDetails classDetails);
	}

	/**
	 * Walk our super-classes passing each to the {@code consumer}
	 */
	default void forEachSuper(@Nonnull ClassDetailsConsumer consumer) {
		ClassDetails check = getSuperClass();
		while ( check != null && check != OBJECT_CLASS_DETAILS ) {
			consumer.accept( check );
			check = check.getSuperClass();
		}
	}

	/**
	 * Pass ourselves into the {@code consumer} and then the same {@linkplain #forEachSuper for each super class}
	 */
	default void forSelfAndEachSuper(@Nonnull ClassDetailsConsumer consumer) {
		consumer.accept( this );
		forEachSuper( consumer );
	}

	/**
	 * Returns {@code true} is the provided classDetails is a
	 * superclass of this class, {@code false} otherwise
	 */
	default boolean isSuperclass(@Nonnull ClassDetails classDetails) {
		ClassDetails check = getSuperClass();
		while ( check != null && check != OBJECT_CLASS_DETAILS ) {
			if ( classDetails == check ) {
				return true;
			}
			check = check.getSuperClass();
		}
		return false;
	}

	@Nullable
	@Override
	default TypeDetails resolveTypeVariable(@Nonnull TypeVariableDetails typeVariable) {
		if ( this == typeVariable.getDeclaringType() ) {
			return TypeDetailsHelper.findTypeVariableDetails(
					typeVariable.getIdentifier(),
					getTypeParameters()
			);
		}

		if ( isSuperclass( typeVariable.getDeclaringType() ) ) {
			final TypeDetails genericSuperType = getGenericSuperType();
			if ( genericSuperType != null ) {
				return genericSuperType.resolveTypeVariable( typeVariable );
			}
		}

		return null;
	}

	@Nonnull
	@Override
	default ClassDetails determineRawClass() {
		return this;
	}

	/**
	 * Whether the described class is an implementor of the given {@code checkType}.
	 */
	boolean isImplementor(@Nonnull Class<?> checkType);

	/**
	 * Get the fields for this class
	 */
	@Nonnull
	List<FieldDetails> getFields();

	/**
	 * Get the constructors for this class
	 */
	@Nonnull
	default List<ConstructorDetails> getConstructors() {
		return Collections.emptyList();
	}

	/**
	 * Visit each constructor
	 */
	default void forEachConstructor(@Nonnull IndexedConsumer<ConstructorDetails> consumer) {
		final List<ConstructorDetails> constructors = getConstructors();
		for ( int i = 0; i < constructors.size(); i++ ) {
			consumer.accept( i, constructors.get( i ) );
		}
	}

	/**
	 * Find a constructor by check
	 */
	@Nullable
	default ConstructorDetails findConstructor(@Nonnull Predicate<ConstructorDetails> check) {
		final List<ConstructorDetails> constructors = getConstructors();
		for ( int i = 0; i < constructors.size(); i++ ) {
			final ConstructorDetails constructorDetails = constructors.get( i );
			if ( check.test( constructorDetails ) ) {
				return constructorDetails;
			}
		}
		return null;
	}

	/**
	 * Visit each field
	 */
	void forEachField(@Nonnull IndexedConsumer<FieldDetails> consumer);

	/**
	 * Find a field by check
	 */
	@Nullable
	default FieldDetails findField(@Nonnull Predicate<FieldDetails> check) {
		final List<FieldDetails> fields = getFields();
		for ( int i = 0; i < fields.size(); i++ ) {
			final FieldDetails fieldDetails = fields.get( i );
			if ( check.test( fieldDetails ) ) {
				return fieldDetails;
			}
		}
		return null;
	}

	/**
	 * Find a field by name
	 */
	@Nullable
	default FieldDetails findFieldByName(@Nonnull String name) {
		assert name != null;
		return findField( fieldDetails -> name.equals( fieldDetails.getName() ) );
	}

	/**
	 * Get the methods for this class
	 */
	@Nonnull
	List<MethodDetails> getMethods();

	/**
	 * Visit each method
	 */
	void forEachMethod(@Nonnull IndexedConsumer<MethodDetails> consumer);

	/**
	 * Get the record components for this class
	 */
	@Nonnull
	List<RecordComponentDetails> getRecordComponents();

	/**
	 * Find a record component by check
	 */
	@Nullable
	default RecordComponentDetails findRecordComponent(@Nonnull Predicate<RecordComponentDetails> check) {
		final List<RecordComponentDetails> components = getRecordComponents();
		for ( int i = 0; i < components.size(); i++ ) {
			final RecordComponentDetails component = components.get( i );
			if ( check.test( component ) ) {
				return component;
			}
		}
		return null;
	}

	/**
	 * Find a record component by name
	 */
	@Nullable
	default RecordComponentDetails findRecordComponentByName(@Nonnull String name) {
		assert name != null;
		return findRecordComponent( component -> name.equals( component.getName() ) );
	}

	/**
	 * Visit each method
	 */
	void forEachRecordComponent(@Nonnull IndexedConsumer<RecordComponentDetails> consumer);

	default void forEachMember(@Nonnull Consumer<MemberDetails> consumer) {
		forEachField( (i,field) -> consumer.accept( field ) );
		forEachMethod( (i,method) -> consumer.accept( method ) );
		forEachRecordComponent( (i,recordComponent) -> consumer.accept( recordComponent ) );
	}

	default void forEachPersistableMember(@Nonnull Consumer<MemberDetails> consumer) {
		forEachField( (i,field) -> {
			if ( field.isPersistable() ) {
				consumer.accept( field );
			}
		} );
		forEachMethod( (i,method) -> {
			if ( method.isPersistable() ) {
				consumer.accept( method );
			}
		} );
		forEachRecordComponent( (i,recordComponent) -> {
			// can they ever not be?
			if ( recordComponent.isPersistable() ) {
				consumer.accept( recordComponent );
			}
		} );
	}

	/**
	 * Whether this class is part of the JDK.  Best guess.
	 */
	default boolean isJdkClass() {
		String className = getClassName();
		if ( className == null ) {
			return false;
		}
		return className.startsWith("java.") || className.startsWith("jdk.");
	}

	@Nullable
	@Override
	default ClassDetails getContainer(@Nonnull ModelsContext modelsContext) {
		if ( getClassName() == null || getClassName().indexOf( "." ) <= 0 ) {
			return null;
		}
		return AnnotationTargetHelper.resolvePackageInfo( this, modelsContext );
	}

	/**
	 * Load the corresponding {@linkplain Class} using standard
	 * {@linkplain ClassLoading}.
	 *
	 * @see ModelsContext#getClassLoading()
	 *
	 * @apiNote Know what you are doing before calling this method
	 *
	 * @throws DynamicClassException If this ClassDetails does not correspond to a Java class
	 * (generally meaning {@linkplain #getClassName()} returns {@code null}).
	 */
	@Nonnull
	<X> Class<X> toJavaClass();

	/**
	 * Load the corresponding {@linkplain Class} using the specified{@linkplain ClassLoading}.
	 *
	 * @param classLoading Where to load the class from/into
	 * @param modelContext The model context
	 *
	 * @throws DynamicClassException If this ClassDetails does not correspond to a Java class
	 * (generally meaning {@linkplain #getClassName()} returns {@code null}).
	 */
	@Nonnull
	<X> Class<X> toJavaClass(@Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext);

	/**
	 * Get the {@linkplain ClassDetails} for the package containing this class, if any.
	 *
	 * @return The package-info class details, or {@code null} when the class
	 * has no package (including dynamic models).
	 *
	 * @since 2.0
	 */
	@Nullable
	default ClassDetails getPackage() {
		return null;
	}

	/**
	 * Get the {@linkplain ModuleDetails} for the module containing this class, if any.
	 *
	 * @return The module details, or {@code null} when the class is
	 * not part of a named module (including dynamic models).
	 *
	 * @since 2.0
	 */
	@Nullable
	default ModuleDetails getModule() {
		return null;
	}

	@Nonnull
	@Override
	default ClassDetails asClassDetails() {
		return this;
	}
}
