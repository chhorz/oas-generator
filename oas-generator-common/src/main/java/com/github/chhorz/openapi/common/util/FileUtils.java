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
package com.github.chhorz.openapi.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.spi.FileWriterProvider;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;
import com.github.chhorz.openapi.common.spi.PostProcessorType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import static com.github.chhorz.openapi.common.spi.PostProcessorType.*;
import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;

public class FileUtils {

	private final ParserProperties properties;

	private final LoggingUtils log;

	private final ObjectMapper objectMapper;
	private final ObjectMapper yamlObjectMapper;

	private final List<OpenAPIPostProcessor> openAPIPostProcessors;

	public FileUtils(final ParserProperties properties) {
		this.properties = properties;

		log = new LoggingUtils(properties);

		objectMapper = configureObjectMapper(new ObjectMapper());
		yamlObjectMapper = configureObjectMapper(new ObjectMapper(new YAMLFactory()
				.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
				.disable(YAMLGenerator.Feature.SPLIT_LINES)));

		// instantiate post processors for string and file types
		ServiceLoader<PostProcessorProvider> serviceLoader = load(PostProcessorProvider.class, getClass().getClassLoader());

		openAPIPostProcessors = StreamSupport.stream(serviceLoader.spliterator(), false)
			.filter(provider -> !(provider instanceof FileWriterProvider))
			.map(provider -> provider.create(properties))
			.filter(postProcessor -> !postProcessor.getPostProcessorType().contains(PostProcessorType.DOMAIN_OBJECT))
			.collect(toList());
	}

	/**
	 * Writes the given OpenAPI object to <i>.json</i> and <i>.yaml</i> files. Which files are written is configurable
	 * within the property file.
	 *
	 * @param openAPI the generated OpenAPI object
	 */
	public void writeToFile(final OpenAPI openAPI) {
		if (properties.hasJsonOutputFormat()) {
			Path outputFile = createOutputFile(properties.getOutputDir(), properties.getOutputFile(), ".json");
			if (outputFile != null) {
				try {
					String openApiJsonString = objectMapper.writeValueAsString(openAPI);
					openAPIPostProcessors.stream()
						.filter(postProcessor -> postProcessor.getPostProcessorType().contains(JSON_STRING))
						.forEach(postProcessor -> postProcessor.execute(openApiJsonString, JSON_STRING));
					Files.write(outputFile, openApiJsonString.getBytes());
				} catch (IOException e) {
					log.error("Could not write .json file", e);
				}
				openAPIPostProcessors.stream()
					.filter(postProcessor -> postProcessor.getPostProcessorType().contains(JSON_FILE))
					.forEach(postProcessor -> postProcessor.execute(outputFile, JSON_FILE));
			} else {
				log.error("JSON output file is null.");
			}
		} else {
			log.debug("JSON output is disabled in property file");
		}
		if (properties.hasYamlOutputFormat()) {
			Path yamlOutputFile = createOutputFile(properties.getOutputDir(), properties.getOutputFile(), ".yaml");
			if (yamlOutputFile != null) {
				try {
					String openApiYamlString = yamlObjectMapper.writeValueAsString(openAPI);
					openAPIPostProcessors.stream()
						.filter(postProcessor -> postProcessor.getPostProcessorType().contains(YAML_STRING))
						.forEach(postProcessor -> postProcessor.execute(openApiYamlString, YAML_STRING));
					Files.write(yamlOutputFile, openApiYamlString.getBytes());
				} catch (IOException e) {
					log.error("Could not write .yaml file", e);
				}
				openAPIPostProcessors.stream()
					.filter(postProcessor -> postProcessor.getPostProcessorType().contains(YAML_FILE))
					.forEach(postProcessor -> postProcessor.execute(yamlOutputFile, YAML_FILE));
			} else {
				log.error("YAML output file is null.");
			}
		} else {
			log.debug("YAML output is disabled in property file");
		}
	}

	public Optional<OpenAPI> readFromFile() {
		if (properties.getSchemaFile() != null) {
			File sourceFile = Paths.get(properties.getSchemaDir(), properties.getSchemaFile() + ".json").toFile();
			if (sourceFile.exists()) {
				try {
					return Optional.ofNullable(objectMapper.readValue(sourceFile, OpenAPI.class));
				} catch (IOException e) {
					log.error("Could not read schema file", e);
				}
			}
		}
		return Optional.empty();
	}

	private Path createOutputFile(String outputDir, String outputFile, String fileFormat){
		Path outputPath = Paths.get(outputDir, outputFile + fileFormat);
		if (!Files.exists(outputPath)) {
			try {
				Files.createDirectories(outputPath.getParent());
				Files.createFile(outputPath);

				return outputPath;
			} catch (IOException e) {
				log.error("Could not create file " + outputPath, e);
				return null;
			}
		} else {
			return outputPath;
		}
	}

	private ObjectMapper configureObjectMapper(ObjectMapper objectMapper){
		objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		// TODO configure include
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		return objectMapper;
	}

}
