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
package com.github.chhorz.openapi.schema;

import com.github.chhorz.openapi.common.OpenAPIConstants;
import com.github.chhorz.openapi.common.OpenAPIProcessor;
import com.github.chhorz.openapi.common.annotation.OpenAPISchema;
import com.github.chhorz.openapi.common.domain.Components;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.LogUtils;
import com.github.chhorz.openapi.common.util.SchemaUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class SchemaOpenApiProcessor extends AbstractProcessor implements OpenAPIProcessor {

	private Elements elements;
	private Types types;

	private ParserProperties parserProperties;

	private LogUtils logUtils;

	private OpenAPI openApi;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		elements = processingEnv.getElementUtils();
		types = processingEnv.getTypeUtils();
		Messager messager = processingEnv.getMessager();

		// initialize property loader
		GeneratorPropertyLoader propertyLoader = new GeneratorPropertyLoader(messager, processingEnv.getOptions());
		parserProperties = propertyLoader.getParserProperties();

		logUtils = new LogUtils(messager, parserProperties);

		openApi = new OpenAPI();
		openApi.setComponents(new Components());
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(OpenAPISchema.class)
			.map(Class::getCanonicalName)
			.collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public Set<String> getOasGeneratorOptions() {
		return Stream.of(
			OpenAPIConstants.OPTION_PROPERTIES_PATH)
			.collect(toSet());
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		if (parserProperties.getEnabled()) {
			SchemaUtils schemaUtils = new SchemaUtils(elements, types, parserProperties, logUtils);
			for (TypeElement annotation : annotations) {
				roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
					openApi.getComponents().putAllSchemas(schemaUtils.createStringSchemaMap(element.asType()));
				});
			}

			openApi.getComponents().putAllSchemas(schemaUtils.parsePackages(parserProperties.getSchemaPackages()));

			if (roundEnv.processingOver()) {
				runPostProcessors(logUtils, parserProperties, openApi);
			}
		} else {
			logUtils.logError("Execution disabled via properties");
		}
		return false;
	}

}
