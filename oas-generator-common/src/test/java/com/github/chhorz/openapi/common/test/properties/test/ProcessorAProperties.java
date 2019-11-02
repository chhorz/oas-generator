package com.github.chhorz.openapi.common.test.properties.test;

import com.github.chhorz.openapi.common.properties.AbstractPostProcessorProperties;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessorAProperties extends AbstractPostProcessorProperties {

	private String valueOne;
	private Integer valueTwo;

	public ProcessorAProperties(Map<String, Object> yamlProperties) {
		super(yamlProperties);

		valueOne = getString("valueOne", "DEFAULT");
		valueTwo = getInteger("valueTwo", 0);
	}

	public String getValueOne() {
		return valueOne;
	}

	public Integer getValueTwo() {
		return valueTwo;
	}

}
