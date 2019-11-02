package com.github.chhorz.openapi.common.properties;

import java.util.Map;
import java.util.Objects;

/**
 * Base class for custom post processor properties.
 *
 * @author chhorz
 */
public abstract class AbstractPostProcessorProperties {

	private Map<String, Object> postProcessorProperties;

	public AbstractPostProcessorProperties(Map<String, Object> yamlPropertyMap) {
		this.postProcessorProperties = yamlPropertyMap;
	}

	public Map<String, Object> getPostProcessorProperties() {
		return postProcessorProperties;
	}

	public void setPostProcessorProperties(Map<String, Object> postProcessorProperties) {
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

}
