package com.github.chhorz.openapi.common;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.file.FileWriter;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;

public interface OpenAPIProcessor {

	default Set<String> getDocGeneratorOptions() {
		return Stream.of(
					OpenAPIConstants.OPTION_PROPERTIES_PATH,
					OpenAPIConstants.OPTION_SCHEMA_FILE_PATH,
					OpenAPIConstants.OPTION_VERSION)
				.collect(toSet());
	}

	default OpenAPI initializeFromProperties(final GeneratorPropertyLoader propertyLoader) {
		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.1");
		openApi.setInfo(propertyLoader.createInfoFromProperties());
		openApi.setServers(propertyLoader.createServerFromProperties());
		openApi.setExternalDocs(propertyLoader.createExternalDocsFromProperties());

		Components components = new Components();
		components.setSecuritySchemes(propertyLoader.createSecuritySchemesFromProperties());
		openApi.setComponents(components);

		return openApi;
	}

	default void writeFile(final ParserProperties parserProperties, final OpenAPI openApi) {
		FileWriter writer = new FileWriter(parserProperties);
		writer.writeToFile(openApi);
	}

}
