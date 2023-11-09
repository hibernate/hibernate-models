/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Steve Ebersole
 */
public class CollectionHelper {
	public static final int DEFAULT_LIST_CAPACITY = 10;
	public static final int MINIMUM_INITIAL_CAPACITY = 16;
	public static final float LOAD_FACTOR = 0.75f;

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(Collection collection) {
		return !isEmpty( collection );
	}

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

	@SuppressWarnings("rawtypes")
	public static boolean isNotEmpty(Map map) {
		return !isEmpty( map );
	}

	public static boolean isEmpty(Object[] objects) {
		return objects == null || objects.length == 0;
	}

	public static boolean isNotEmpty(Object[] objects) {
		return objects != null && objects.length > 0;
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

	public static <E> List<E> join(Collection<E> first, Collection<E> second) {
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

	public static <E> List<E> mutableJoin(Collection<E> first, Collection<E> second) {
		final int totalCount = ( first == null ? 0 : first.size() )
				+ ( second == null ? 0 : second.size() );
		if ( totalCount == 0 ) {
			return new ArrayList<>();
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
}
