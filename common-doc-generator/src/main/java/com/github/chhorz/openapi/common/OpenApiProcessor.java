package com.github.chhorz.openapi.common;

import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.file.FileWriter;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.properties.SpecGeneratorPropertyLoader;

public interface OpenApiProcessor {

	default OpenAPI initializeFromProperties(final SpecGeneratorPropertyLoader propertyLoader) {
		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi("3.0.1");
		openApi.setInfo(propertyLoader.createInfoFromProperties());
		openApi.addServer(propertyLoader.createServerFromProperties());
		openApi.setExternalDocs(propertyLoader.createExternalDocsFromProperties());
		return openApi;
	}

	default void writeFile(final ParserProperties parserProperties, final OpenAPI openApi) {
		FileWriter writer = new FileWriter(parserProperties);
		writer.writeToFile(openApi);
	}

}
