package com.github.chhorz.openapi.spi.asciidoctor;

import com.github.chhorz.openapi.common.properties.domain.AbstractPostProcessorProperties;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class AsciidoctorAttributes extends AbstractPostProcessorProperties {

	private static final String DEFAULT_ICONS = "image";

	public AsciidoctorAttributes() {
		super(emptyMap());
	}

	public AsciidoctorAttributes(Map<String, Object> yamlProperties) {
		super(yamlProperties);
	}

	public String getIcons() {
		return getString("icons", DEFAULT_ICONS);
	}

}
