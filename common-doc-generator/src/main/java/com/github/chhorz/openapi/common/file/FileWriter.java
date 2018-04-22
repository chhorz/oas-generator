package com.github.chhorz.openapi.common.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import org.yaml.snakeyaml.DumperOptions;
//import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.chhorz.openapi.common.domain.OpenAPI;

public class FileWriter {

	private File file;
	private ObjectMapper objectMapper;
	// private Yaml yaml;

	public FileWriter(final String directory) {
		Path path = Paths.get(directory, "openapi.json");

		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);

				this.file = path.toFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			this.file = path.toFile();
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
			objectMapper.writeValue(file, openAPI);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
