/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import static org.hibernate.models.accessor.generator.impl.GenerationUtil.fqcnToName;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorInstantiator;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;

public interface GeneratorConstants {

	String GENERATED_FACTORY_FQCN = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorFactory";
	String FACTORY_IMPLEMENTATION_INTERNAL = fqcnToName( GENERATED_FACTORY_FQCN );

	String GENERATED_READER_IMPL = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorValueReaderImpl";
	String GENERATED_WRITER_IMPL = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorValueWriterImpl";
	String GENERATED_INSTANTIATOR_IMPL = "org.hibernate.models.accessor.generator.generated.GeneratedHibernateAccessorInstantiatorImpl";

	String READER_INTERFACE_INTERNAL = fqcnToName( HibernateAccessorValueReader.class.getName() );
	String WRITER_INTERFACE_INTERNAL = fqcnToName( HibernateAccessorValueWriter.class.getName() );
	String INSTANTIATOR_INTERFACE_INTERNAL = fqcnToName( HibernateAccessorInstantiator.class.getName() );
	String FACTORY_INTERFACE_INTERNAL = fqcnToName( HibernateAccessorFactory.class.getName() );
	String NAMING_UTIL_INTERNAL = fqcnToName( "org.hibernate.models.accessor.generator.runtime.NamingUtil" );

	String METHOD_NAME_FIELD_READER_ACCESSOR = "$$__hibernateAccessor_fieldReader";
	String METHOD_NAME_METHOD_READER_ACCESSOR = "$$__hibernateAccessor_methodReader";
	String METHOD_NAME_FIELD_WRITER_ACCESSOR = "$$__hibernateAccessor_fieldWriter";
	String METHOD_NAME_METHOD_WRITER_ACCESSOR = "$$__hibernateAccessor_methodWriter";
	String METHOD_NAME_INSTANTIATOR_ACCESSOR = "$$__hibernateAccessor_instantiator";

	String PREFIX_READ_METHOD = "$$__hibernateAccessor_read";
	String PREFIX_WRITE_METHOD = "$$__hibernateAccessor_write";
	String PREFIX_CREATE_METHOD = "$$__hibernateAccessor_create";
}
