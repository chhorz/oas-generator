package com.github.chhorz.openapi.schema.test.github.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.chhorz.openapi.common.annotation.OpenAPISchema;

@OpenAPISchema
public class JsonPropertyTest {

	@JsonProperty
	private String jsonProperty;

}
