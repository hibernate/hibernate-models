/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private StorableContextImpl serialContext;

	public ClassDetailsBuilderImpl(StorableContextImpl serialContext, ClassLoading classLoading) {
		this.serialContext = serialContext;
	}

	@Override
	public ClassDetails buildClassDetails(String name, SourceModelBuildingContext buildingContext) {
		if ( serialContext == null ) {
			throw new IllegalStateException( "Building context is now immutable" );
		}
		final SerialClassDetails serialClassDetails = serialContext.getSerialClassDetailsMap().get( name );
		return serialClassDetails.fromStorableForm( buildingContext );
	}

	public void invalidate() {
		serialContext = null;
	}
}
