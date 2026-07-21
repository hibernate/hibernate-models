/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.io.Serial;
import java.util.Objects;

import org.hibernate.models.jdk.JdkClassDetails;
import org.hibernate.models.jdk.JdkBuilders;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;

/**
 * Backend-neutral serial representation of a {@link ClassDetails}.
 *
 * @author Steve Ebersole
 */
public class SerialClassDetailsImpl implements SerialClassDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	private final String name;
	private final String className;

	public SerialClassDetailsImpl(String name, String className) {
		this.name = Objects.requireNonNull( name, "name" );
		this.className = Objects.requireNonNull( className, "className" );
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
	public ClassDetails toClassDetails(ModelsContext context) {
		final ClassDetails classDetails = JdkBuilders.DEFAULT_BUILDER.buildClassDetails( className, context );
		if ( name.equals( classDetails.getName() ) ) {
			return classDetails;
		}
		return new JdkClassDetails( name, classDetails.toJavaClass( context.getClassLoading(), context ), context );
	}
}
