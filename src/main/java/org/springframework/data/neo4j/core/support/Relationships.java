/*
 * Copyright 2011-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.neo4j.core.support;

import static java.util.stream.Collectors.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.data.neo4j.core.mapping.Neo4jPersistentProperty;
import org.springframework.lang.Nullable;

/**
 * @author Michael J. Simons
 * @author Philipp Tölle
 */
public final class Relationships {

	/**
	 * The value for a relationship can be a scalar object (1:1), a collection (1:n), a map (1:n, but with dynamic
	 * relationship types) or a map (1:n) with properties for each relationship. This method unifies the type into
	 * something iterable, depending on the given inverse type.
	 *
	 * @param rawValue The raw value to unify
	 * @return A unified collection (Either a collection of Map.Entry for dynamic and relationships with properties or a
	 *         list of related values)
	 */
	@Nullable
	public static Collection<?> unifyRelationshipValue(Neo4jPersistentProperty property, Object rawValue) {
		Collection<?> unifiedValue;
		if (property.isDynamicAssociation()) {
			if (property.isDynamicOneToManyAssociation()) {
				unifiedValue = ((Map<String, Collection<?>>) rawValue).entrySet().stream()
						.flatMap(e -> e.getValue().stream().map(v -> new SimpleEntry(e.getKey(), v))).collect(toList());
			} else {
				unifiedValue = ((Map<String, Object>) rawValue).entrySet();
			}
		} else if (property.isRelationshipWithProperties()) {
			unifiedValue = ((Map<Object, Object>) rawValue).entrySet();
		} else if (property.isCollectionLike()) {
			unifiedValue = (Collection<Object>) rawValue;
		} else {
			unifiedValue = Collections.singleton(rawValue);
		}
		return unifiedValue;
	}

	private Relationships() {}
}
