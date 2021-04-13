/**
 *
 *    Copyright 2018-2020 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.chhorz.openapi.common.properties.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyMap;

/**
 * Base class for custom post processor properties.
 *
 * @author chhorz
 */
public abstract class AbstractPostProcessorProperties {

	private Map<String, Object> postProcessorProperties;

	public AbstractPostProcessorProperties(Map<String, Object> postProcessorProperties) {
		setPostProcessorProperties(postProcessorProperties);
	}

	public Map<String, Object> getPostProcessorProperties() {
		return postProcessorProperties;
	}

	public void setPostProcessorProperties(Map<String, Object> postProcessorProperties) {
		if (postProcessorProperties == null) {
			postProcessorProperties = emptyMap();
		}
		this.postProcessorProperties = postProcessorProperties;
	}

	/**
	 * Get a string value for the given key.
	 *
	 * @param key the property key (not null)
	 * @param defaultValue the default value if the property is not present
	 * @return the value if present, otherwise the default value
	 */
	protected String getString(final String key, final String defaultValue) {
		Objects.requireNonNull(key, "Key must not be empty.");
		return (String) postProcessorProperties.getOrDefault(key, defaultValue);
	}

	/**
	 * Get a integer value for the given key.
	 *
	 * @param key the property key (not null)
	 * @param defaultValue the default value if the property is not present
	 * @return the value if present, otherwise the default value
	 */
	protected Integer getInteger(final String key, final Integer defaultValue) {
		Objects.requireNonNull(key, "Key must not be empty.");
		return (Integer) postProcessorProperties.getOrDefault(key, defaultValue);
	}

	/**
	 * Get a boolean value for the given key.
	 *
	 * @param key the property key (not null)
	 * @param defaultValue the default value if the property is not present
	 * @return the value if present, otherwise the default value
	 */
	protected Boolean getBoolean(final String key, final boolean defaultValue) {
		Objects.requireNonNull(key, "Key must not be empty.");
		Object property = postProcessorProperties.get(key);

		if (property != null ) {
			if (property instanceof Boolean) {
				return (Boolean) property;
			} else {
				return Boolean.valueOf((String) property);
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * Get a object value for the given key.
	 *
	 * @param key the property key (not null)
	 * @param clazz the class of the custom object
	 * @return an optional containing the given value
	 */
	protected <T> Optional<T> getObject(final String key, Class<T> clazz) {
		Objects.requireNonNull(key, "Key must not be empty.");
		LinkedHashMap property = (LinkedHashMap) postProcessorProperties.get(key);

		try {
			return Optional.of(clazz.getConstructor(Map.class).newInstance(property));
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			try {
				return Optional.of(clazz.getDeclaredConstructor().newInstance());
			} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
		return Optional.empty();
	}

}
