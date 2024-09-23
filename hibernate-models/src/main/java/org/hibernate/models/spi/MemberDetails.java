/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.CollectionElementSwitch;
import org.hibernate.models.internal.MapKeySwitch;
import org.hibernate.models.internal.MapValueSwitch;
import org.hibernate.models.internal.ModifierUtils;

/**
 * Models a {@linkplain Member member} in a {@linkplain ClassDetails class} while processing annotations.
 *
 * @apiNote This can be a virtual member, meaning there is no physical member in the declaring type
 * (which itself might be virtual)
 *
 * @author Steve Ebersole
 */
public interface MemberDetails extends AnnotationTarget {
	/**
	 * The name of the member.  This would be the name of the method or field.
	 */
	String getName();

	/**
	 * The member type.  May be {@code null}
	 *
	 * @return Returns one of:<ul>
	 * <li>for a field, the field type</li>
	 * <li>for a getter method, the return type</li>
	 * <li>for a setter method, the argument type</li>
	 * <li>otherwise, {@code null}</li>
	 * </ul>
	 */
	TypeDetails getType();

	/**
	 * For plural members, the {@linkplain #getElementType "element type"};
	 * otherwise, the member's {@linkplain #getType() type}
	 */
	default TypeDetails getAssociatedType() {
		return isPlural() ? getElementType() : getType();
	}

	/**
	 * Get the plural element type for this member.  If the member does not have a type or the
	 * member is not plural, a {@code null} is returned.
	 * <p/>
	 * For arrays, lists and sets the element type is returned.
	 * <p/>
	 * For maps, the value type is returned.
	 */
	default TypeDetails getElementType() {
		final TypeDetails memberType = getType();
		if ( memberType == null ) {
			return null;
		}

		if ( memberType.getTypeKind() == TypeDetails.Kind.ARRAY ) {
			return memberType.asArrayType().getConstituentType();
		}

		if ( memberType.isImplementor( Collection.class ) ) {
			if ( memberType.getTypeKind() == TypeDetails.Kind.CLASS ) {
				// handle "concrete types" such as `class SpecialList implements List<String>`
				return CollectionElementSwitch.extractCollectionElementType( memberType );
			}
			if ( memberType.getTypeKind() == TypeDetails.Kind.PARAMETERIZED_TYPE ) {
				final ParameterizedTypeDetails parameterizedType = memberType.asParameterizedType();
				assert parameterizedType.getArguments().size() == 1;
				return parameterizedType.getArguments().get( 0 );
			}
			if ( memberType.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				assert typeVariable.getBounds().size() == 1;
				return typeVariable.getBounds().get( 0 );
			}
		}

		if ( memberType.isImplementor( Map.class ) ) {
			if ( memberType.getTypeKind() == TypeDetails.Kind.CLASS ) {
				// handle "concrete types" such as `class SpecialMap implements Map<String,String>`
				return MapValueSwitch.extractMapValueType( memberType );
			}
			if ( memberType.getTypeKind() == TypeDetails.Kind.PARAMETERIZED_TYPE ) {
				final ParameterizedTypeDetails parameterizedType = memberType.asParameterizedType();
				assert parameterizedType.getArguments().size() == 2;
				return parameterizedType.getArguments().get( 1 );
			}
			if ( memberType.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				assert typeVariable.getBounds().size() == 2;
				return typeVariable.getBounds().get( 1 );
			}
		}

		return null;
	}

	/**
	 * Get the map key type for this member.  If the member does not have a type or the
	 * member is not a map, a {@code null} is returned.
	 */
	default TypeDetails getMapKeyType() {
		final TypeDetails memberType = getType();
		if ( memberType == null ) {
			return null;
		}

		if ( memberType.isImplementor( Map.class ) ) {
			if ( memberType.getTypeKind() == TypeDetails.Kind.CLASS ) {
				// handle "concrete types" such as `class SpecialMap implements Map<String,String>`
				return MapKeySwitch.extractMapKeyType( memberType );
			}
			if ( memberType.getTypeKind() == TypeDetails.Kind.PARAMETERIZED_TYPE ) {
				final ParameterizedTypeDetails parameterizedType = memberType.asParameterizedType();
				assert parameterizedType.getArguments().size() == 2;
				return parameterizedType.getArguments().get( 0 );
			}
			if ( memberType.getTypeKind() == TypeDetails.Kind.TYPE_VARIABLE ) {
				final TypeVariableDetails typeVariable = memberType.asTypeVariable();
				assert typeVariable.getBounds().size() == 2;
				return typeVariable.getBounds().get( 0 );
			}
		}

		return null;
	}

