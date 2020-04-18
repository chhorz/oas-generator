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
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileUtils {

	private final ParserProperties properties;

	private final LoggingUtils log;

	private final ObjectMapper objectMapper;
	private final ObjectMapper yamlObjectMapper;

	public FileUtils(final ParserProperties properties) {
		this.properties = properties;

		log = new LoggingUtils(properties);

		objectMapper = configureObjectMapper(new ObjectMapper());
		yamlObjectMapper = configureObjectMapper(new ObjectMapper(new YAMLFactory()
				.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
				.disable(YAMLGenerator.Feature.SPLIT_LINES)));
	}

	/**
	 * Writes the given OpenAPI object to <i>.json</i> and <i>.yaml</i> files. Which files are written is configurable
	 * within the property file.
	 *
	 * @param openAPI the generated OpenAPI object
	 */
	public void writeToFile(final OpenAPI openAPI) {
		if (properties.hasJsonOutputFormat()) {
			File outputFile = createOutputFile(properties.getOutputDir(), properties.getOutputFile(), ".json");
			try {
				objectMapper.writeValue(outputFile, openAPI);
			} catch (IOException e) {
				log.error("Could not write .json file", e);
			}
		} else {
			log.debug("JSON output is disabled in property file");
		}
		if (properties.hasYamlOutputFormat()) {
			File yamlOutputFile = createOutputFile(properties.getOutputDir(), properties.getOutputFile(), ".yaml");
			try {
				yamlObjectMapper.writeValue(yamlOutputFile, openAPI);
			} catch (IOException e) {
				log.error("Could not write .yaml file", e);
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

	private File createOutputFile(String outputDir, String outputFile, String fileFormat){
		Path outputPath = Paths.get(outputDir, outputFile + fileFormat);
		if (!Files.exists(outputPath)) {
			try {
				Files.createDirectories(outputPath.getParent());
				Files.createFile(outputPath);

				return outputPath.toFile();
			} catch (IOException e) {
				log.error("Could not create file " + outputPath, e);
				return null;
			}
		} else {
			return outputPath.toFile();
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
