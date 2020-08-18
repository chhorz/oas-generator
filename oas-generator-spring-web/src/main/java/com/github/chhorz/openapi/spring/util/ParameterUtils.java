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
package com.github.chhorz.openapi.spring.util;

import com.github.chhorz.javadoc.tags.ParamTag;
import com.github.chhorz.openapi.common.domain.Parameter;
import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.util.ProcessingUtils;
import com.github.chhorz.openapi.common.util.SchemaUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterUtils {
	
	private final SchemaUtils schemaUtils;
	private final ProcessingUtils processingUtils;
	private final AliasUtils aliasUtils;

	public ParameterUtils(final SchemaUtils schemaUtils, final ProcessingUtils processingUtils, final AliasUtils aliasUtils) {
		this.schemaUtils = schemaUtils;
		this.processingUtils = processingUtils;
		this.aliasUtils = aliasUtils;
	}

	public Parameter mapPathVariable(final String path, final VariableElement variableElement,
		final List<ParamTag> parameterDocs) {
		PathVariable pathVariable = variableElement.getAnnotation(PathVariable.class);

		final String name = aliasUtils.getValue(pathVariable, PathVariable::name, PathVariable::value,
			variableElement.getSimpleName().toString());

		Optional<ParamTag> parameterDescription = parameterDocs.stream()
			.filter(tag -> tag.getParamName().equalsIgnoreCase(name))
			.findFirst();

		Parameter parameter = new Parameter();
		parameter.setAllowEmptyValue(Boolean.FALSE);
		parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
		parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
		parameter.setIn(Parameter.In.PATH);
		parameter.setName(name);
		parameter.setRequired(pathVariable.required());
		if (processingUtils.isAssignableTo(variableElement.asType(), Optional.class)) {
			parameter.setRequired(false);
		}

		Map<TypeMirror, Schema> map = schemaUtils.mapTypeMirrorToSchema(variableElement.asType());
		Schema schema = map.get(variableElement.asType());

		getRegularExpression(path, name).ifPresent(schema::setPattern);

		parameter.setSchema(schema);

		return parameter;
	}

	public Parameter mapRequestParam(final VariableElement variableElement, final List<ParamTag> parameterDocs) {
		RequestParam requestParam = variableElement.getAnnotation(RequestParam.class);

		final String name = aliasUtils.getValue(requestParam, RequestParam::name, RequestParam::value,
			variableElement.getSimpleName().toString());

		Optional<ParamTag> parameterDescription = parameterDocs.stream()
			.filter(tag -> tag.getParamName().equalsIgnoreCase(name))
			.findFirst();

		Parameter parameter = new Parameter();
		parameter.setAllowEmptyValue(!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue()));
		parameter.setDeprecated(variableElement.getAnnotation(Deprecated.class) != null);
		parameter.setDescription(parameterDescription.isPresent() ? parameterDescription.get().getParamDescription() : "");
		parameter.setIn(Parameter.In.QUERY);
		parameter.setName(name);
		parameter.setRequired(requestParam.required());
		if (processingUtils.isAssignableTo(variableElement.asType(), Optional.class)) {
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

	public Parameter mapRequestHeader(final VariableElement variableElement, final List<ParamTag> parameterDocs) {
		RequestHeader requestHeader = variableElement.getAnnotation(RequestHeader.class);

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
		parameter.setIn(Parameter.In.HEADER);
		parameter.setName(name);
		parameter.setRequired(requestHeader.required());
		if (processingUtils.isAssignableTo(variableElement.asType(), Optional.class)) {
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