	/**
	 * The class which declares this member
	 */
	ClassDetails getDeclaringType();

	/**
	 * For member's with an associated {@linkplain #getType() type}, whether that type considered plural.
	 *
	 * @return {@code true} When the member has a type and that type is an array or a Map or Collection inheritor
	 */
	boolean isPlural();

	boolean isArray();

	/**
	 * Access to the member modifier flags.
	 *
	 * @see Member#getModifiers()
	 */
	int getModifiers();

	/**
	 * Get the member's visibility
	 */
	default Visibility getVisibility() {
		return ModifierUtils.resolveVisibility( getModifiers() );
	}

	/**
	 * Whether the member is {@linkplain ModifierUtils#isStatic static}
	 */
	default boolean isStatic() {
		return ModifierUtils.isStatic( getModifiers() );
	}

	/**
	 * Whether the member is {@linkplain ModifierUtils#isSynthetic synthetic}
	 */
	default boolean isSynthetic() {
		return ModifierUtils.isSynthetic( getModifiers() );
	}

	default boolean isFinal() {
		return ModifierUtils.isFinal( getModifiers() );
	}

	/**
	 * Whether the member is a field.
	 *
	 * @return {@code true} indicates the member is a field; {@code false} indicates it is a method.
	 */
	default boolean isField() {
		return getKind() == Kind.FIELD;
	}

	/**
	 * Can this member be a persistent attribute
	 */
	boolean isPersistable();

	/**
	 * For members potentially representing attributes based on naming patterns, determine the attribute name.
	 * Return {@code null} if the name does not match the pattern for an attribute name.
	 *
	 * @return The potential attribute name, or {@code null}.
	 *
	 * @apiNote For a {@linkplain Kind#FIELD field}, this will be the name of the field;
	 * for a {@linkplain Kind#METHOD method}, this will be the name as determined by the
	 * Java Bean naming pattern.
	 */
	String resolveAttributeName();

	/**
	 * Access to the underlying {@linkplain Member}.  May return {@code null}.  May throw an exception.
	 *
	 * @return The underlying member, or {@code null} if there is no underlying member.
	 *
	 * @throws RuntimeException If there is expected to be a member, but it cannot be located.
	 */
	Member toJavaMember();

	/**
	 * Determine the type of the member relative to the given {@code container} type.
	 * <p/>
	 * For example, given
	 * <pre class="brush:java">
	 * {@code class Thing<T extends Number>} {
	 *     T id;
	 * }
	 * {@code class SubThing extends Thing<Integer>} {
	 *     ...
	 * }
	 * </pre>
	 * Accessing the {@code id} member relative to {@code Thing} simply returns the
	 * {@linkplain TypeVariableDetails type variable} {@code T}.  However, asking the
	 * {@code id} member for its type relative to {@code SubThing} will report
	 * {@code Integer}.
	 *
	 * @throws IllegalStateException If called on a member other than a field,
	 * getter, setter or record component.
	 * @apiNote It is only valid to call this on members which have a type, i.e. fields,
	 * getters, setters and record components.
	 */
	default TypeDetails resolveRelativeType(TypeVariableScope container) {
		return getType().determineRelativeType( container );
	}

	/**
	 * Same as {@link #resolveRelativeType(TypeVariableScope)}, but for the
	 * {@linkplain #getAssociatedType() associated type}.
	 *
	 * @see #getAssociatedType()
	 * @see #resolveRelativeType(TypeVariableScope)
	 */
	default TypeDetails resolveRelativeAssociatedType(TypeVariableScope container) {
		return getAssociatedType().determineRelativeType( container );
	}

	/**
	 * Determine the concrete class of the member relative to the given {@code container} type.
	 * <p/>
	 * Similar to {@linkplain #resolveRelativeType(TypeVariableScope)}, but fully resolving the result
	 * into the concrete class.
	 */
	default ClassBasedTypeDetails resolveRelativeClassType(TypeVariableScope container) {
		return TypeDetailsHelper.resolveRelativeClassType( getType(), container );
	}


	@Override
	default MemberDetails asMemberDetails() {
		return this;
	}

	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "MemberDetails cannot be cast to an AnnotationDescriptor" );
	}

	@Override
	default ClassDetails asClassDetails() {
		throw new IllegalCastException( "MemberDetails cannot be cast to a ClassDetails" );
	}

	enum Visibility {
		PUBLIC,
		PROTECTED,
		PACKAGE,
		PRIVATE
	}
}
