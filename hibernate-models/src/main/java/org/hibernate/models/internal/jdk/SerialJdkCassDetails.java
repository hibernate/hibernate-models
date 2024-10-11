/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.jdk;

import org.hibernate.models.internal.SerialCassDetails;
import org.hibernate.models.internal.SerialForm;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

public class SerialJdkCassDetails implements SerialForm<ClassDetails>, SerialCassDetails {
	private final String name;
	private final Class<?> javaType;

	public SerialJdkCassDetails(String name, Class<?> javaType) {
		this.name = name;
		this.javaType = javaType;
	}

	@Override
	public ClassDetails fromSerialForm(SourceModelBuildingContext context) {
		return new JdkClassDetails( name, javaType, context );
	}
}
