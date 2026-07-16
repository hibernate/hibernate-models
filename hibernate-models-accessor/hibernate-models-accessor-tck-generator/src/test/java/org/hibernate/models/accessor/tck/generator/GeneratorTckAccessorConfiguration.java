/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.generator;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.generator.runtime.AccessorImplFactory;
import org.hibernate.models.accessor.tck.util.TckAccessorConfiguration;

public class GeneratorTckAccessorConfiguration implements TckAccessorConfiguration {
	@Override
	public HibernateAccessorFactory factory() {
		try {
			String factoryFqcn = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorFactory";
			String readerFqcn = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorValueReaderImpl";
			String writerFqcn = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorValueWriterImpl";
			String instantiatorFqcn = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorInstantiatorImpl";
			String multiValueReaderFqcn = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorMultiValueReaderImpl";
			String multiValueWriterFqcn = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorMultiValueWriterImpl";

			AccessorImplFactory.init( readerFqcn, writerFqcn, instantiatorFqcn,
					multiValueReaderFqcn, multiValueWriterFqcn );

			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Class<?> factoryClass = cl.loadClass( factoryFqcn );
			return (HibernateAccessorFactory) factoryClass.getDeclaredConstructor().newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException( "Failed to load generated accessor factory", e );
		}
	}
}
