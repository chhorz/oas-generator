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
package com.github.chhorz.openapi.common.test;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.ParseContextImpl;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractProcessorTest {

	private static JavaCompiler javaCompiler;

	private StandardJavaFileManager fileManager;
	private DiagnosticCollector<JavaFileObject> collector;

	@BeforeAll
	static void initialize() {
		javaCompiler = ToolProvider.getSystemJavaCompiler();
	}

	@BeforeEach
	void initTest() throws Exception {
		// configure the diagnostics collector.
		collector = new DiagnosticCollector<>();
		fileManager = javaCompiler.getStandardFileManager(collector, Locale.US, Charset.forName("UTF-8"));
	}

	public void testCompilation(final Processor processor, final Class<?>... classes) {
		testCompilation(processor, emptyMap(),
			Stream.of(classes)
				.map(Class::getCanonicalName)
				.map(clazz -> clazz.replaceAll("\\.", "/"))
				.map(clazz -> String.format("src/test/java/%s.java", clazz))
				.toArray(String[]::new));
	}

	public void testCompilation(final Processor processor, final Map<String, String> options, final Class<?>... classes) {
		testCompilation(processor, options,
				Stream.of(classes)
						.map(Class::getCanonicalName)
						.map(clazz -> clazz.replaceAll("\\.", "/"))
						.map(clazz -> String.format("src/test/java/%s.java", clazz))
						.toArray(String[]::new));
	}

	private void testCompilation(final Processor processor, final Map<String, String> options, final String... files) {
		try {
			List<String> optionsList = options.entrySet().stream()
					.map(entry -> String.format("-A%s=%s", entry.getKey(), entry.getValue()))
					.collect(Collectors.toList());

			// streams.
			ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
			OutputStreamWriter stdout = new OutputStreamWriter(stdoutStream);

			JavaCompiler.CompilationTask task = javaCompiler.getTask(stdout, fileManager, collector, optionsList, null,
					fileManager.getJavaFileObjects(files));
			task.setProcessors(Collections.singletonList(processor));
			Boolean result = task.call();

			String stdoutS = new String(stdoutStream.toByteArray());

			// perform the verifications.
			for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
				if (diagnostic.getKind() == Kind.MANDATORY_WARNING || diagnostic.getKind() == Kind.ERROR) {
					fail("Failed with message: " + diagnostic.getMessage(null));
				}
			}

			assertThat(result).isTrue().as("Files should have no compilation errors");
		} finally {
			// no-op
		}
	}

	/**
	 * Creates a <i>json-path</i> context for the openapi file at the default output path.
	 *
	 * @return the json content
	 */
	public DocumentContext createJsonPathDocumentContext() {
		return createJsonPathDocumentContext("target/openapi/openapi.json");
	}

	/**
	 * Creates a <i>json-path</i> context for the openapi file at the given path.
	 *
	 * @param filePath the file path of the json file
	 *
	 * @return the json content
	 */
	public DocumentContext createJsonPathDocumentContext(String filePath) {
		try {
			return JsonPath.parse(Paths.get(filePath).toFile(), Configuration.builder().mappingProvider(new JacksonMappingProvider()).build());
		} catch (IOException e) {
			fail("Could not read openapi file.", e);
		}
		return new ParseContextImpl().parse("{}");
	}

}
