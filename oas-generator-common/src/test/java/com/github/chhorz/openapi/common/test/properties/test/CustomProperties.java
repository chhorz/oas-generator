package com.github.chhorz.openapi.common.test.properties.test;

import com.github.chhorz.openapi.common.properties.domain.AbstractPostProcessorProperties;

import java.util.Collections;
import java.util.Map;

public class CustomProperties extends AbstractPostProcessorProperties {

	private String test;

	public CustomProperties() {
		super(Collections.emptyMap());
	}

	public CustomProperties(Map<String, Object> yamlProperties) {
		super(yamlProperties);

		test = getString("test", "abc");
	}

	public String getTest() {
		return test;
	}
}
