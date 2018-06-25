package com.github.chhorz.openapi.common;

import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;
import com.github.chhorz.openapi.common.util.FileUtils;

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

	default void writeOpenApiFile(final ParserProperties parserProperties, final OpenAPI openApi) {
		ServiceLoader<PostProcessorProvider> loader = ServiceLoader.load(PostProcessorProvider.class);
		loader.forEach(provider -> {
			OpenAPIPostProcessor postProcessor = provider.create();
			postProcessor.postProcess(parserProperties, openApi);
		});
	}

	default Optional<OpenAPI> readOpenApiFile(final ParserProperties parserProperties) {
		FileUtils fileUtils = new FileUtils(parserProperties);
		return fileUtils.readFromFile();
	}

}
