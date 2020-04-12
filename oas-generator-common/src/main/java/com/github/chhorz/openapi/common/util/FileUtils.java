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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

//import org.yaml.snakeyaml.DumperOptions;
//import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;

public class FileUtils {

	private final LoggingUtils log;

	private final ObjectMapper objectMapper;
	private final ObjectMapper yamlObjectMapper;

	private File outputFile;
	private File yamlOutputFile;
	private File sourceFile;

	public FileUtils(final ParserProperties properties) {
		log = new LoggingUtils(properties);

		if (properties.hasJsonOutputFormat()) {
			this.outputFile = createOutputFile(properties.getOutputDir(), properties.getOutputFile(), ".json");
		}

		if (properties.hasYamlOutputFormat()) {
			this.yamlOutputFile = createOutputFile(properties.getOutputDir(), properties.getOutputFile(), ".yaml");
		}

		if (properties.getSchemaFile() != null) {
			sourceFile = Paths.get(properties.getSchemaFile()).toFile();
		}

		objectMapper = configureObjectMapper(new ObjectMapper());
		yamlObjectMapper = configureObjectMapper(new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)));
	}

	public void writeToFile(final OpenAPI openAPI) {
		if (outputFile != null) {
			try {
				objectMapper.writeValue(outputFile, openAPI);
			} catch (IOException e) {
				log.error("Could not write .json file", e);
			}
		}
		if (yamlOutputFile != null) {
			try {
				yamlObjectMapper.writeValue(yamlOutputFile, openAPI);
			} catch (IOException e) {
				log.error("Could not write .yaml file", e);
			}
		}
	}

	public Optional<OpenAPI> readFromFile() {
		try {
			return Optional.ofNullable(objectMapper.readValue(sourceFile, OpenAPI.class));
		} catch (IOException e) {
			log.error("Could not read schema file", e);
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
