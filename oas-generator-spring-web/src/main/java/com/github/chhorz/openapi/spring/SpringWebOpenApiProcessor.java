package com.github.chhorz.openapi.spring;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.tags.CategoryTag;
import com.github.chhorz.javadoc.tags.ParamTag;
import com.github.chhorz.javadoc.tags.ReturnTag;
import com.github.chhorz.openapi.common.OpenAPIProcessor;
import com.github.chhorz.openapi.common.domain.MediaType;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.Parameter;
import com.github.chhorz.openapi.common.domain.Parameter.In;
import com.github.chhorz.openapi.common.domain.PathItemObject;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Responses;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.domain.SecurityScheme;
import com.github.chhorz.openapi.common.javadoc.ResponseTag;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.LoggingUtils;
import com.github.chhorz.openapi.common.util.ReferenceUtils;
import com.github.chhorz.openapi.common.util.ResponseUtils;
import com.github.chhorz.openapi.common.util.SchemaUtils;
import com.github.chhorz.openapi.common.util.TagUtils;
import com.github.chhorz.openapi.common.util.TypeMirrorUtils;
import com.github.chhorz.openapi.spring.util.AliasUtils;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
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
		responseUtils = new ResponseUtils(elements);

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
	public Set<String> getSupportedOptions() {
		return getDocGeneratorOptions();
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
				// .peek(e -> System.out.println(e))
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

		writeOpenApiFile(parserProperties, openApi);

		return false;
	}

	private void mapOperationMethod(final ExecutableElement executableElement) {
		log.debug("Parsing method: %s#%s", executableElement.getEnclosingElement().getSimpleName(), executableElement);

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

				for (RequestMethod requestMethod : requestMethods) {

					Operation operation = new Operation();
					operation.setSummary(javaDoc.getSummary());
					operation.setDescription(javaDoc.getDescription());
					operation.setOperationId(String.format("%s#%s", executableElement.getEnclosingElement().getSimpleName(),
							executableElement.getSimpleName()));
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

						Optional<ParamTag> optionalParameter = javaDoc.getTags(ParamTag.class)
								.stream()
								.filter(tag -> requestBody.toString().equals(tag.getParamName()))
								.findFirst();
						if (optionalParameter.isPresent()) {
							r.setDescription(optionalParameter.get().getParamDescription());
						} else {
							r.setDescription("");
						}

						r.setRequired(Boolean.TRUE);

						for (String consumes : requestMapping.consumes()) {
							MediaType mediaType = new MediaType();
							mediaType.setSchema(ReferenceUtils.createSchemaReference(requestBody.asType()));

							r.putContent(consumes, mediaType);
						}

						openApi.getComponents().putAllSchemas(schemaUtils.mapTypeMirrorToSchema(requestBody.asType()));
						openApi.getComponents().putRequestBody(requestBody.asType().toString(), r);

						operation.setRequestBodyReference(ReferenceUtils.createRequestBodyReference(requestBody.asType()));
					}

					String returnTag = "";
					List<ReturnTag> returnTags = javaDoc.getTags(ReturnTag.class);
					if (returnTags.size() == 1) {
						returnTag = returnTags.get(0).getDesrcription();
					}

					Responses responses = responseUtils.initializeFromJavadoc(javaDoc, requestMapping.produces());

					// use return type of method as default response
					TypeMirror returnType = typeMirrorUtils.removeEnclosingType(executableElement.getReturnType(),
							ResponseEntity.class)[0];
					Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(returnType);

					if (exceptionHanderReturntype != null) {
						// use return type of ExcheptionHandler as default response
						Map<TypeMirror, Schema> exceptionSchemaMap = schemaUtils.mapTypeMirrorToSchema(exceptionHanderReturntype);
						Schema exceptionSchema = exceptionSchemaMap.get(exceptionHanderReturntype);
						if (Type.OBJECT.equals(exceptionSchema.getType()) || Type.ENUM.equals(exceptionSchema.getType())) {
							Response response = responseUtils.mapTypeMirrorToResponse(exceptionHanderReturntype,
									requestMapping.produces());
							response.setDescription(returnTag);
							responses.setDefaultResponse(response);
						} else {
							Response response = responseUtils.mapSchemaToResponse(exceptionSchema, requestMapping.produces());
							response.setDescription(returnTag);
							responses.setDefaultResponse(response);
							schemaMap.remove(exceptionHanderReturntype);
						}
					} else {

						Schema schema = schemaMap.get(returnType);
						if (Type.OBJECT.equals(schema.getType()) || Type.ENUM.equals(schema.getType())) {
							Response response = responseUtils.mapTypeMirrorToResponse(returnType, requestMapping.produces());
							response.setDescription(returnTag);
							responses.setDefaultResponse(response);
						} else {
							Response response = responseUtils.mapSchemaToResponse(schema, requestMapping.produces());
							response.setDescription(returnTag);
							responses.setDefaultResponse(response);
							schemaMap.remove(returnType);
						}

					}

					openApi.getComponents().putAllSchemas(schemaMap);

					operation.setResponses(responses);

					javaDoc.getTags(CategoryTag.class).stream()
							.map(CategoryTag::getCategoryName)
							.forEach(operation::addTag);

					Map<String, List<String>> securityInformation = getSecurityInformation(executableElement,
							openApi.getComponents().getSecuritySchemes());
					operation.setSecurity(Arrays.asList(securityInformation));

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

	private Map<String, List<String>> getSecurityInformation(final ExecutableElement executableElement, final Map<String, SecurityScheme> map) {
		Map<String, List<String>> securityInformation = new TreeMap<>();

		for (AnnotationMirror annotation : executableElement.getAnnotationMirrors()) {
			log.info("Annotation: %s", annotation);
			if (annotation.getAnnotationType().toString().equalsIgnoreCase(
					"org.springframework.security.access.prepost.PreAuthorize")) {
				log.info("PreAuthorize");
				PreAuthorize preAuthorized = executableElement.getAnnotation(PreAuthorize.class);
				map.entrySet().stream()
						.filter(entry -> preAuthorized.value().toLowerCase().contains(entry.getKey().toLowerCase()))
						.forEach(entry -> {
							log.info("Entry: %s", entry);
							securityInformation.put(entry.getKey(), new ArrayList<>());
						});
			}
		}

		return securityInformation;
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
		parameter.setRequired(Boolean.TRUE);

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
