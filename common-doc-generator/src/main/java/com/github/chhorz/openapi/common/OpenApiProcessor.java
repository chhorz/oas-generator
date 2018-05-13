package com.github.chhorz.openapi.common;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.file.FileWriter;
import com.github.chhorz.openapi.common.properties.DocGeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;

public interface OpenApiProcessor {

	default Set<String> getDocGeneratorOptions() {
		return Stream.of(
					"propertiesPath",	// configurable path to properties
					"version")			// get version from build tool
				.collect(toSet());
	}

	default OpenAPI initializeFromProperties(final DocGeneratorPropertyLoader propertyLoader) {
		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.1");
		openApi.setInfo(propertyLoader.createInfoFromProperties());
		openApi.setServers(propertyLoader.createServerFromProperties());
		openApi.setExternalDocs(propertyLoader.createExternalDocsFromProperties());
		return openApi;
	}

	default void writeFile(final ParserProperties parserProperties, final OpenAPI openApi) {
		FileWriter writer = new FileWriter(parserProperties);
		writer.writeToFile(openApi);
	}

}
