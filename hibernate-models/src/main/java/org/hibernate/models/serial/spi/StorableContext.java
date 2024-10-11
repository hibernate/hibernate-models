/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.io.Serializable;

import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;

/**
 * Form of {@linkplain SourceModelContext} which is serializable.
 *
 * @see SourceModelBuildingContext#toStorableForm()
 *
 * @author Steve Ebersole
 */
public interface StorableContext extends Serializable {
	/**
	 * "Re-construct" the model context from the serial form
	 */
	SourceModelContext fromStorableForm(ClassLoading classLoading);
}
