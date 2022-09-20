/**
 * Copyright 2018-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chhorz.openapi.common;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.javadoc.tags.CategoryTag;
import com.github.chhorz.openapi.common.annotation.OpenAPIExclusion;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.javadoc.SecurityTag;
import com.github.chhorz.openapi.common.javadoc.TagTag;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.spi.OpenAPIPostProcessor;
import com.github.chhorz.openapi.common.spi.PostProcessorProvider;
import com.github.chhorz.openapi.common.spi.PostProcessorType;
import com.github.chhorz.openapi.common.util.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.chhorz.openapi.common.OpenAPIConstants.OPEN_API_VERSION;
import static com.github.chhorz.openapi.common.OpenAPIConstants.X_GENERATED_FIELD;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;
import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toSet;

/**
 * Abstract annotation processor to provide some common functionality for all specific OpenAPI annotation processors.
 *
 * @author chhorz
 */
public abstract class OpenAPIProcessor extends AbstractProcessor {

	private final AtomicInteger operationIdCounter = new AtomicInteger(0);

	protected Elements elements;

	protected GeneratorPropertyLoader propertyLoader;
	protected ParserProperties parserProperties;

	protected LogUtils logUtils;
	protected SchemaUtils schemaUtils;
	protected ProcessingUtils processingUtils;
	protected ResponseUtils responseUtils;

	protected JavaDocParser javaDocParser;

	protected OpenAPI openApi;

	/**
	 *
	 *
	 * @see AbstractProcessor#init(ProcessingEnvironment)
	 */
	protected void init(ProcessingEnvironment processingEnv, final List<Class<?>> baseClasses) {
		elements = processingEnv.getElementUtils();
		Types types = processingEnv.getTypeUtils();
		Messager messager = processingEnv.getMessager();

		// initialize property loader
		propertyLoader = new GeneratorPropertyLoader(messager, processingEnv.getOptions());
		parserProperties = propertyLoader.getParserProperties();

		logUtils = new LogUtils(messager, parserProperties);
		schemaUtils = new SchemaUtils(elements, types, parserProperties, logUtils, baseClasses);
		processingUtils = new ProcessingUtils(elements, types, logUtils);
		responseUtils = new ResponseUtils(elements, types, parserProperties, logUtils);

		javaDocParser = createJavadocParser();

		openApi = initializeFromProperties(propertyLoader);
	}

