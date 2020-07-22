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
package com.github.chhorz.openapi.spi.asciidoctor;

import com.github.chhorz.openapi.common.properties.domain.AbstractPostProcessorProperties;

import java.util.Collections;
import java.util.Map;

/**
 * Java class representing the configuration properties for the Asciidoctor post processor.
 * <p>
 * Default configuration in YAML format:
 * <pre>
ptio
 *       templateFile: openapi.ftlh
 *       outputPath: ./target/openapi
 *       outputFile: /openapi.adoc
 *       standaloneFile: true
 * </pre>
 *
 * @author chhorz
 */
public  class AsciidoctorProperties extends AbstractPostProcessorProperties {

	private static final boolean DEFAULT_EXCEPTION_LOGGING = true;
	private static final boolean DEFAULT_LOCALIZED_LOOKUP = false;
	private static final String DEFAULT_TEMPLATE_PATH = "/freemarker";
	private static final String DEFAULT_TEMPLATE_NAME = "openapi.ftlh";

	private static final String DEFAULT_OUTPUT_PATH = "./target/openapi";
	private static final String DEFAULT_OUTPUT_FILE = "openapi.adoc";
	private static final boolean DEFAULT_STANDALONE_FILE = true;

	public AsciidoctorProperties(){
		super(Collections.emptyMap());
	}

	public AsciidoctorProperties(Map<String, Object> yamlPropertyMap) {
		super(yamlPropertyMap);
	}

	public boolean getExceptionLogging() {
		return getBoolean("logging", DEFAULT_EXCEPTION_LOGGING);
	}

	public boolean getLocalizedLookup() {
		return getBoolean("templateLocalizedLookup", DEFAULT_LOCALIZED_LOOKUP);
	}

	public String getTemplatePath() {
		return getString("templatePath", DEFAULT_TEMPLATE_PATH);
	}

	public String getTemplateFile() {
		return getString("templateFile", DEFAULT_TEMPLATE_NAME);
	}

	public String getOutputPath() {
		return getString("outputPath", DEFAULT_OUTPUT_PATH);
	}

	public String getOutputFile() {
		return getString("outputFile", DEFAULT_OUTPUT_FILE);
	}

	public boolean getStandaloneFile() {
		return getBoolean("standaloneFile", DEFAULT_STANDALONE_FILE);
	}

}
