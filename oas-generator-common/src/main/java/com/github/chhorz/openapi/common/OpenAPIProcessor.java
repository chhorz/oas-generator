package com.github.chhorz.openapi.common;

import static com.github.chhorz.openapi.common.OpenAPIConstants.OPEN_API_VERSION;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.domain.SecurityScheme;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;
import com.github.chhorz.openapi.common.util.FileUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;

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
		openApi.setOpenapi(OPEN_API_VERSION);
		openApi.setInfo(propertyLoader.createInfoFromProperties());
		openApi.setServers(propertyLoader.createServerFromProperties());
		openApi.setExternalDocs(propertyLoader.createExternalDocsFromProperties());

		Components components = new Components();
		components.setSecuritySchemes(propertyLoader.createSecuritySchemesFromProperties());
		openApi.setComponents(components);

		return openApi;
	}

	default JavaDocParser createJavadocParser() {
		return JavaDocParserBuilder.withBasicTags()
				.withCustomTag(new ResponseTag())
				.withOutputType(OutputType.MARKDOWN)
				.build();
	}

	default Map<String, List<String>> getSecurityInformation(final ExecutableElement executableElement, final Map<String, SecurityScheme> map) {
		Map<String, List<String>> securityInformation = new TreeMap<>();

		for (AnnotationMirror annotation : executableElement.getAnnotationMirrors()) {
			if (annotation.getAnnotationType().toString().equalsIgnoreCase(
					"org.springframework.security.access.prepost.PreAuthorize")) {
				PreAuthorize preAuthorized = executableElement.getAnnotation(PreAuthorize.class);
				map.entrySet().stream()
						.filter(entry -> preAuthorized.value().toLowerCase().contains(entry.getKey().toLowerCase()))
						.forEach(entry -> securityInformation.put(entry.getKey(), new ArrayList<>()));
			}
		}

		return securityInformation;
	}

	default void runPostProcessors(final ParserProperties parserProperties, final OpenAPI openApi) {
		ServiceLoader<PostProcessorProvider> serviceLoader = ServiceLoader.load(PostProcessorProvider.class, getClass().getClassLoader());

		StreamSupport.stream(serviceLoader.spliterator(), false)
				.map(provider -> provider.create(parserProperties))
				.sorted(Comparator.comparing(OpenAPIPostProcessor::getPostProcessorOrder))
				.forEach(openAPIPostProcessor -> openAPIPostProcessor.execute(openApi));
	}

	default Optional<OpenAPI> readOpenApiFile(final ParserProperties parserProperties) {
		FileUtils fileUtils = new FileUtils(parserProperties);
		return fileUtils.readFromFile();
	}

}
