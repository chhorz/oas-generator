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
package com.github.chhorz.openapi.common;

import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.openapi.common.annotation.OpenAPIExclusion;
import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.Info;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.domain.SecurityScheme;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.javadoc.SecurityTag;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;
import com.github.chhorz.openapi.common.util.FileUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.chhorz.openapi.common.OpenAPIConstants.OPEN_API_VERSION;
import static com.github.chhorz.openapi.common.OpenAPIConstants.X_GENERATED_FIELD;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

/**
 * Interface to provide some common functionality for all OpenAPI annotation processors.
 *
 * @author chhorz
 */
public interface OpenAPIProcessor {

	/**
	 * Returns a set of all annotation processor options that will be recognized by the implementators of this interface.
	 *
	 * @return a set of annotation processor compiler options
	 */
	default Set<String> getOasGeneratorOptions() {
		return Stream.of(
			OpenAPIConstants.OPTION_PROPERTIES_PATH,
			OpenAPIConstants.OPTION_SCHEMA_FILE_PATH,
			OpenAPIConstants.OPTION_VERSION)
			.collect(toSet());
	}

	/**
	 * Initializes a new OpenAPI domain object with the information from the configuration file.
	 *
	 * @param propertyLoader the property loader that loads the properties from the configuration file
	 * @return a new instance of an OpenAPI domain object
	 */
	default OpenAPI initializeFromProperties(final GeneratorPropertyLoader propertyLoader) {
		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi(OPEN_API_VERSION);

		Info info = propertyLoader.createInfoFromProperties();
		info.setxGeneratedBy(X_GENERATED_FIELD);
		info.setxGeneratedTs(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		openApi.setInfo(info);

		openApi.setServers(propertyLoader.createServerFromProperties());
		openApi.setExternalDocs(propertyLoader.createExternalDocsFromProperties());

		Components components = new Components();
		propertyLoader.createSecuritySchemesFromProperties().ifPresent(components::setSecuritySchemes);
		openApi.setComponents(components);

		return openApi;
	}

	/**
	 * Initializes a new instance of the external JavaDocParser. The parser will be configured with additional Javadoc
	 * tags and the Markdown converter.
	 *
	 * @return a new instance of the JavaDocParser
	 */
	default JavaDocParser createJavadocParser() {
		return JavaDocParserBuilder.withBasicTags()
				.withCustomTag(new ResponseTag())
				.withCustomTag(new SecurityTag())
				.withOutputType(OutputType.MARKDOWN)
				.build();
	}

	/**
	 * Creates an OpenAPI operation id from a java method executableElement.
	 *
	 * @param executableElement the executable executableElement that defines a specific method
	 * @return the OpenAPI operation id (should be unique)
	 */
	default String getOperationId(final ExecutableElement executableElement){
		return String.format("%s#%s", executableElement.getEnclosingElement().getSimpleName(), executableElement.getSimpleName());
	}

	/**
	 * Checks if the given method should be excluded from the generated OpenAPI file.
	 *
	 * @param executableElement the executable executableElement that defines a specific method
	 * @return a flag if the given method should be excluded
	 */
	default boolean exclude(final ExecutableElement executableElement) {
		return executableElement.getEnclosingElement().getAnnotation(OpenAPIExclusion.class) != null ||
			executableElement.getAnnotation(OpenAPIExclusion.class) != null;
	}

	/**
	 * Creates a map of security information for a give method.
	 *
	 * @param executableElement the current method
	 * @param map the security schemes from the configuration file
	 * @param securityTags the security tags from the javadoc comment
	 * @return map of security information
	 */
	default List<Map<String, List<String>>> getSecurityInformation(final ExecutableElement executableElement, final Map<String, SecurityScheme> map,
																	   final List<SecurityTag> securityTags) {
		List<Map<String, List<String>>> securityInformation = new ArrayList<>();

		if (map != null) {
			for (AnnotationMirror annotation : executableElement.getAnnotationMirrors()) {
				if (annotation.getAnnotationType().toString().equalsIgnoreCase(
					"org.springframework.security.access.prepost.PreAuthorize")) {
					PreAuthorize preAuthorized = executableElement.getAnnotation(PreAuthorize.class);
					map.entrySet().stream()
						.filter(entry -> preAuthorized.value().toLowerCase().contains(entry.getKey().toLowerCase()))
						.forEach(entry -> securityInformation.add(singletonMap(entry.getKey(), emptyList())));
				}
			}

			if (securityTags != null) {
				securityTags.stream()
					.filter(Objects::nonNull)
					.map(SecurityTag::getSecurityRequirement)
					.filter(map::containsKey)
					.forEach(securityRequirement -> securityInformation.add(singletonMap(securityRequirement, emptyList())));
			}
		}

		return securityInformation;
	}

	/**
	 * Runs all registered post processors from the service loader.
	 *
	 * @param parserProperties the configuration properties form the configuration file
	 * @param openApi the generated OpenAPI domain object
	 */
	default void runPostProcessors(final ParserProperties parserProperties, final OpenAPI openApi) {
		ServiceLoader<PostProcessorProvider> serviceLoader = ServiceLoader.load(PostProcessorProvider.class, getClass().getClassLoader());

		StreamSupport.stream(serviceLoader.spliterator(), false)
				.map(provider -> provider.create(parserProperties))
				.sorted(comparing(OpenAPIPostProcessor::getPostProcessorOrder).reversed())
				.forEach(openAPIPostProcessor -> openAPIPostProcessor.execute(openApi));
	}

	/**
	 * Loads an existing OpenAPI schema file.
	 *
	 * @see ParserProperties#getSchemaFile()
	 *
	 * @param parserProperties the loaded configuration properties
	 * @return an OpenAPI domain object of the configured schema file from the properties
	 */
	default Optional<OpenAPI> readOpenApiFile(final ParserProperties parserProperties) {
		FileUtils fileUtils = new FileUtils(parserProperties);
		return fileUtils.readFromFile();
	}

}
