/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Steve Ebersole
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EverythingBagel {
	String theString();

	Status theEnum();

	boolean theBoolean();

	byte theByte();

	short theShort();

	int theInteger();

	long theLong();

	float theFloat();

	double theDouble();

	Class<?> theClass();

	Nested theNested();

	Nested[] theNesteds();

	String[] theStrings();
}
