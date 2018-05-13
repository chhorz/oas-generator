package com.github.chhorz.openapi.common.properties;

import java.util.List;

public class ParserProperties {

	private String logLevel;

	private String outputDir;
	private String outputFile;
	private List<String> resourcePackages;

	public ParserProperties() {
		logLevel = GeneratorPropertiesDefaults.PARSER_LOG_LEVEL;
		outputDir = GeneratorPropertiesDefaults.PARSER_OUTPUT_DIR;
		outputFile = GeneratorPropertiesDefaults.PARSER_OUTPUT_FILE;
		resourcePackages = GeneratorPropertiesDefaults.PARSER_RESOURCE_PACKAGE;
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

	public List<String> getResourcePackages() {
		return resourcePackages;
	}

	public void setResourcePackages(final List<String> resourcePackages) {
		this.resourcePackages = resourcePackages;
	}

}
