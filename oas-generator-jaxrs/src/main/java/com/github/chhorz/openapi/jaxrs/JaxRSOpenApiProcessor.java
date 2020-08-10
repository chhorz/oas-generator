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
package com.github.chhorz.openapi.jaxrs;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.tags.CategoryTag;
import com.github.chhorz.javadoc.tags.ParamTag;
import com.github.chhorz.javadoc.tags.ReturnTag;
import com.github.chhorz.openapi.common.OpenAPIProcessor;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.domain.Parameter.In;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.javadoc.SecurityTag;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.ws.rs.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class JaxRSOpenApiProcessor extends AbstractProcessor implements OpenAPIProcessor {

	private Elements elements;

	private GeneratorPropertyLoader propertyLoader;
	private ParserProperties parserProperties;

	private LogUtils logUtils;
	private SchemaUtils schemaUtils;
	private ResponseUtils responseUtils;

	private JavaDocParser javaDocParser;

	private OpenAPI openApi;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		elements = processingEnv.getElementUtils();
		Types types = processingEnv.getTypeUtils();
		Messager messager = processingEnv.getMessager();

		propertyLoader = new GeneratorPropertyLoader(messager, processingEnv.getOptions());
		parserProperties = propertyLoader.getParserProperties();

		logUtils = new LogUtils(messager, parserProperties);
		schemaUtils = new SchemaUtils(elements, types, logUtils);
		responseUtils = new ResponseUtils(elements, types, logUtils);

		javaDocParser = createJavadocParser();

		openApi = initializeFromProperties(propertyLoader);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(Path.class)
			.map(Class::getCanonicalName)
			.collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public Set<String> getSupportedOptions() {
		return getOasGeneratorOptions();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		if (parserProperties.getEnabled()) {
			annotations.stream()
				.flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
				.filter(element -> element instanceof ExecutableElement)
				.map(ExecutableElement.class::cast)
				// .peek(e -> System.out.println(e))
				.forEach(this::mapOperationMethod);

			Map<TypeMirror, Schema> schemaMap = schemaUtils.parsePackages(parserProperties.getSchemaPackages());
			openApi.getComponents().putAllSchemas(schemaMap);

			if (parserProperties.getSchemaFile() != null) {
				readOpenApiFile(logUtils, parserProperties).ifPresent(schemaFile -> openApi.getComponents().putAllParsedSchemas(schemaFile.getComponents().getSchemas()));
			}

			TagUtils tagUtils = new TagUtils(propertyLoader);
			List<String> tags = new ArrayList<>();
			openApi.getPaths().values()
				.stream()
				.map(tagUtils::getAllTags)
				.flatMap(Collection::stream)
				.forEach(tags::add);

			tags.stream()
				.distinct()
				.map(tagUtils::createTag)
				.forEach(openApi::addTag);

			if (roundEnv.processingOver()) {
				runPostProcessors(logUtils, parserProperties, openApi);
			}
		} else {
			logUtils.logError("Execution disabled via properties");
		}
		return false;
	}

	private void mapOperationMethod(final ExecutableElement executableElement) {
		if (exclude(executableElement)) {
			logUtils.logInfo("Skipping method: %s (excluded with @OpenAPIExclusion)", getOperationId(executableElement));
			return;
		} else {
			logUtils.logDebug("Parsing method: %s", getOperationId(executableElement));
		}

		JavaDoc javaDoc = javaDocParser.parse(elements.getDocComment(executableElement));

		Path classPath = executableElement.getEnclosingElement().getAnnotation(Path.class);
		Path methodPath = executableElement.getAnnotation(Path.class);

		StringBuilder sb = new StringBuilder();
		if (classPath != null) {
			sb.append(classPath.value());
		}
		if (methodPath != null) {
			sb.append(methodPath.value());
		}
		String path = sb.toString();

		Operation operation = new Operation();
		operation.setSummary(javaDoc.getSummary());
		operation.setDescription(javaDoc.getDescription());
		operation.setOperationId(getOperationId(executableElement));
		operation.setDeprecated(executableElement.getAnnotation(Deprecated.class) != null);

		List<ParamTag> tags = javaDoc.getTags(ParamTag.class);

		operation.addParameterObjects(executableElement.getParameters()
				.stream()
				.filter(variableElement -> variableElement.getAnnotation(PathParam.class) != null)
				.map(v -> mapPathVariable(path, v, tags))
				.collect(toList()));

		operation.addParameterObjects(executableElement.getParameters()
				.stream()
				.filter(variableElement -> variableElement.getAnnotation(QueryParam.class) != null)
				.map(v -> mapRequestParam(v, tags))
				.collect(toList()));

		operation.addParameterObjects(executableElement.getParameters()
				.stream()
				.filter(variableElement -> variableElement.getAnnotation(HeaderParam.class) != null)
				.map(v -> mapRequestHeader(v, tags))
				.collect(toList()));

		VariableElement requestBody = executableElement.getParameters()
				.stream()
				.filter(variableElement -> variableElement.getAnnotation(PathParam.class) == null)
				.filter(variableElement -> variableElement.getAnnotation(QueryParam.class) == null)
				.filter(variableElement -> variableElement.getAnnotation(HeaderParam.class) == null)
				.findFirst()
				.orElse(null);

		if (requestBody != null) {
			RequestBody r = new RequestBody();

			javaDoc.getTags(ParamTag.class)
				.stream()
				.filter(tag -> requestBody.toString().equals(tag.getParamName()))
				.findFirst()
				.ifPresent(parameter -> r.setDescription(parameter.getParamDescription()));

			r.setRequired(Boolean.TRUE);

			MediaType mediaType = schemaUtils.createMediaType(requestBody.asType());
			Consumes consumes = executableElement.getAnnotation(Consumes.class);
			if (consumes != null) {
				for (String c : consumes.value()) {
					r.putContent(c, mediaType);
				}
			} else {
				r.putContent("*/*", mediaType);
			}

			openApi.getComponents().putAllSchemas(schemaUtils.mapTypeMirrorToSchema(requestBody.asType()));
			openApi.getComponents().putRequestBody(requestBody.asType(), r);

			operation.setRequestBodyReference(ReferenceUtils.createRequestBodyReference(requestBody.asType()));
		}

		String returnTag = "";
		List<ReturnTag> returnTags = javaDoc.getTags(ReturnTag.class);
		if (returnTags.size() == 1) {
			returnTag = returnTags.get(0).getDesrcription();
		}

		TypeMirror returnType = executableElement.getReturnType();
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(returnType);
		Schema schema = schemaMap.get(returnType);

		Produces produces = executableElement.getAnnotation(Produces.class);
		if (produces != null) {
			if (Type.OBJECT.equals(schema.getType()) || Type.ENUM.equals(schema.getType())) {
				Response response = responseUtils.fromTypeMirror(returnType, produces.value(), returnTag);
				response.setDescription(returnTag);
				operation.putDefaultResponse(response);
			} else {
				Response response = responseUtils.fromSchema(schema, produces.value(), returnTag);
				response.setDescription(returnTag);
				operation.putDefaultResponse(response);
				schemaMap.remove(returnType);
			}
		}

		openApi.getComponents().putAllSchemas(schemaMap);

		javaDoc.getTags(CategoryTag.class).stream()
				.map(CategoryTag::getCategoryName)
				.forEach(operation::addTag);

		operation.setSecurity(getSecurityInformation(executableElement, openApi.getComponents().getSecuritySchemes(), javaDoc.getTags(SecurityTag.class)));

		PathItemObject pathItemObject = openApi.getPaths().getOrDefault(path, new PathItemObject());
		if (executableElement.getAnnotation(GET.class) != null) {
			pathItemObject.setGet(operation);
		} else if (executableElement.getAnnotation(DELETE.class) != null) {
			pathItemObject.setDelete(operation);
		} else if (executableElement.getAnnotation(HEAD.class) != null) {
			pathItemObject.setHead(operation);
		} else if (executableElement.getAnnotation(OPTIONS.class) != null) {
			pathItemObject.setOptions(operation);
		} else if (executableElement.getAnnotation(PATCH.class) != null) {
			pathItemObject.setPatch(operation);
		} else if (executableElement.getAnnotation(POST.class) != null) {
			pathItemObject.setPost(operation);
		} else if (executableElement.getAnnotation(PUT.class) != null) {
			pathItemObject.setPut(operation);
		} else {
			throw new RuntimeException("Unknown RequestMethod value.");
		}
		openApi.putPathItemObject(path, pathItemObject);
	}

	private Parameter mapPathVariable(final String path, final VariableElement variableElement,
			final List<ParamTag> parameterDocs) {
		PathParam pathParam = variableElement.getAnnotation(PathParam.class);

		Optional<ParamTag> parameterDescription = parameterDocs.stream()
				.filter(tag -> tag.getParamName().equalsIgnoreCase(pathParam.value()))
				.findFirst();

		Parameter parameter = new Parameter();
		parameter.setAllowEmptyValue(Boolean.FALSE);
		parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
		parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
		parameter.setIn(In.PATH);
		parameter.setName(pathParam.value());
		parameter.setRequired(Boolean.TRUE);

		Map<TypeMirror, Schema> map = schemaUtils.mapTypeMirrorToSchema(variableElement.asType());
		Schema schema = map.get(variableElement.asType());

		parameter.setSchema(schema);

		return parameter;
	}

	private Parameter mapRequestParam(final VariableElement variableElement, final List<ParamTag> parameterDocs) {
		QueryParam queryParam = variableElement.getAnnotation(QueryParam.class);

		Optional<ParamTag> parameterDescription = parameterDocs.stream()
				.filter(tag -> tag.getParamName().equalsIgnoreCase(queryParam.value()))
				.findFirst();

		Parameter parameter = new Parameter();
//		parameter.setAllowEmptyValue();
		parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
		parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
		parameter.setIn(In.QUERY);
		parameter.setName(queryParam.value());
//		parameter.setRequired(variableElement.getAnnotation(DefaultValue.class) == null);

		Schema schema = schemaUtils.mapTypeMirrorToSchema(variableElement.asType())
				.get(variableElement.asType());

		if (variableElement.getAnnotation(DefaultValue.class) != null) {
			DefaultValue defaultValue = variableElement.getAnnotation(DefaultValue.class);
			schema.setDefaultValue(defaultValue.value());
		}
		parameter.setSchema(schema);

		return parameter;
	}

	private Parameter mapRequestHeader(final VariableElement variableElement, final List<ParamTag> parameterDocs) {
		HeaderParam headerParam = variableElement.getAnnotation(HeaderParam.class);

		Optional<ParamTag> parameterDescription = parameterDocs.stream()
				.filter(tag -> tag.getParamName().equalsIgnoreCase(headerParam.value()))
				.findFirst();

		// TODO handle MultiValueMap

		Parameter parameter = new Parameter();
//		parameter.setAllowEmptyValue();
		parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
		parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
		parameter.setIn(In.HEADER);
		parameter.setName(headerParam.value());
//		parameter.setRequired(variableElement.getAnnotation(DefaultValue.class) == null);

		Schema schema = schemaUtils.mapTypeMirrorToSchema(variableElement.asType())
				.get(variableElement.asType());
		if (variableElement.getAnnotation(DefaultValue.class) != null) {
			DefaultValue defaultValue = variableElement.getAnnotation(DefaultValue.class);
			schema.setDefaultValue(defaultValue.value());
		}
		parameter.setSchema(schema);

		return parameter;
	}
}
