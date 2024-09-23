/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Steve Ebersole
 */
public class CollectionHelper {
	public static final int DEFAULT_LIST_CAPACITY = 10;

	public static boolean isEmpty(@SuppressWarnings("rawtypes") Collection collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNotEmpty(@SuppressWarnings("rawtypes") Collection collection) {
		return !isEmpty( collection );
	}

	public static boolean isEmpty(@SuppressWarnings("rawtypes") Map map) {
		return map == null || map.isEmpty();
	}

	public static boolean isNotEmpty(@SuppressWarnings("rawtypes") Map map) {
		return !isEmpty( map );
	}

	public static boolean isEmpty(Object[] objects) {
		return objects == null || objects.length == 0;
	}

	public static boolean isNotEmpty(Object[] objects) {
		return objects != null && objects.length > 0;
	}

	public static int size(@SuppressWarnings("rawtypes") Collection collection) {
		return collection == null
				? 0
				: collection.size();
	}

	public static int size(@SuppressWarnings("rawtypes") Map map) {
		return map == null
				? 0
				: map.size();
	}

	public static int length(Object[] array) {
		return array == null
				? 0
				: array.length;
	}

	public static <T> ArrayList<T> arrayList(int expectedNumberOfElements) {
		return new ArrayList<>( Math.max( expectedNumberOfElements + 1, DEFAULT_LIST_CAPACITY ) );
	}

	public static <E> List<E> join(List<E> first, List<E> second) {
		final int totalCount = ( first == null ? 0 : first.size() )
				+ ( second == null ? 0 : second.size() );
		if ( totalCount == 0 ) {
			return Collections.emptyList();
		}
		final ArrayList<E> joined = new ArrayList<>( totalCount );
		if ( first != null ) {
			joined.addAll( first );
		}
		if ( second != null ) {
			joined.addAll( second );
		}
		return joined;
	}

	public static <T> void forEach(T[] values, Consumer<T> consumer) {
		if ( isNotEmpty( values ) ) {
			for ( int i = 0; i < values.length; i++ ) {
				consumer.accept( values[i] );
			}
		}
	}
}
