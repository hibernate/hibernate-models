/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.models.spi.ClassDetailsRegistry;

/**
 * Base set of classes used to prime the {@link ClassDetailsRegistry}..
 *
 * @author Steve Ebersole
 */
public class BaseLineJavaTypes {

	public static final Class<?>[] BASELINE_JAVA_TYPES = new Class[] {
			Object.class,
			Serializable.class,
			Cloneable.class,
			Externalizable.class,
			Comparator.class,
			Comparable.class,
			CharSequence.class,
			String.class,
			Boolean.class,
			Enum.class,
			Byte.class,
			Number.class,
			Short.class,
			Integer.class,
			Long.class,
			Double.class,
			Float.class,
			BigInteger.class,
			BigDecimal.class,
			Blob.class,
			Clob.class,
			NClob.class,
			Instant.class,
			LocalDate.class,
			LocalTime.class,
			LocalDateTime.class,
			OffsetTime.class,
			OffsetDateTime.class,
			ZonedDateTime.class,
			java.util.Date.class,
			java.sql.Date.class,
			java.sql.Time.class,
			java.sql.Timestamp.class,
			Temporal.class,
			TemporalAdjuster.class,
			TemporalAccessor.class,
			ChronoLocalDate.class,
			ChronoLocalDateTime.class,
			ChronoZonedDateTime.class,
			TimeZone.class,
			Locale.class,
			UUID.class,
			URL.class,
			Collection.class,
			Set.class,
			List.class,
			Map.class,
			SortedSet.class,
			SortedMap.class,
			Iterable.class,
			Consumer.class,
			BiConsumer.class,
			Function.class,
			BiFunction .class,
			Annotation.class,
			ElementType.class,
			RetentionPolicy.class,
			Deprecated.class,
			Documented.class,
			Target.class,
			Retention.class,
			Inherited.class,
			FunctionalInterface.class
	};

	public static void forEachJavaType(Consumer<Class<?>> consumer) {
		for ( int i = 0; i < BASELINE_JAVA_TYPES.length; i++ ) {
			consumer.accept( BASELINE_JAVA_TYPES[i] );
		}
	}

}
