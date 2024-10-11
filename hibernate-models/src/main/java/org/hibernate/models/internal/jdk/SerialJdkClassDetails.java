/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

public class SerialJdkClassDetails implements SerialClassDetails {
	private final String name;
	private final Class<?> javaType;

	public SerialJdkClassDetails(String name, Class<?> javaType) {
		this.name = name;
		this.javaType = javaType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassName() {
		return javaType.getName();
	}

	@Override
	public ClassDetails fromStorableForm(SourceModelBuildingContext context) {
		return new JdkClassDetails( name, javaType, context );
	}
}
