package com.github.chhorz.openapi.common.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

//import org.yaml.snakeyaml.DumperOptions;
//import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.chhorz.openapi.common.domain.OpenAPI;

public class FileWriter {

	private File file;
	private ObjectMapper objectMapper;
	// private Yaml yaml;

	public FileWriter(final String fileName) {
		File file = new File(fileName);
		try {
			file.createNewFile();
			this.file = file;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		// TODO include
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// objectMapper.setSerializationInclusion(Include.NON_EMPTY);

		// DumperOptions options = new DumperOptions();
		// options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		// options.setPrettyFlow(true);
		//
		// yaml = new Yaml(options);

	}

	public void writeToFile(final OpenAPI openAPI) {
		try {
			objectMapper.writeValue(file, openAPI);
			// String yamlString = yaml.dump(openAPI);
			//
			// Files.write(file.toPath(), yamlString.getBytes());

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
