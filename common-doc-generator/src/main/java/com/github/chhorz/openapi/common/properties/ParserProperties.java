package com.github.chhorz.openapi.common.properties;

public class ParserProperties {

	private String outputDir;
	private String outputFile;
	private String resourcePackage;

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

	public String getResourcePackage() {
		return resourcePackage;
	}

	public void setResourcePackage(final String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

}
