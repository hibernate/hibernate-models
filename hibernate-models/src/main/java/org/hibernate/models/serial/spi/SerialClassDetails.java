/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import org.hibernate.models.spi.ClassDetails;

/**
 * @author Steve Ebersole
 */
public interface SerialClassDetails extends StorableForm<ClassDetails> {
	String getName();

	String getClassName();
}
