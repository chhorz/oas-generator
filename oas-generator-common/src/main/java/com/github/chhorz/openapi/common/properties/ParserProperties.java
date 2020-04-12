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
package com.github.chhorz.openapi.common.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class ParserProperties {

	private String logLevel;

	private String outputDir;
	private String outputFile;
	private String outputFormat;
	private String schemaFile;

	private List<String> schemaPackages;

	private Map<String, LinkedHashMap> postProcessor;

	public ParserProperties() {
		logLevel = GeneratorPropertiesDefaults.PARSER_LOG_LEVEL;
		outputDir = GeneratorPropertiesDefaults.PARSER_OUTPUT_DIR;
		outputFile = GeneratorPropertiesDefaults.PARSER_OUTPUT_FILE;
		outputFormat = GeneratorPropertiesDefaults.PARSER_OUTPUT_FORMAT;
		schemaFile = GeneratorPropertiesDefaults.PARSER_SCHEMA_FILE;
		schemaPackages = GeneratorPropertiesDefaults.PARSER_SCHEMA_PACKAGES;
		postProcessor = GeneratorPropertiesDefaults.PARSER_POST_PROCESSOR;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(final String logLevel) {
		this.logLevel = logLevel;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(final String outputDir) {
		this.outputDir = outputDir;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(final String outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public boolean hasJsonOutputFormat(){
		return outputFormat != null && Stream.of(outputFormat.split(","))
			.anyMatch(format -> format.equalsIgnoreCase("JSON"));
	}

	public boolean hasYamlOutputFormat(){
		return outputFormat != null && Stream.of(outputFormat.split(","))
			.map(String::toLowerCase)
			.anyMatch(format -> format.matches("ya?ml"));
	}

	public String getSchemaFile() {
		return schemaFile;
	}

	public void setSchemaFile(final String schemaFile) {
		this.schemaFile = schemaFile;
	}

	public List<String> getSchemaPackages() {
		return schemaPackages;
	}

	public void setSchemaPackages(final List<String> schemaPackages) {
		this.schemaPackages = schemaPackages;
	}

	public Map<String, LinkedHashMap> getPostProcessor() {
		return postProcessor;
	}

	public void setPostProcessor(final Map<String, LinkedHashMap> postProcessor) {
		this.postProcessor = postProcessor;
	}

	public <T extends AbstractPostProcessorProperties> Optional<T> getPostProcessor(final String key, final Class<T> clazz) {
		if (postProcessor != null) {
			LinkedHashMap yamlProperties = postProcessor.get(key);
			try {
				return Optional.of(clazz.getConstructor(Map.class).newInstance(yamlProperties));
			} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
				try {
					return Optional.ofNullable(clazz.newInstance());
				} catch (InstantiationException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
		return Optional.empty();
	}

}
