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
import com.github.chhorz.openapi.spring.util.AliasUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class SpringWebOpenApiProcessor extends AbstractProcessor implements OpenAPIProcessor {

    private Elements elements;
    private Types types;

    private GeneratorPropertyLoader propertyLoader;
    private ParserProperties parserProperties;

    private LoggingUtils log;
    private SchemaUtils schemaUtils;
    private TypeMirrorUtils typeMirrorUtils;
    private ResponseUtils responseUtils;

    private JavaDocParser javaDocParser;

    private OpenAPI openApi;

    private TypeMirror exceptionHanderReturntype = null;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();

        // initialize property loader
        propertyLoader = new GeneratorPropertyLoader(processingEnv.getOptions());
        parserProperties = propertyLoader.getParserProperties();

        log = new LoggingUtils(parserProperties);
        schemaUtils = new SchemaUtils(elements, types, log);
        typeMirrorUtils = new TypeMirrorUtils(elements, types);
        responseUtils = new ResponseUtils(elements, types, log);

        javaDocParser = createJavadocParser();

        openApi = initializeFromProperties(propertyLoader);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(RequestMapping.class,
			GetMapping.class,
			PostMapping.class,
			PutMapping.class,
			DeleteMapping.class,
			PatchMapping.class,
			ExceptionHandler.class)
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
		Set<? extends Element> exceptionHandler = roundEnv.getElementsAnnotatedWith(ExceptionHandler.class);
		if (exceptionHandler != null && !exceptionHandler.isEmpty()) {
			exceptionHandler.stream()
				.filter(element -> element instanceof ExecutableElement)
				.map(ExecutableElement.class::cast)
				.peek(exElement -> log.info("Parsing exception handler: %s", exElement))
				.map(ExecutableElement::getReturnType)
				.map(type -> typeMirrorUtils.removeEnclosingType(type, ResponseEntity.class)[0])
				.map(schemaUtils::mapTypeMirrorToSchema)
				.forEach(openApi.getComponents()::putAllSchemas);

			exceptionHanderReturntype = exceptionHandler.stream()
				.filter(element -> element instanceof ExecutableElement)
				.map(ExecutableElement.class::cast)
                    .map(ExecutableElement::getReturnType)
                    .map(type -> typeMirrorUtils.removeEnclosingType(type, ResponseEntity.class)[0])
                    .findFirst()
                    .orElse(null);
        }

        annotations.stream()
                .flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream())
                .filter(element -> element instanceof ExecutableElement)
                .map(ExecutableElement.class::cast)
                .forEach(this::mapOperationMethod);

        Map<TypeMirror, Schema> schemaMap = schemaUtils.parsePackages(parserProperties.getSchemaPackages());
        openApi.getComponents().putAllSchemas(schemaMap);

        if (parserProperties.getSchemaFile() != null) {
            readOpenApiFile(parserProperties).ifPresent(schemaFile -> openApi.getComponents().putAllParsedSchemas(schemaFile.getComponents().getSchemas()));
        }

        TagUtils tagUtils = new TagUtils(propertyLoader);
        List<String> tags = new ArrayList<>();
        openApi.getPaths()
                .values()
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

		return false;
	}

    private void mapOperationMethod(final ExecutableElement executableElement) {
        log.debug("Parsing method: %s", getOperationId(executableElement));

        JavaDoc javaDoc = javaDocParser.parse(elements.getDocComment(executableElement));

        AliasUtils<?> aliasUtils = new AliasUtils<>();
        RequestMapping methodMapping = aliasUtils.getMappingAnnotation(executableElement);
        RequestMapping classMapping = aliasUtils.getMappingAnnotation(executableElement.getEnclosingElement());

        RequestMapping requestMapping = aliasUtils.mergeClassAndMethodMappings(classMapping, methodMapping);

        if (requestMapping != null) {

            String[] urlPaths;
            if (0 != requestMapping.path().length) {
                urlPaths = requestMapping.path();
            } else {
                urlPaths = requestMapping.value();
            }

            for (String path : urlPaths) {
                log.info("Parsing path: %s", path);
                String cleanedPath = path;

                RequestMethod[] requestMethods = requestMapping.method();

                if (requestMethods.length == 0) {
                    log.error("No request method defined. operationId=%s", getOperationId(executableElement));
                }

                for (RequestMethod requestMethod : requestMethods) {
                    Operation operation = new Operation();
                    operation.setSummary(javaDoc.getSummary());
                    operation.setDescription(javaDoc.getDescription());
                    operation.setOperationId(getOperationId(executableElement));
                    operation.setDeprecated(executableElement.getAnnotation(Deprecated.class) != null);

                    List<ParamTag> tags = javaDoc.getTags(ParamTag.class);

                    operation.addParameterObjects(executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(PathVariable.class) != null)
                            .map(v -> mapPathVariable(path, v, tags))
                            .collect(toList()));

                    List<String> removals = operation.getParameterObjects()
                            .stream()
                            .map(Parameter::getSchema)
                            .filter(schema -> schema.getPattern() != null)
                            .map(Schema::getPattern)
                            .collect(toList());

                    for (String string : removals) {
                        cleanedPath = cleanedPath.replace(":" + string, "");
                    }

                    operation.addParameterObjects(executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(RequestParam.class) != null)
                            .map(v -> mapRequestParam(v, tags))
                            .collect(toList()));

                    operation.addParameterObjects(executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(RequestHeader.class) != null)
                            .map(v -> mapRequestHeader(v, tags))
                            .collect(toList()));

                    VariableElement requestBody = executableElement.getParameters()
                            .stream()
                            .filter(variableElement -> variableElement.getAnnotation(RequestBody.class) != null)
                            .findFirst()
                            .orElse(null);

                    if (requestBody != null) {
						com.github.chhorz.openapi.common.domain.RequestBody r = new com.github.chhorz.openapi.common.domain.RequestBody();

						javaDoc.getTags(ParamTag.class)
							.stream()
							.filter(tag -> requestBody.toString().equals(tag.getParamName()))
							.findFirst()
							.ifPresent(parameter -> r.setDescription(parameter.getParamDescription()));

						r.setRequired(Boolean.TRUE);

						MediaType mediaType = schemaUtils.createMediaType(requestBody.asType());
						if (requestMapping.consumes().length == 0) {
							r.putContent("*/*", mediaType);
						} else {
							for (String consumes : requestMapping.consumes()) {
								r.putContent(consumes, mediaType);
							}
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

                    // use return type of method as default response
                    TypeMirror returnType = typeMirrorUtils.removeEnclosingType(executableElement.getReturnType(), ResponseEntity.class)[0];
                    Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(returnType);
                    Map<TypeMirror, Schema> exceptionSchemaMap = schemaUtils.mapTypeMirrorToSchema(exceptionHanderReturntype);

                    Map<TypeMirror, Schema> combinedMap = new HashMap<>(schemaMap);
                    combinedMap.putAll(exceptionSchemaMap);
                    Map<String, Response> responses = responseUtils.initializeFromJavadoc(javaDoc, requestMapping.produces(), returnTag, combinedMap);

                    if (exceptionHanderReturntype != null && !responses.isEmpty()) {
                        // use return type of ExceptionHandler as default response
                        Schema exceptionSchema = exceptionSchemaMap.get(exceptionHanderReturntype);
                        if (Type.OBJECT.equals(exceptionSchema.getType()) || Type.ENUM.equals(exceptionSchema.getType())) {
                            operation.putDefaultResponse(responseUtils.fromTypeMirror(exceptionHanderReturntype, requestMapping.produces(), returnTag));
                        } else {
                            operation.putDefaultResponse(responseUtils.fromSchema(exceptionSchema, requestMapping.produces(), returnTag));
                            schemaMap.remove(exceptionHanderReturntype);
                        }
                    } else {
                        Schema schema = schemaMap.get(returnType);
                        if (schema != null) {
                            if (Type.OBJECT.equals(schema.getType()) || Type.ENUM.equals(schema.getType())) {
                                operation.putDefaultResponse(responseUtils.fromTypeMirror(returnType, requestMapping.produces(), returnTag));
                            } else {
                                operation.putDefaultResponse(responseUtils.fromSchema(schema, requestMapping.produces(), returnTag));
                                schemaMap.remove(returnType);
                            }
                        }
                    }

                    openApi.getComponents().putAllSchemas(schemaMap.entrySet()
                            .stream()
                            .filter(entry -> !Type.ARRAY.equals(entry.getValue().getType()))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

                    responses.forEach(operation::putResponse);

                    if (operation.getResponses() == null || operation.getResponses().isEmpty()) {
                        Response response = new Response();
                        response.setDescription("");
                        operation.putDefaultResponse(response);
                    }

                    javaDoc.getTags(CategoryTag.class).stream()
                            .map(CategoryTag::getCategoryName)
                            .forEach(operation::addTag);

					operation.setSecurity(getSecurityInformation(executableElement, openApi.getComponents().getSecuritySchemes(), javaDoc.getTags(SecurityTag.class)));

                    PathItemObject pathItemObject = openApi.getPaths().getOrDefault(cleanedPath, new PathItemObject());
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

                    openApi.putPathItemObject(cleanedPath, pathItemObject);
                }
            }
        }
    }

    private Parameter mapPathVariable(final String path, final VariableElement variableElement,
                                      final List<ParamTag> parameterDocs) {
        PathVariable pathVariable = variableElement.getAnnotation(PathVariable.class);

        AliasUtils<PathVariable> aliasUtils = new AliasUtils<>();
        final String name = aliasUtils.getValue(pathVariable, PathVariable::name, PathVariable::value,
                variableElement.getSimpleName().toString());

        Optional<ParamTag> parameterDescription = parameterDocs.stream()
                .filter(tag -> tag.getParamName().equalsIgnoreCase(name))
                .findFirst();

        Parameter parameter = new Parameter();
        parameter.setAllowEmptyValue(Boolean.FALSE);
        parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
        parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
        parameter.setIn(In.PATH);
        parameter.setName(name);
        parameter.setRequired(pathVariable.required());
		if (schemaUtils.isAssignableFrom(elements, types, variableElement.asType(), Optional.class)) {
			parameter.setRequired(false);
		}

        Map<TypeMirror, Schema> map = schemaUtils.mapTypeMirrorToSchema(variableElement.asType());
        Schema schema = map.get(variableElement.asType());

        getRegularExpression(path, name).ifPresent(schema::setPattern);

        parameter.setSchema(schema);

        return parameter;
    }

    private Parameter mapRequestParam(final VariableElement variableElement, final List<ParamTag> parameterDocs) {
        RequestParam requestParam = variableElement.getAnnotation(RequestParam.class);

        AliasUtils<RequestParam> aliasUtils = new AliasUtils<>();
        final String name = aliasUtils.getValue(requestParam, RequestParam::name, RequestParam::value,
                variableElement.getSimpleName().toString());

        Optional<ParamTag> parameterDescription = parameterDocs.stream()
                .filter(tag -> tag.getParamName().equalsIgnoreCase(name))
                .findFirst();

        Parameter parameter = new Parameter();
        parameter.setAllowEmptyValue(!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()));
        parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
        parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
        parameter.setIn(In.QUERY);
        parameter.setName(name);
        parameter.setRequired(requestParam.required());
        if (schemaUtils.isAssignableFrom(elements, types, variableElement.asType(), Optional.class)) {
        	parameter.setRequired(false);
		}

        Schema schema = schemaUtils.mapTypeMirrorToSchema(variableElement.asType())
                .get(variableElement.asType());
        if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
            schema.setDefaultValue(requestParam.defaultValue());
        }
        parameter.setSchema(schema);

        return parameter;
    }

    private Parameter mapRequestHeader(final VariableElement variableElement, final List<ParamTag> parameterDocs) {
        RequestHeader requestHeader = variableElement.getAnnotation(RequestHeader.class);

        AliasUtils<RequestHeader> aliasUtils = new AliasUtils<>();
        final String name = aliasUtils.getValue(requestHeader, RequestHeader::name, RequestHeader::value,
                variableElement.getSimpleName().toString());

        Optional<ParamTag> parameterDescription = parameterDocs.stream()
                .filter(tag -> tag.getParamName().equalsIgnoreCase(name))
                .findFirst();

        // TODO handle MultiValueMap

        Parameter parameter = new Parameter();
        parameter.setAllowEmptyValue(!ValueConstants.DEFAULT_NONE.equals(requestHeader.defaultValue()));
        parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
        parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
        parameter.setIn(In.HEADER);
        parameter.setName(name);
        parameter.setRequired(requestHeader.required());
		if (schemaUtils.isAssignableFrom(elements, types, variableElement.asType(), Optional.class)) {
			parameter.setRequired(false);
		}

        Schema schema = schemaUtils.mapTypeMirrorToSchema(variableElement.asType())
                .get(variableElement.asType());
        if (!ValueConstants.DEFAULT_NONE.equals(requestHeader.defaultValue())) {
            schema.setDefaultValue(requestHeader.defaultValue());
        }
        parameter.setSchema(schema);

        return parameter;
    }

    private Optional<String> getRegularExpression(final String path, final String pathVariable) {
        Pattern pathVariablePattern = Pattern.compile(".*\\{" + pathVariable + ":([^{}]+)}.*");
        Matcher pathVariableMatcher = pathVariablePattern.matcher(path);
        if (pathVariableMatcher.matches()) {
            return Optional.ofNullable(pathVariableMatcher.group(1));
        } else {
            return Optional.empty();
        }
    }

}
