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
package com.github.chhorz.openapi.schema.test;

import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.test.github.GitHubIssue;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.github.chhorz.openapi.common.test.AbstractProcessorTest;
import com.github.chhorz.openapi.schema.SchemaOpenApiProcessor;
import com.github.chhorz.openapi.schema.test.schema.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SchemaOpenApiProcessorTest extends AbstractProcessorTest {

	@Test
	void testGeneratingSchemaFile() {
		// run annotation processor
		testCompilation(new SchemaOpenApiProcessor(), Resource.class);

		// create json-path context
		DocumentContext documentContext = createJsonPathDocumentContext("target/openapi/openapi-schema.json");

		// assertions
		Components components = documentContext.read("$.components", Components.class);

		assertThat(components)
			.isNotNull();

		assertThat(components.getSchemas())
			.isNotNull()
			.hasSize(1)
			.containsKey("Resource")
			.extractingByKey("Resource")
			.hasFieldOrPropertyWithValue("deprecated", false)
			.hasFieldOrPropertyWithValue("type", Schema.Type.OBJECT)
			.hasFieldOrPropertyWithValue("description", "");
	}

	@Test
	@GitHubIssue("#53")
	void testProcessorDisabled() {
		// run annotation processor
		testCompilation(new SchemaOpenApiProcessor(), createConfigFileOption("oas-generator-disabled.yml"), Resource.class);

		// assertions
		assertThat(Paths.get("target/openapi/openapi-schema-missing.json").toFile())
			.isNotNull()
			.doesNotExist();
	}

}
