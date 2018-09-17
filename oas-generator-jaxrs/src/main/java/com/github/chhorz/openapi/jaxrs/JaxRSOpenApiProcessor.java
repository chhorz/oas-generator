package com.github.chhorz.openapi.jaxrs;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.security.access.prepost.PreAuthorize;

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
import com.github.chhorz.openapi.common.domain.RequestBody;
import com.github.chhorz.openapi.common.domain.Response;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.Schema.Type;
import com.github.chhorz.openapi.common.domain.SecurityScheme;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.LoggingUtils;
import com.github.chhorz.openapi.common.util.ReferenceUtils;
import com.github.chhorz.openapi.common.util.ResponseUtils;
import com.github.chhorz.openapi.common.util.SchemaUtils;
import com.github.chhorz.openapi.common.util.TagUtils;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JaxRSOpenApiProcessor extends AbstractProcessor implements OpenAPIProcessor {

	private Elements elements;
	private Types types;

	private GeneratorPropertyLoader propertyLoader;
	private ParserProperties parserProperties;

	private LoggingUtils log;
	private SchemaUtils schemaUtils;
	private ResponseUtils responseUtils;

	private JavaDocParser javaDocParser;

	private OpenAPI openApi;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		elements = processingEnv.getElementUtils();
		types = processingEnv.getTypeUtils();

		propertyLoader = new GeneratorPropertyLoader(processingEnv.getOptions());
		parserProperties = propertyLoader.getParserProperties();

		log = new LoggingUtils(parserProperties);
		schemaUtils = new SchemaUtils(elements, types, log);
		responseUtils = new ResponseUtils(elements, types, log);

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
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
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
		operation.setOperationId(String.format("%s#%s", executableElement.getEnclosingElement().getSimpleName(),
				executableElement.getSimpleName()));
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

			Consumes consumes = executableElement.getAnnotation(Consumes.class);
			if (consumes != null) {
				for (String c : consumes.value()) {
					MediaType mediaType = new MediaType();
					mediaType.setSchema(ReferenceUtils.createSchemaReference(requestBody.asType()));

					r.putContent(c, mediaType);
				}
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

		TypeMirror returnType = executableElement.getReturnType();
		Map<TypeMirror, Schema> schemaMap = schemaUtils.mapTypeMirrorToSchema(returnType);
		Schema schema = schemaMap.get(returnType);

		Produces produces = executableElement.getAnnotation(Produces.class);
		if (produces != null) {
			if (Type.OBJECT.equals(schema.getType()) || Type.ENUM.equals(schema.getType())) {
				Response response = responseUtils.mapTypeMirrorToResponse(returnType, produces.value());
				response.setDescription(returnTag);
				operation.putDefaultResponse(response);
			} else {
				Response response = responseUtils.mapSchemaToResponse(schema, produces.value());
				response.setDescription(returnTag);
				operation.putDefaultResponse(response);
				schemaMap.remove(returnType);
			}
		}

		openApi.getComponents().putAllSchemas(schemaMap);

		javaDoc.getTags(CategoryTag.class).stream()
				.map(CategoryTag::getCategoryName)
				.forEach(operation::addTag);

		Map<String, List<String>> securityInformation = getSecurityInformation(executableElement,
				openApi.getComponents().getSecuritySchemes());
		operation.setSecurity(Arrays.asList(securityInformation));

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