	/**
	 * Create a stream of all supported annotation classes
	 *
	 * @return stream of class elements.
	 */
	protected abstract Stream<Class<? extends Annotation>> getSupportedAnnotationClasses();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return getSupportedAnnotationClasses()
			.map(Class::getCanonicalName)
			.collect(toSet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	/**
	 * Returns a set of all annotation processor options that will be recognized by the implementations of this interface.
	 *
	 * @return a set of annotation processor compiler options
	 */
	@Override
	public Set<String> getSupportedOptions() {
		return Stream.of(
				OpenAPIConstants.OPTION_PROPERTIES_PATH,
				OpenAPIConstants.OPTION_SCHEMA_FILE_PATH,
				OpenAPIConstants.OPTION_VERSION)
			.collect(toSet());
	}

	/**
	 * Initializes a new OpenAPI domain object with the information from the configuration file.
	 *
	 * @param propertyLoader the property loader that loads the properties from the configuration file
	 * @return a new instance of an OpenAPI domain object
	 */
	protected OpenAPI initializeFromProperties(final GeneratorPropertyLoader propertyLoader) {
		OpenAPI openApi = new OpenAPI();
		openApi.setOpenapi(OPEN_API_VERSION);

		Info info = propertyLoader.createInfoFromProperties();
		info.setxGeneratedBy(X_GENERATED_FIELD);
		info.setxGeneratedTs(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		openApi.setInfo(info);

		openApi.setServers(propertyLoader.createServerFromProperties());
		openApi.setExternalDocs(propertyLoader.createExternalDocsFromProperties());

		Components components = new Components();
		propertyLoader.createSecuritySchemesFromProperties().ifPresent(components::setSecuritySchemes);
		openApi.setComponents(components);

		return openApi;
	}

	/**
	 * Initializes a new instance of the external JavaDocParser. The parser will be configured with additional Javadoc
	 * tags and the Markdown converter.
	 *
	 * @return a new instance of the JavaDocParser
	 */
	protected JavaDocParser createJavadocParser() {
		return JavaDocParserBuilder.withAllKnownTags()
			.withTag(new ResponseTag())
			.withTag(new SecurityTag())
			.withTag(new TagTag())
			.withOutputType(OutputType.MARKDOWN)
			.build();
	}

	/**
	 * Creates an OpenAPI operation id from a java method executableElement.
	 *
	 * @param executableElement the executable executableElement that defines a specific method
	 * @return the OpenAPI operation id (should be unique)
	 */
	protected String getOperationId(final ExecutableElement executableElement) {
		return getOperationId(executableElement, null);
	}

	protected String getOperationId(final ExecutableElement executableElement, final OpenAPI openAPI) {
		String operationId = String.format("%s#%s", executableElement.getEnclosingElement().getSimpleName(), executableElement.getSimpleName());
		if (openAPI == null || openAPI.getPaths().values().stream()
			.noneMatch(pathItem -> operationIdPresent(pathItem.getGet(), operationId)
								   || operationIdPresent(pathItem.getPut(), operationId)
								   || operationIdPresent(pathItem.getPost(), operationId)
								   || operationIdPresent(pathItem.getDelete(), operationId)
								   || operationIdPresent(pathItem.getOptions(), operationId)
								   || operationIdPresent(pathItem.getHead(), operationId)
								   || operationIdPresent(pathItem.getPatch(), operationId)
								   || operationIdPresent(pathItem.getTrace(), operationId))) {
			return operationId;
		} else {
			return String.format("%s_%04d", operationId, operationIdCounter.incrementAndGet());
		}
	}

	private boolean operationIdPresent(Operation operation, String operationId) {
		return operation != null && operationId.equals(operation.getOperationId());
	}

	/**
	 * Checks if the given method should be excluded from the generated OpenAPI file.
	 *
	 * @param executableElement the executable executableElement that defines a specific method
	 * @return a flag if the given method should be excluded
	 */
	protected boolean exclude(final ExecutableElement executableElement) {
		return executableElement.getEnclosingElement().getAnnotation(OpenAPIExclusion.class) != null ||
			   executableElement.getAnnotation(OpenAPIExclusion.class) != null;
	}

	/**
	 * Creates a list of tags from the given input sources.
	 *
	 * @param javaDoc the Javadoc from the executable element
	 * @param openApiAnnotation the {@link com.github.chhorz.openapi.common.annotation.OpenAPI} annotation from the executable element
	 * @return a list of tags
	 */
	protected List<String> getTags(final JavaDoc javaDoc, final com.github.chhorz.openapi.common.annotation.OpenAPI openApiAnnotation) {
		List<String> tags = new ArrayList<>();
		javaDoc.getTags(CategoryTag.class).stream()
			.map(CategoryTag::getCategoryName)
			.forEach(tags::add);
		javaDoc.getTags(TagTag.class).stream()
			.map(TagTag::getTagName)
			.forEach(tags::add);
		if (openApiAnnotation != null) {
			tags.addAll(asList(openApiAnnotation.tags()));
		}
		Collections.sort(tags);
		return tags;
	}

	/**
	 * Creates a map of security information for a give method.
	 *
	 * @param executableElement the current method
	 * @param openAPI the openapi object with the current parsed data
	 * @param javaDoc the javadoc comment from the current method
	 * @param openApiAnnotation the annotation from the current method
	 * @return map of security information
	 */
	protected List<Map<String, List<String>>> getSecurityInformation(final ExecutableElement executableElement,
		final OpenAPI openAPI, final JavaDoc javaDoc, final com.github.chhorz.openapi.common.annotation.OpenAPI openApiAnnotation) {
		List<Map<String, List<String>>> securityInformation = new ArrayList<>();

		Objects.requireNonNull(openAPI);
		Objects.requireNonNull(openAPI.getComponents());
		Map<String, SecurityScheme> securitySchemes = openAPI.getComponents().getSecuritySchemes();

		if (securitySchemes != null) {
			for (AnnotationMirror annotation : executableElement.getAnnotationMirrors()) {
				if (annotation.getAnnotationType().toString().equalsIgnoreCase(
					"org.springframework.security.access.prepost.PreAuthorize")) {
					PreAuthorize preAuthorized = executableElement.getAnnotation(PreAuthorize.class);
					securitySchemes.entrySet().stream()
						.filter(entry -> preAuthorized.value().toLowerCase().contains(entry.getKey().toLowerCase()))
						.filter(entry -> securityInformation.stream()
							.map(Map::keySet)
							.flatMap(Set::stream)
							.noneMatch(set -> set.contains(entry.getKey())))
						.forEach(entry -> securityInformation.add(singletonMap(entry.getKey(), emptyList())));
				}
			}

			if (javaDoc.getTags(SecurityTag.class) != null) {
				javaDoc.getTags(SecurityTag.class)
					.stream()
					.filter(Objects::nonNull)
					.map(SecurityTag::getSecurityRequirement)
					.filter(securitySchemes::containsKey)
					.filter(securityRequirement -> securityInformation.stream()
						.map(Map::keySet)
						.flatMap(Set::stream)
						.noneMatch(set -> set.contains(securityRequirement)))
					.forEach(securityRequirement -> securityInformation.add(singletonMap(securityRequirement, emptyList())));
			}

			if (openApiAnnotation != null) {
				Stream.of(openApiAnnotation.securitySchemes())
					.filter(Objects::nonNull)
					.filter(securitySchemes::containsKey)
					.filter(securityRequirement -> securityInformation.stream()
						.map(Map::keySet)
						.flatMap(Set::stream)
						.noneMatch(set -> set.contains(securityRequirement)))
					.forEach(securityRequirement -> securityInformation.add(singletonMap(securityRequirement, emptyList())));
			}
		}

		return securityInformation;
	}

	/**
	 * Runs all registered post processors from the service loader.
	 *
	 * @param parserProperties the configuration properties form the configuration file
	 * @param openApi the generated OpenAPI domain object
	 */
	protected void runPostProcessors(final ParserProperties parserProperties, final OpenAPI openApi) {
		ServiceLoader<PostProcessorProvider> serviceLoader = load(PostProcessorProvider.class, getClass().getClassLoader());

		StreamSupport.stream(serviceLoader.spliterator(), false)
			.map(provider -> provider.create(logUtils, parserProperties))
			.filter(postProcessor -> postProcessor.getPostProcessorType().contains(PostProcessorType.DOMAIN_OBJECT))
			.sorted(comparing(OpenAPIPostProcessor::getPostProcessorOrder).reversed())
			.forEach(openAPIPostProcessor -> openAPIPostProcessor.execute(openApi));
	}

	/**
	 * Loads an existing OpenAPI schema file.
	 *
	 * @see ParserProperties#getSchemaFile()
	 *
	 * @param parserProperties the loaded configuration properties
	 * @return an OpenAPI domain object of the configured schema file from the properties
	 */
	protected Optional<OpenAPI> readOpenApiFile(final ParserProperties parserProperties) {
		FileUtils fileUtils = new FileUtils(logUtils, parserProperties);
		return fileUtils.readOpenAPIObjectFromFile();
	}

	/**
	 * Check if the given class is available on the classpath. This check is required for optional maven dependencies.
	 *
	 * @param className the full qualified class name
	 * @return {@code true} if the class is available on the classpath
	 */
	public static boolean isClassAvailable(final String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
