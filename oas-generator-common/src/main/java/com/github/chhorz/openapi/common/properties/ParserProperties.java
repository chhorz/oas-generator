package com.github.chhorz.openapi.common.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParserProperties {

	private String logLevel;

	private String outputDir;
	private String outputFile;
	private String schemaFile;

	private List<String> schemaPackages;

	private Map<String, LinkedHashMap> postProcessor;

	public ParserProperties() {
		logLevel = GeneratorPropertiesDefaults.PARSER_LOG_LEVEL;
		outputDir = GeneratorPropertiesDefaults.PARSER_OUTPUT_DIR;
		outputFile = GeneratorPropertiesDefaults.PARSER_OUTPUT_FILE;
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
		return Optional.empty();
	}

}
