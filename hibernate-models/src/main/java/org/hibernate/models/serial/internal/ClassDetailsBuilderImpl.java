/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.util.Map;

import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private Map<String, SerialClassDetails> serialClassDetailsMap;

	public ClassDetailsBuilderImpl(Map<String, SerialClassDetails> serialClassDetailsMap, ClassLoading classLoading) {
		this.serialClassDetailsMap = serialClassDetailsMap;
	}

	@Override
	public ClassDetails buildClassDetails(String name, ModelsContext modelsContext) {
		if ( serialClassDetailsMap != null ) {
			final SerialClassDetails serialClassDetails = serialClassDetailsMap.get( name );
			if ( serialClassDetails != null ) {
				return serialClassDetails.toClassDetails( modelsContext );
			}
		}

		return JdkBuilders.DEFAULT_BUILDER.buildClassDetails( name, modelsContext );
	}

	public void invalidate() {
		serialClassDetailsMap = null;
	}
}
