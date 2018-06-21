package com.github.chhorz.openapi.jaxrs;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
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
import javax.ws.rs.QueryParam;

import org.springframework.security.access.prepost.PreAuthorize;

import com.github.chhorz.javadoc.JavaDoc;
import com.github.chhorz.javadoc.JavaDocParser;
import com.github.chhorz.javadoc.JavaDocParserBuilder;
import com.github.chhorz.javadoc.OutputType;
import com.github.chhorz.javadoc.tags.CategoryTag;
import com.github.chhorz.javadoc.tags.ParamTag;
import com.github.chhorz.openapi.common.OpenAPIProcessor;
import com.github.chhorz.openapi.common.domain.OpenAPI;
import com.github.chhorz.openapi.common.domain.Operation;
import com.github.chhorz.openapi.common.domain.Parameter;
import com.github.chhorz.openapi.common.domain.PathItemObject;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.domain.SecurityScheme;
import com.github.chhorz.openapi.common.domain.Parameter.In;
import com.github.chhorz.openapi.common.properties.GeneratorPropertyLoader;
import com.github.chhorz.openapi.common.properties.ParserProperties;
import com.github.chhorz.openapi.common.util.LoggingUtils;
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

	private OpenAPI openApi;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		elements = processingEnv.getElementUtils();
		types = processingEnv.getTypeUtils();

		propertyLoader = new GeneratorPropertyLoader(processingEnv.getOptions());
		parserProperties = propertyLoader.getParserProperties();

		log = new LoggingUtils(parserProperties);
		schemaUtils = new SchemaUtils(elements, types, log);

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
			Optional<OpenAPI> schemaFile = readOpenApiFile(parserProperties);
			if (schemaFile.isPresent()) {
				openApi.getComponents().putAllParsedSchemas(schemaFile.get().getComponents().getSchemas());
			}
		}

		TagUtils tagUtils = new TagUtils(propertyLoader);
		List<String> tags = new ArrayList<>();
		openApi.getPaths()
				.values()
				.stream()
				.map(tagUtils::getAllTags)
				.flatMap(tagList -> tagList.stream())
				.forEach(tag -> tags.add(tag));

		tags.stream()
				.distinct()
				.map(tagUtils::createTag)
				.forEach(openApi::addTag);

		writeOpenApiFile(parserProperties, openApi);

		return false;
	}

	private void mapOperationMethod(final ExecutableElement executableElement) {
		log.debug("Parsing method: %s#%s", executableElement.getEnclosingElement().getSimpleName(), executableElement);

		JavaDocParser parser = JavaDocParserBuilder.withBasicTags().withOutputType(OutputType.HTML).build();
		JavaDoc javaDoc = parser.parse(elements.getDocComment(executableElement));

		Path classPath = executableElement.getEnclosingElement().getAnnotation(Path.class);
		Path methodPath = executableElement.getAnnotation(Path.class);

		StringBuilder sb = new StringBuilder();
		if (classPath != null && classPath.value() != null) {
			sb.append(classPath.value());
		}
		if (methodPath != null && methodPath.value() != null) {
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

		// ...

		javaDoc.getTags(CategoryTag.class).stream()
				.map(CategoryTag::getCategoryName)
				.forEach(tag -> operation.addTag(tag));

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
