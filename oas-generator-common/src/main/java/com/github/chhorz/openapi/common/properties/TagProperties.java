package com.github.chhorz.openapi.common.properties;

public class TagProperties {

	private String description;
	private ExternalDocsProperties externalDocs;

	public TagProperties() {
		description = GeneratorPropertiesDefaults.TAG_DESCRIPTION;
		externalDocs = new ExternalDocsProperties();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ExternalDocsProperties getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(final ExternalDocsProperties externalDocs) {
		this.externalDocs = externalDocs;
	}

}
