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
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.ParserProperties;

public class FileUtils {

	private ObjectMapper objectMapper;

	private File outputFile;
	private File sourceFile;

	// private Yaml yaml;

	public FileUtils(final ParserProperties properties) {
		Path outputPath = Paths.get(properties.getOutputDir(), properties.getOutputFile());
		if (properties.getSchemaFile() != null) {
			sourceFile = Paths.get(properties.getSchemaFile()).toFile();
		}

		if (!Files.exists(outputPath)) {
			try {
				Files.createDirectories(outputPath.getParent());
				Files.createFile(outputPath);

				this.outputFile = outputPath.toFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			this.outputFile = outputPath.toFile();
		}

		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		// TODO include
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// objectMapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	public void writeToFile(final OpenAPI openAPI) {
		try {
			objectMapper.writeValue(outputFile, openAPI);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Optional<OpenAPI> readFromFile() {
		try {
			return Optional.ofNullable(objectMapper.readValue(sourceFile, OpenAPI.class));
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Optional.empty();
	}

}
