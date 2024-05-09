/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Predicate;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.AnnotationHelper;
import org.hibernate.models.internal.RenderingCollectorImpl;
import org.hibernate.models.internal.SimpleClassDetails;
import org.hibernate.models.internal.util.IndexedConsumer;

/**
 * Abstraction for what Hibernate understands about a "class", generally before it has access to
 * the actual {@link Class} reference, if there is a {@code Class} at all (dynamic models).
 *
 * @author Steve Ebersole
 * @see ClassDetailsRegistry
 */
public interface ClassDetails extends AnnotationTarget, TypeVariableScope {
	/**
	 * Details for {@code Object.class}
	 */
	ClassDetails OBJECT_CLASS_DETAILS = new SimpleClassDetails( Object.class );

	/**
	 * Details for {@code Class.class}
	 */
	ClassDetails CLASS_CLASS_DETAILS = new SimpleClassDetails( Class.class );

	/**
	 * Details for {@code void.class}
	 */
	ClassDetails VOID_CLASS_DETAILS = new SimpleClassDetails( void.class );

	/**
	 * Details for {@code Void.class}
	 */
	ClassDetails VOID_OBJECT_CLASS_DETAILS = new SimpleClassDetails( Void.class );

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
	String getName();

	/**
	 * The name of the {@link Class}, or {@code null} for dynamic models.
	 *
	 * @apiNote Will be {@code null} for dynamic models
	 */
	String getClassName();

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
	 * Where the class is a Java record
	 */
	boolean isRecord();

	/**
	 * Details for the class that is the super type for this class.
	 */
	ClassDetails getSuperClass();

	TypeDetails getGenericSuperType();

	/**
	 * Details for the interfaces this class implements.
	 */
	List<TypeDetails> getImplementedInterfaces();

	/**
	 * Access to the type parameters associated with this class.
	 */
	List<TypeVariableDetails> getTypeParameters();

	@FunctionalInterface
	interface ClassDetailsConsumer extends java.util.function.Consumer<ClassDetails> {
		@Override
		void accept(ClassDetails classDetails);
	}

	/**
	 * Walk our super-classes passing each to the {@code consumer}
	 */
	default void forEachSuper(ClassDetailsConsumer consumer) {
		ClassDetails check = getSuperClass();
		while ( check != null && check != OBJECT_CLASS_DETAILS ) {
			consumer.accept( check );
			check = check.getSuperClass();
		}
	}

	/**
	 * Pass ourselves into the {@code consumer} and then the same {@linkplain #forEachSuper for each super class}
	 */
	default void forSelfAndEachSuper(ClassDetailsConsumer consumer) {
		consumer.accept( this );
		forEachSuper( consumer );
	}

	/**
	 * Returns {@code true} is the provided classDetails is a
	 * superclass of this class, {@code false} otherwise
	 */
	default boolean isSuperclass(ClassDetails classDetails) {
		ClassDetails check = getSuperClass();
		while ( check != null && check != OBJECT_CLASS_DETAILS ) {
			if ( classDetails == check ) {
				return true;
			}
			check = check.getSuperClass();
		}
		return false;
	}

	@Override
	default TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
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

	@Override
	default ClassDetails determineRawClass() {
		return this;
	}

	/**
	 * Whether the described class is an implementor of the given {@code checkType}.
	 */
	boolean isImplementor(Class<?> checkType);

	/**
	 * Get the fields for this class
	 */
	List<FieldDetails> getFields();

	/**
	 * Visit each field
	 */
	void forEachField(IndexedConsumer<FieldDetails> consumer);

	/**
	 * Find a field by check
	 */
	default FieldDetails findField(Predicate<FieldDetails> check) {
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
	default FieldDetails findFieldByName(String name) {
		assert name != null;
		return findField( fieldDetails -> name.equals( fieldDetails.getName() ) );
	}

	/**
	 * Get the methods for this class
	 */
	List<MethodDetails> getMethods();

	/**
	 * Visit each method
	 */
	void forEachMethod(IndexedConsumer<MethodDetails> consumer);

	/**
	 * Get the record components for this class
	 */
	List<RecordComponentDetails> getRecordComponents();

	/**
	 * Find a record component by check
	 */
	default RecordComponentDetails findRecordComponent(Predicate<RecordComponentDetails> check) {
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
	default RecordComponentDetails findRecordComponentByName(String name) {
		assert name != null;
		return findRecordComponent( component -> name.equals( component.getName() ) );
	}

	/**
	 * Know what you are doing before calling this method
	 */
	<X> Class<X> toJavaClass();

	@Override
	default ClassDetails asClassDetails() {
		return this;
	}

	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "ClassDetails cannot be cast to AnnotationDescriptor" );
	}

	@Override
	default MemberDetails asMemberDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to MemberDescriptor" );
	}

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to RecordComponentDetails" );
	}

	@Override
	default void render(SourceModelBuildingContext modelContext) {
		final RenderingCollectorImpl renderingCollector = new RenderingCollectorImpl();
		render( renderingCollector, modelContext );
		renderingCollector.render();
	}

	@Override
	default void render(RenderingCollector collector, SourceModelBuildingContext modelContext) {
		forEachDirectAnnotationUsage( (usage) -> AnnotationHelper.render( collector, usage, modelContext ) );

		final String pattern = isRecord()
				? "record %s {"
				: "class %s {";

		collector.addLine( pattern, getName() );
		collector.indent( 1 );

		collector.addLine( "// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		collector.addLine( "// fields" );
		getFields().forEach( (fieldDetails) -> {
			fieldDetails.render( collector, modelContext );
			collector.addLine();
		} );
		collector.addLine();

		collector.addLine( "// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		collector.addLine( "// methods" );
		getMethods().forEach( (methodDetails) -> {
			methodDetails.render( collector, modelContext );
			collector.addLine();
		} );
		collector.addLine();

		if ( isRecord() ) {
			collector.addLine( "// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
			collector.addLine( "// record components" );
			getRecordComponents().forEach( (recordComponentDetails) -> {
				recordComponentDetails.render( collector, modelContext );
				collector.addLine();
			} );
		}

		collector.unindent( 1 );
		collector.addLine( "}" );
	}
}
