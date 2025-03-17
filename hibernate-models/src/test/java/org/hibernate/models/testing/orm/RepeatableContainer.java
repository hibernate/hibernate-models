/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;

/**
 * @author Steve Ebersole
 */
public interface RepeatableContainer<R extends Annotation> extends Annotation {
	R[] value();
	void value(R[] value);
}
