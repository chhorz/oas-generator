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
package com.github.chhorz.openapi.spring;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.tags.ParamTag;
import com.github.chhorz.javadoc.tags.ReturnTag;
import com.github.chhorz.openapi.common.OpenAPIProcessor;
import com.github.chhorz.openapi.common.annotation.OpenAPISchema;
import com.github.chhorz.openapi.common.domain.*;
import com.github.chhorz.openapi.common.util.ComponentUtils;
import com.github.chhorz.openapi.common.util.ProcessingUtils;
import com.github.chhorz.openapi.common.util.TagUtils;
import com.github.chhorz.openapi.spring.util.AliasUtils;
import com.github.chhorz.openapi.spring.util.ParameterUtils;
import com.github.chhorz.openapi.spring.util.PathItemUtils;
import com.github.chhorz.openapi.spring.util.RequestBodyUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.chhorz.openapi.common.util.ComponentUtils.convertSchemaMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class SpringWebOpenApiProcessor extends OpenAPIProcessor {

    private AliasUtils aliasUtils;
    private ParameterUtils parameterUtils;

    private TypeMirror exceptionHanderReturntype = null;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
    	init(processingEnv, singletonList(ResponseEntity.class));

		aliasUtils = new AliasUtils();
		parameterUtils = new ParameterUtils(schemaUtils, processingUtils, aliasUtils);

        openApi = initializeFromProperties(propertyLoader);
    }

    @Override
    public Stream<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return Stream.of(RequestMapping.class,
				GetMapping.class,
				PostMapping.class,
				PutMapping.class,
				DeleteMapping.class,
				PatchMapping.class,
				ExceptionHandler.class,
				OpenAPISchema.class);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    	if (parserProperties.getEnabled()) {
			Set<? extends Element> exceptionHandler = roundEnv.getElementsAnnotatedWith(ExceptionHandler.class);
			if (exceptionHandler != null && !exceptionHandler.isEmpty()) {
				exceptionHandler.stream()
					.filter(element -> element instanceof ExecutableElement)
					.map(ExecutableElement.class::cast)
					.peek(exElement -> logUtils.logInfo("Parsing exception handler: %s", exElement))
					.map(ExecutableElement::getReturnType)
					.map(type -> processingUtils.removeEnclosingType(type, ResponseEntity.class)[0])
					.map(schemaUtils::createStringSchemaMap)
					.forEach(openApi.getComponents()::putAllSchemas);

				exceptionHanderReturntype = exceptionHandler.stream()
					.filter(element -> element instanceof ExecutableElement)
					.map(ExecutableElement.class::cast)
					.map(ExecutableElement::getReturnType)
					.map(type -> processingUtils.removeEnclosingType(type, ResponseEntity.class)[0])
					.findFirst()
					.orElse(null);
			}

			Set<? extends Element> openApiSchemaClasses = roundEnv.getElementsAnnotatedWith(OpenAPISchema.class);
			if (openApiSchemaClasses != null && !openApiSchemaClasses.isEmpty()) {
				openApiSchemaClasses.stream()
					.filter(element -> element instanceof TypeElement)
					.map(TypeElement.class::cast)
					.peek(typeElement -> logUtils.logInfo("Parsing annotated type: %s", typeElement))
					.map(Element::asType)
					.map(schemaUtils::createStringSchemaMap)
					.forEach(openApi.getComponents()::putAllSchemas);
			}

			annotations.stream()
				.flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
				.filter(element -> element instanceof ExecutableElement)
				.map(ExecutableElement.class::cast)
				.forEach(this::mapOperationMethod);

			openApi.getComponents().putAllSchemas(schemaUtils.parsePackages(parserProperties.getSchemaPackages()));

			if (parserProperties.getSchemaFile() != null) {
				readOpenApiFile(parserProperties)
					.ifPresent(schemaFile -> openApi.getComponents().putAllParsedSchemas(schemaFile.getComponents().getSchemas()));
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
				runPostProcessors(parserProperties, openApi);
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
		com.github.chhorz.openapi.common.annotation.OpenAPI openApiAnnotation = executableElement
			.getAnnotation(com.github.chhorz.openapi.common.annotation.OpenAPI.class);

		RequestMapping methodMapping = aliasUtils.getMappingAnnotation(executableElement);
        RequestMapping classMapping = aliasUtils.getMappingAnnotation(executableElement.getEnclosingElement());

        RequestMapping requestMapping = aliasUtils.mergeClassAndMethodMappings(classMapping, methodMapping);

        if (requestMapping != null) {

            List<String> urlPaths;
            if (0 != requestMapping.path().length) {
				urlPaths = Stream.of(requestMapping.path())
					.map(aliasUtils::prependSlash)
					.collect(toList());
            } else {
				urlPaths = Stream.of(requestMapping.value())
					.map(aliasUtils::prependSlash)
					.collect(toList());
            }

            for (String path : urlPaths) {
                logUtils.logInfo("Parsing path: %s", path);
                String cleanedPath = path;

                RequestMethod[] requestMethods = requestMapping.method();

                if (requestMethods.length == 0) {
                    logUtils.logError("No request method defined. operationId=%s", getOperationId(executableElement));
                }

                for (RequestMethod requestMethod : requestMethods) {
                    Operation operation = new Operation();
                    operation.setSummary(javaDoc.getSummary());
                    operation.setDescription(javaDoc.getDescription());
                    operation.setOperationId(getOperationId(executableElement));
                    operation.setDeprecated(executableElement.getAnnotation(Deprecated.class) != null);

                    List<ParamTag> paramTags = javaDoc.getTags(ParamTag.class);

                    operation.addParameterObjects(executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(PathVariable.class) != null)
                            .map(v -> parameterUtils.mapPathVariable(path, v, paramTags))
                            .collect(toList()));

                    List<String> removals = operation.getParameterObjects()
                            .stream()
                            .map(Parameter::getSchema)
                            .map(Schema::getPattern)
                            .filter(Objects::nonNull)
                            .collect(toList());

                    for (String string : removals) {
                        cleanedPath = cleanedPath.replace(":" + string, "");
                    }

                    operation.addParameterObjects(executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(RequestParam.class) != null)
                            .map(v -> parameterUtils.mapRequestParam(v, paramTags))
                            .collect(toList()));

                    operation.addParameterObjects(executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(RequestHeader.class) != null)
                            .map(v -> parameterUtils.mapRequestHeader(v, paramTags))
                            .collect(toList()));

                    executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(RequestBody.class) != null)
                            .findFirst()
                            .ifPresent(requestBody -> {
								com.github.chhorz.openapi.common.domain.RequestBody r = new com.github.chhorz.openapi.common.domain.RequestBody();

								javaDoc.getTags(ParamTag.class)
									.stream()
									.filter(tag -> requestBody.toString().equals(tag.getParamName()))
									.findFirst()
									.ifPresent(parameter -> r.setDescription(parameter.getParamDescription()));

								if (processingUtils.isAssignableTo(requestBody.asType(), Optional.class)) {
									r.setRequired(false);
								} else {
									RequestBody requestBodyAnnotation = requestBody.getAnnotation(RequestBody.class);
									r.setRequired(requestBodyAnnotation.required());
								}

								MediaType mediaType = schemaUtils.createMediaType(requestBody.asType());
								if (requestMapping.consumes().length == 0) {
									r.putContent("*/*", mediaType);
								} else {
									for (String consumes : requestMapping.consumes()) {
										r.putContent(consumes, mediaType);
									}
								}

								openApi.getComponents().putAllSchemas(schemaUtils.createStringSchemaMap(requestBody.asType()));

								String requestBodyKey = ComponentUtils.getKey(requestBody.asType());
								if (openApi.getComponents().getRequestBodies() != null &&
									openApi.getComponents().getRequestBodies().containsKey(requestBodyKey)) {
									RequestBodyUtils requestBodyUtils = new RequestBodyUtils(logUtils);
									openApi.getComponents().putRequestBody(requestBodyKey,
										requestBodyUtils.mergeRequestBodies(openApi.getComponents().getRequestBodies().get(requestBodyKey), r));
								} else {
									openApi.getComponents().putRequestBody(requestBodyKey, r);
								}

								operation.setRequestBodyReference(Reference.forRequestBody(ProcessingUtils.getShortName(requestBody.asType())));
							});

                    if (isClassAvailable("org.springframework.data.domain.Pageable")) {
						executableElement.getParameters()
							.stream()
							.filter(variableElement -> processingUtils.isAssignableTo(variableElement.asType(), Pageable.class))
							.findAny()
							.ifPresent(variableElement -> {
								Schema primitiveIntSchema = new Schema();
								primitiveIntSchema.setType(Schema.Type.INTEGER);
								primitiveIntSchema.setFormat(Schema.Format.INT32);

								Schema stringSchema = new Schema();
								stringSchema.setType(Schema.Type.STRING);

								// see PageableHandlerMethodArgumentResolverSupport.java
								Parameter sizeParameter = new Parameter();
								sizeParameter.setIn(Parameter.In.QUERY);
								sizeParameter.setDescription("Requested page size");
								sizeParameter.setName("size");
								sizeParameter.setRequired(false);
								sizeParameter.setDeprecated(false);
								sizeParameter.setSchema(primitiveIntSchema);

								Parameter pageParameter = new Parameter();
								pageParameter.setIn(Parameter.In.QUERY);
								pageParameter.setDescription("Requested page number");
								pageParameter.setName("page");
								pageParameter.setRequired(false);
								pageParameter.setDeprecated(false);
								pageParameter.setSchema(primitiveIntSchema);

								// see SortHandlerMethodArgumentResolverSupport.java
								Parameter sortParameter = new Parameter();
								sortParameter.setIn(Parameter.In.QUERY);
								sortParameter.setDescription("Requested sort attribute and order");
								sortParameter.setName("sort");
								sortParameter.setRequired(false);
								sortParameter.setDeprecated(false);
								sortParameter.setSchema(stringSchema);

								operation.addParameterObjects(asList(sizeParameter, pageParameter, sortParameter));
							});
					}

                    String returnTag = "";
                    List<ReturnTag> returnTags = javaDoc.getTags(ReturnTag.class);
                    if (returnTags.size() == 1) {
                        returnTag = returnTags.get(0).getDesrcription();
                    }

                    // use return type of method as default response
                    TypeMirror returnType = processingUtils.removeEnclosingType(executableElement.getReturnType(), ResponseEntity.class)[0];
                    Map<TypeMirror, Schema> schemaMap = schemaUtils.createTypeMirrorSchemaMap(returnType);
                    Map<TypeMirror, Schema> exceptionSchemaMap = schemaUtils.createTypeMirrorSchemaMap(exceptionHanderReturntype);

                    Map<TypeMirror, Schema> combinedMap = new HashMap<>(schemaMap);
                    combinedMap.putAll(exceptionSchemaMap);
					combinedMap.putAll(schemaUtils.createSchemasFromDocComment(javaDoc));
                    Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, openApiAnnotation,
						requestMapping.produces(), returnTag, combinedMap);

                    if (exceptionHanderReturntype != null && !responses.isEmpty()) {
                        // use return type of ExceptionHandler as default response
                        Schema exceptionSchema = exceptionSchemaMap.get(exceptionHanderReturntype);
                        if (Schema.Type.OBJECT.equals(exceptionSchema.getType()) || Schema.Type.ENUM.equals(exceptionSchema.getType())) {
                            operation.putDefaultResponse(responseUtils.fromTypeMirror(exceptionHanderReturntype, requestMapping.produces(), returnTag));
                        } else {
                            operation.putDefaultResponse(responseUtils.fromSchema(exceptionSchema, requestMapping.produces(), returnTag));
                            schemaMap.remove(exceptionHanderReturntype);
                        }
                    } else {
                        Schema schema = schemaMap.get(returnType);
						if (schema != null && responses.isEmpty()) {
                            if (Schema.Type.OBJECT.equals(schema.getType()) || Schema.Type.ENUM.equals(schema.getType())) {
                                operation.putDefaultResponse(responseUtils.fromTypeMirror(returnType, requestMapping.produces(), returnTag));
                            } else {
                                operation.putDefaultResponse(responseUtils.fromSchema(schema, requestMapping.produces(), returnTag));
                                schemaMap.remove(returnType);
                            }
                        }
                    }

                    openApi.getComponents().putAllSchemas(convertSchemaMap(combinedMap.entrySet()
                            .stream()
							.filter(entry -> !processingUtils.isAssignableTo(entry.getKey(), Void.class))
                            .filter(entry -> !Schema.Type.ARRAY.equals(entry.getValue().getType()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));

                    responses.forEach(operation::putResponse);

                    // according to the specification at least one response is required
                    if (operation.getResponses() == null || operation.getResponses().isEmpty()) {
                    	logUtils.logError("No response status defined for operation: %s", operation.getOperationId());
                        Response response = new Response();
                        response.setDescription("");
                        operation.putDefaultResponse(response);
                    }

					getTags(javaDoc, openApiAnnotation).forEach(operation::addTag);
					operation.setSecurity(getSecurityInformation(executableElement, openApi, javaDoc, openApiAnnotation));

                    PathItemObject pathItemObject = new PathItemObject();
                    switch (requestMethod) {
                        case GET:
                            pathItemObject.setGet(operation);
                            break;
                        case DELETE:
                            pathItemObject.setDelete(operation);
                            break;
                        case HEAD:
                            pathItemObject.setHead(operation);
                            break;
                        case OPTIONS:
                            pathItemObject.setOptions(operation);
                            break;
                        case PATCH:
                            pathItemObject.setPatch(operation);
                            break;
                        case POST:
                            pathItemObject.setPost(operation);
                            break;
                        case PUT:
                            pathItemObject.setPut(operation);
                            break;
                        case TRACE:
                            pathItemObject.setTrace(operation);
                            break;

                        default:
                            throw new RuntimeException("Unknown RequestMethod value.");
                    }

                    if (openApi.getPaths().containsKey(cleanedPath)) {
						PathItemUtils utils = new PathItemUtils(logUtils);
						openApi.putPathItemObject(cleanedPath, utils.mergePathItems(openApi.getPaths().get(cleanedPath), pathItemObject));
					} else {
						openApi.putPathItemObject(cleanedPath, pathItemObject);
					}
                }
            }
        }
    }

}
