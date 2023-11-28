/**
 *
 * Copyright 2018-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.chhorz.openapi.common.spi.mapping;

import com.github.chhorz.openapi.common.domain.Schema;
import com.github.chhorz.openapi.common.properties.domain.ParserProperties;
import com.github.chhorz.openapi.common.util.LogUtils;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;

/**
 * Interface for mapper of java types to OpenAPI schema
 *
 * @author chhorz
 */
public interface TypeMirrorMapper {

	/**
	 * Constructor method that will be used to fill custom classes.
	 *
	 * @param elements
	 * @param types
	 * @param logUtils
	 * @param parserProperties
	 * @param typeMirrorMapper
	 */
	void setup(Elements elements, Types types, LogUtils logUtils, ParserProperties parserProperties, List<TypeMirrorMapper> typeMirrorMapper);

	/**
	 * Test method to check if the mapper supports the current type mirror.
	 *
	 * @param typeMirror the current java type
	 * @return {@code true} if the mapper can consume the type, {@code false} otherwise
	 */
	boolean test(TypeMirror typeMirror);

	/**
	 * Mapping method to map the java type to a map of OpenAPI schema.
	 *
	 * @param typeMirror the java type
	 * @param parsedSchemaMap the OpenAPI schema map tha was already parsed
	 * @return the OpenAPI schema map
	 */
	Map<TypeMirror, Schema> map(TypeMirror typeMirror, Map<TypeMirror, Schema> parsedSchemaMap);

}
